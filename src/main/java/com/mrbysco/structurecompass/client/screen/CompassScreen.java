package com.mrbysco.structurecompass.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.client.screen.widget.StructureListWidget;
import com.mrbysco.structurecompass.compat.gamestages.GameStagesHelper;
import com.mrbysco.structurecompass.network.PacketHandler;
import com.mrbysco.structurecompass.network.message.SetStructureMessage;
import com.mrbysco.structurecompass.util.StructureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.network.PacketDistributor;

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

		ITextComponent getButtonText() {
			return new TranslationTextComponent("structurecompass.screen.search." + name().toLowerCase(Locale.ROOT));
		}
	}

	private static final int PADDING = 6;

	private StructureListWidget structureWidget;
	private StructureListWidget.ListEntry selected = null;
	private int listWidth;
	private List<ResourceLocation> structures;
	private final List<ResourceLocation> unsortedStructures;
	private Button loadButton;

	private final PlayerEntity editingPlayer;
	private final Hand usedHand;
	private final ItemStack compassStack;

	private int buttonMargin = 1;
	private int numButtons = SortType.values().length;
	private String lastFilterText = "";

	private TextFieldWidget search;
	private boolean sorted = false;
	private SortType sortType = SortType.NORMAL;

	public CompassScreen(PlayerEntity player, Hand hand, ItemStack compass) {
		super(new TranslationTextComponent(Reference.MOD_PREFIX + "compass.screen"));
		this.editingPlayer = player;
		this.usedHand = hand;
		this.compassStack = compass;

		List<ResourceLocation> allStructures = StructureUtil.getAvailableStructureList();
		List<ResourceLocation> structureList = new ArrayList<>();
		for(ResourceLocation id : allStructures) {
			if(id != null) {
				structureList.add(id);
			}
		}
		if(ModList.get().isLoaded("gamestages")) {
			structureList.removeIf((location) -> !GameStagesHelper.doesPlayerHaveRequiredStage(this.editingPlayer, location));
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
		listWidth = Math.max(Math.min(listWidth, width/3), 200);
		listWidth += listWidth % numButtons != 0 ? (numButtons - listWidth % numButtons) : 0;
		int structureWidth = this.width - this.listWidth - (PADDING * 3);
		int closeButtonWidth = Math.min(structureWidth, 200);
		int y = this.height - 20 - PADDING;
		this.addButton(new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				new TranslationTextComponent("gui.cancel"), b -> CompassScreen.this.onClose()));

		y -= 18 + PADDING;
		this.addButton(this.loadButton = new Button(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20,
				new TranslationTextComponent("structurecompass.screen.selection.load"), b -> {
			if(selected != null) {
				PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new SetStructureMessage(usedHand, selected.getStructureLocation()));
			}
		}));

		y -= 14 + PADDING;
		search = new TextFieldWidget(getFontRenderer(), centerWidth - listWidth / 2 + PADDING + 1, y, listWidth - 2, 14,
				new TranslationTextComponent("structurecompass.screen.search"));

		int fullButtonHeight = PADDING + 20 + PADDING;
		this.structureWidget = new StructureListWidget(this, width, fullButtonHeight, search.y - getFontRenderer().lineHeight - PADDING);
		this.structureWidget.setLeftPos(0);

		children.add(search);
		children.add(structureWidget);
		search.setFocus(false);
		search.setCanLoseFocus(true);
		if(this.compassStack.hasTag() && this.compassStack.getTag().contains(Reference.structure_tag)) {
			String structure = this.compassStack.getTag().getString(Reference.structure_tag);
			search.setValue(structure);
		}

		final int width = listWidth / numButtons;
		int x = centerWidth + PADDING - width;
		addButton(SortType.A_TO_Z.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.A_TO_Z.getButtonText(), b -> resortStructures(SortType.A_TO_Z)));
		x += width + buttonMargin;
		addButton(SortType.Z_TO_A.button = new Button(x, PADDING, width - buttonMargin, 20, SortType.Z_TO_A.getButtonText(), b -> resortStructures(SortType.Z_TO_A)));

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
			if(sortType == SortType.A_TO_Z) {
				Collections.sort(structures);
			} else if(sortType == SortType.Z_TO_A) {
				Collections.sort(structures, Collections.reverseOrder());
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

	public <T extends ExtendedList.AbstractListEntry<T>> void buildStructureList(Consumer<T> ListViewConsumer, Function<ResourceLocation, T> newEntry) {
		structures.forEach(mod->ListViewConsumer.accept(newEntry.apply(mod)));
	}

	private void reloadStructures() {
		this.structures = this.unsortedStructures.stream().
				filter(struc -> StringUtils.toLowerCase(struc.toString()).contains(StringUtils.toLowerCase(search.getValue()))).collect(Collectors.toList());
		checkStages();
		lastFilterText = search.getValue();
	}

	private void checkStages() {
		if(ModList.get().isLoaded("gamestages")) {
			this.structures.removeIf((location) -> !GameStagesHelper.doesPlayerHaveRequiredStage(this.editingPlayer, location));
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
	public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.structureWidget.render(poseStack, mouseX, mouseY, partialTicks);

		ITextComponent text = new TranslationTextComponent("structurecompass.screen.search");
		drawCenteredString(poseStack, getFontRenderer(), text, this.width / 2 + PADDING,
				search.y - getFontRenderer().lineHeight - 2, 0xFFFFFF);

		this.search.render(poseStack, mouseX , mouseY, partialTicks);

		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	public FontRenderer getFontRenderer() {
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
