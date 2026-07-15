package com.mrbysco.structurecompass.client.screen.widget;

import com.mrbysco.structurecompass.client.screen.CompassScreen;
import com.mrbysco.structurecompass.client.screen.widget.StructureListWidget.ListEntry;
import com.mrbysco.structurecompass.util.StructureUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class StructureListWidget extends ObjectSelectionList<ListEntry> {
	private final CompassScreen parent;
	private final int listWidth;

	public StructureListWidget(CompassScreen parent, int listWidth, int top, int bottom) {
		super(parent.getMinecraft(), listWidth, bottom - top, top, parent.getFontRenderer().lineHeight * 2 + 8);
		this.parent = parent;
		this.listWidth = listWidth;
		this.refreshList();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.listWidth;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	public void refreshList() {
		this.clearEntries();
		parent.buildTagList(this::addEntry, location -> new ListEntry(location, this.parent, true));
		parent.buildStructureList(this::addEntry, location -> new ListEntry(location, this.parent));
	}

	@Override
	public void setSelected(@Nullable StructureListWidget.ListEntry selected) {
		this.parent.setSelected(getSelected(), selected);
		super.setSelected(selected);
	}

	public class ListEntry extends ObjectSelectionList.Entry<ListEntry> {
		private final ResourceLocation structureLocation;
		private final boolean isTag;
		private final CompassScreen parent;

		public ListEntry(ResourceLocation location, CompassScreen parent, boolean isTag) {
			this.structureLocation = location;
			this.parent = parent;
			this.isTag = isTag;
		}

		public ListEntry(ResourceLocation location, CompassScreen parent) {
			this(location, parent, false);
		}

		@Override
		public void render(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovering, float partialTicks) {
			Component structureName = StructureUtil.getStructureName(structureLocation, isTag);
			Font font = this.parent.getFontRenderer();
			guiGraphics.drawString(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(structureName, listWidth))),
					(this.parent.width / 2) - (font.width(structureName) / 2) + 3, top + 6, 0xFFFFFF, false);

			if (Screen.hasShiftDown() && hovering) {
				guiGraphics.renderTooltip(font, Component.literal(structureLocation.toString()), mouseX, mouseY);
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0) {
				StructureListWidget.this.setSelected(this);
				return true;
			}
			return false;
		}

		public ResourceLocation getStructureLocation() {
			return structureLocation;
		}

		public boolean isTag() {
			return isTag;
		}

		@Override
		public Component getNarration() {
			return Component.literal(getStructureLocation().getPath());
		}
	}
}