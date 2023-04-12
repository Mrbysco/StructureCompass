package com.mrbysco.structurecompass.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.client.screen.widget.StructureListWidget;
import com.mrbysco.structurecompass.network.PacketHandler;
import com.mrbysco.structurecompass.network.message.SetStructureMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompassScreen extends Screen {
	private enum SortType {
		NORMAL,
		A_TO_Z,
		Z_TO_A;

		Button button;

		Component getButtonText() {
			return Component.translatable("structurecompass.screen.search." + name().toLowerCase(Locale.ROOT));
		}
	}

	private static final int PADDING = 6;

	private StructureListWidget structureWidget;
	private StructureListWidget.ListEntry selected = null;
	private int listWidth;
	private List<ResourceLocation> structures;
	private final List<ResourceLocation> unsortedStructures;
	private Button loadButton;

	private final InteractionHand usedHand;
	private final ItemStack compassStack;

	private final int buttonMargin = 1;
	private final int numButtons = SortType.values().length;
	private String lastFilterText = "";

	private EditBox search;
	private boolean sorted = false;
	private SortType sortType = SortType.NORMAL;

	public CompassScreen(InteractionHand hand, ItemStack compass, List<ResourceLocation> allStructures) {
		super(Component.translatable(Reference.MOD_PREFIX + "compass.screen"));
		this.usedHand = hand;
		this.compassStack = compass;

		List<ResourceLocation> structureList = new ArrayList<>();
		for (ResourceLocation id : allStructures) {
			if (id != null) {
				structureList.add(id);
			}
		}
		if (ModList.get().isLoaded("gamestages")) {
			structureList.removeIf((location) -> !com.mrbysco.structurecompass.compat.gamestages.GameStagesHelper.doesPlayerHaveRequiredStage(location));
		}
		Collections.sort(structureList);

		this.structures = Collections.unmodifiableList(structureList);
		this.unsortedStructures = Collections.unmodifiableList(allStructures);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		int centerWidth = this.width / 2;
		for (ResourceLocation structureLocation : structures) {
			listWidth = Math.max(listWidth, getFontRenderer().width(structureLocation.toString()) + 10);
		}
		listWidth = Math.max(Math.min(listWidth, width / 3), 200);
		listWidth += listWidth % numButtons != 0 ? (numButtons - listWidth % numButtons) : 0;
		int structureWidth = this.width - this.listWidth - (PADDING * 3);
		int closeButtonWidth = Math.min(structureWidth, 200);
		int y = this.height - 20 - PADDING;
		this.addRenderableWidget(new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				Component.translatable("gui.cancel"), b -> CompassScreen.this.onClose()));

		y -= 18 + PADDING;
		this.addRenderableWidget(this.loadButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				Component.translatable("structurecompass.screen.selection.select"), b -> {
			if (selected != null) {
				PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new SetStructureMessage(usedHand, selected.getStructureLocation()));
			}
		}));

		y -= 14 + PADDING;
		search = new EditBox(getFontRenderer(), centerWidth - listWidth / 2 + PADDING + 1, y, listWidth - 2, 14,
				Component.translatable("structurecompass.screen.search"));

		int fullButtonHeight = PADDING + 20 + PADDING;
		this.structureWidget = new StructureListWidget(this, width, fullButtonHeight, search.y - getFontRenderer().lineHeight - PADDING);
		this.structureWidget.setLeftPos(0);

		addWidget(search);
		addWidget(structureWidget);
		search.setFocus(false);
		search.setCanLoseFocus(true);
		if (this.compassStack.hasTag() && this.compassStack.getTag().contains(Reference.structure_tag)) {
			String structure = this.compassStack.getTag().getString(Reference.structure_tag);
			search.setValue(structure);
		}

		final int width = listWidth / numButtons;
		int x = centerWidth + PADDING - width;
		addRenderableWidget(SortType.A_TO_Z.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.A_TO_Z.getButtonText(), b -> resortStructures(SortType.A_TO_Z)));
		x += width + buttonMargin;
		addRenderableWidget(SortType.Z_TO_A.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.Z_TO_A.getButtonText(), b -> resortStructures(SortType.Z_TO_A)));

		resortStructures(SortType.A_TO_Z);
		updateCache();
	}

	@Override
	public void tick() {
		search.tick();
		structureWidget.setSelected(selected);

		if (!search.getValue().equals(lastFilterText)) {
			reloadStructures();
			sorted = false;
		}

		if (!sorted) {
			reloadStructures();
			if (sortType == SortType.A_TO_Z) {
				Collections.sort(structures);
			} else if (sortType == SortType.Z_TO_A) {
				structures.sort(Collections.reverseOrder());
			}
			checkStages();
			structureWidget.refreshList();
			if (selected != null) {
				selected = structureWidget.children().stream().filter(e -> e == selected).findFirst().orElse(null);
				updateCache();
			}
			sorted = true;
		}
	}

	public <T extends ObjectSelectionList.Entry<T>> void buildStructureList(Consumer<T> ListViewConsumer, Function<ResourceLocation, T> newEntry) {
		structures.forEach(mod -> ListViewConsumer.accept(newEntry.apply(mod)));
	}

	private void reloadStructures() {
		this.structures = this.unsortedStructures.stream().
				filter(struc -> StringUtils.toLowerCase(struc.toString()).contains(StringUtils.toLowerCase(search.getValue()))).collect(Collectors.toList());
		checkStages();
		lastFilterText = search.getValue();
	}

	private void checkStages() {
		if (ModList.get().isLoaded("gamestages")) {
			this.structures.removeIf((location) -> !com.mrbysco.structurecompass.compat.gamestages.GameStagesHelper.doesPlayerHaveRequiredStage(location));
		}
	}

	private void resortStructures(SortType newSort) {
		this.sortType = newSort;

		for (SortType sort : SortType.values()) {
			if (sort.button != null)
				sort.button.active = sortType != sort;
		}
		sorted = false;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.structureWidget.render(poseStack, mouseX, mouseY, partialTicks);

		Component text = Component.translatable("structurecompass.screen.search");
		drawCenteredString(poseStack, getFontRenderer(), text, this.width / 2 + PADDING,
				search.y - getFontRenderer().lineHeight - 2, 0xFFFFFF);

		this.search.render(poseStack, mouseX, mouseY, partialTicks);

		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	public Font getFontRenderer() {
		return font;
	}

	public void setSelected(StructureListWidget.ListEntry entry) {
		this.selected = entry == this.selected ? null : entry;
		updateCache();
	}

	private void updateCache() {
		if (selected == null) {
			this.loadButton.active = false;
			return;
		} else {
			this.loadButton.active = true;
		}
	}

	/**
	 * Clear the search field when right-clicked on it
	 */
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean flag = super.mouseClicked(mouseX, mouseY, button);
		if (button == 1 && search.isMouseOver(mouseX, mouseY)) {
			search.setValue("");
		}
		return flag;
	}

	@Override
	public void resize(Minecraft mc, int width, int height) {
		String s = this.search.getValue();
		SortType sort = this.sortType;
		StructureListWidget.ListEntry selected = this.selected;
		this.init(mc, width, height);
		this.search.setValue(s);
		this.selected = selected;
		if (!this.search.getValue().isEmpty())
			reloadStructures();
		if (sort != SortType.NORMAL)
			resortStructures(sort);
		updateCache();
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(null);
	}
}
