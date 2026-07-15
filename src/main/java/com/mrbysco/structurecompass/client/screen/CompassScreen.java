package com.mrbysco.structurecompass.client.screen;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.client.screen.widget.StructureListWidget;
import com.mrbysco.structurecompass.client.screen.widget.ToggleButton;
import com.mrbysco.structurecompass.network.PacketHandler;
import com.mrbysco.structurecompass.network.message.SetStructureMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
	private Component showTagsText = Component.translatable("structurecompass.screen.search.show_tags");
	private Component hideTagsText = Component.translatable("structurecompass.screen.search.hide_tags");

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
	private List<ResourceLocation> tags;
	private final List<ResourceLocation> unsortedTags;
	private Button loadButton;
	private ToggleButton showTags;

	private final InteractionHand usedHand;
	private final ItemStack compassStack;

	private final int buttonMargin = 1;
	private final int numButtons = SortType.values().length;
	private String lastFilterText = "";

	private EditBox search;
	private boolean sorted = false;
	private SortType sortType = SortType.NORMAL;

	public CompassScreen(InteractionHand hand, ItemStack compass, List<ResourceLocation> allStructures, List<ResourceLocation> allTags) {
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

		this.tags = Collections.unmodifiableList(allTags);
		this.unsortedTags = Collections.unmodifiableList(allTags);
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
		this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> CompassScreen.this.onClose())
				.bounds(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20).build());

		y -= 18 + PADDING;
		this.addRenderableWidget(this.loadButton = Button.builder(Component.translatable("structurecompass.screen.selection.select"), b -> {
			if (selected != null) {
				PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new SetStructureMessage(usedHand, selected.getStructureLocation(), selected.isTag()));
			}

			if (this.minecraft.player != null && selected != null)
				this.minecraft.player.sendSystemMessage(Component.translatable("structurecompass.screen.selection.selected", selected.getStructureLocation()).withStyle(ChatFormatting.GOLD));
			this.onClose();
		}).bounds(centerWidth - (closeButtonWidth / 2) + PADDING, y, closeButtonWidth, 20).build());

		y -= 14 + PADDING;
		search = new EditBox(getFontRenderer(), centerWidth - listWidth / 2 + PADDING + 1, y, listWidth - 2, 14,
				Component.translatable("structurecompass.screen.search"));

		this.addRenderableWidget(this.showTags = new ToggleButton.Builder(false, showTagsText, hideTagsText, b -> {
			ToggleButton toggleButton = ((ToggleButton) b);
			toggleButton.setValue(!toggleButton.getValue());
			this.updateTags();
		}).bounds(centerWidth + listWidth / 2 - 24, PADDING, 80, 20).build());

		int fullButtonHeight = PADDING + 20 + PADDING;
		this.structureWidget = new StructureListWidget(this, width, fullButtonHeight, search.getY() - getFontRenderer().lineHeight - PADDING);
		this.structureWidget.setLeftPos(0);

		addWidget(search);
		addWidget(structureWidget);
		search.setFocused(false);
		search.setCanLoseFocus(true);
		if (this.compassStack.hasTag() && this.compassStack.getTag().contains(Reference.structure_tag)) {
			String structure = this.compassStack.getTag().getString(Reference.structure_tag);
			if (this.compassStack.getTag().getBoolean(Reference.structure_is_tag)) {
				this.showTags.setValue(true);
			}
			search.setValue(structure);
		}

		final int width = listWidth / numButtons;
		int x = centerWidth + PADDING - width;
		addRenderableWidget(SortType.A_TO_Z.button = Button.builder(SortType.A_TO_Z.getButtonText(), b ->
						resortStructures(SortType.A_TO_Z))
				.bounds(x, PADDING, width - buttonMargin, 20).build());
		x += width + buttonMargin;
		addRenderableWidget(SortType.Z_TO_A.button = Button.builder(SortType.Z_TO_A.getButtonText(), b ->
						resortStructures(SortType.Z_TO_A))
				.bounds(x, PADDING, width - buttonMargin, 20).build());

		resortStructures(SortType.A_TO_Z);
		updateCache();
	}

	private void updateTags() {
		reloadStructures();
		this.structureWidget.refreshList();
		updateCache();
	}

	@Override
	public void tick() {
		search.tick();

		if (structureWidget.getSelected() != selected) {
			structureWidget.setSelected(selected);
		}

		if (!search.getValue().equals(lastFilterText)) {
			reloadStructures();
			sorted = false;
		}

		if (!sorted) {
			reloadStructures();
			if (sortType == SortType.A_TO_Z) {
				Collections.sort(structures);
				Collections.sort(tags);
			} else if (sortType == SortType.Z_TO_A) {
				structures.sort(Collections.reverseOrder());
				tags.sort(Collections.reverseOrder());
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

	public <T extends ObjectSelectionList.Entry<T>> void buildTagList(Consumer<T> ListViewConsumer, Function<ResourceLocation, T> newEntry) {
		if (showTags.getValue())
			tags.forEach(mod -> ListViewConsumer.accept(newEntry.apply(mod)));
	}

	private void reloadStructures() {
		this.structures = this.unsortedStructures.stream().
				filter(struc -> StringUtils.toLowerCase(struc.toString()).contains(StringUtils.toLowerCase(search.getValue()))).collect(Collectors.toList());
		this.tags = this.unsortedTags.stream().
				filter(tag -> StringUtils.toLowerCase(tag.toString()).contains(StringUtils.toLowerCase(search.getValue()))).collect(Collectors.toList());
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
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.structureWidget.render(guiGraphics, mouseX, mouseY, partialTicks);

		Component text = Component.translatable("structurecompass.screen.search");
		guiGraphics.drawCenteredString(getFontRenderer(), text, this.width / 2 + PADDING,
				search.getY() - getFontRenderer().lineHeight - 2, 0xFFFFFF);

		this.search.render(guiGraphics, mouseX, mouseY, partialTicks);

		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	public Font getFontRenderer() {
		return font;
	}

	public void setSelected(StructureListWidget.ListEntry previousEntry, StructureListWidget.ListEntry entry) {
		if (this.selected == previousEntry) {
			this.selected = entry;
		} else {
			if (this.selected == null || entry != null) {
				this.selected = entry;
			}
		}
		updateCache();
	}

	private void updateCache() {
		this.loadButton.active = selected != null;
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
		boolean showTags = this.showTags.getValue();
		String s = this.search.getValue();
		SortType sort = this.sortType;
		StructureListWidget.ListEntry selected = this.selected;
		this.init(mc, width, height);
		this.search.setValue(s);
		this.selected = selected;
		this.showTags.setValue(showTags);
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
