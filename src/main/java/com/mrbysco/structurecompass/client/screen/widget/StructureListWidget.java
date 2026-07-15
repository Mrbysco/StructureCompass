package com.mrbysco.structurecompass.client.screen.widget;

import com.mrbysco.structurecompass.client.screen.CompassScreen;
import com.mrbysco.structurecompass.client.screen.widget.StructureListWidget.ListEntry;
import com.mrbysco.structurecompass.util.StructureUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;
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
	protected int scrollBarX() {
		return this.getX() + this.listWidth - 6;
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
		private final Identifier structureLocation;
		private final boolean isTag;
		private final CompassScreen parent;

		public ListEntry(Identifier location, CompassScreen parent, boolean isTag) {
			this.structureLocation = location;
			this.parent = parent;
			this.isTag = isTag;
		}

		public ListEntry(Identifier location, CompassScreen parent) {
			this(location, parent, false);
		}

		@Override
		public void extractContent(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, boolean hovering, float partialTick) {
			Component name = StructureUtil.getStructureName(structureLocation, isTag);
			Font font = this.parent.getFontRenderer();
			int top = getContentY();
			guiGraphicsExtractor.text(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, listWidth))),
					(this.parent.width / 2) - (font.width(name) / 2) + 3, top + 6, ARGB.opaque(0xFFFFFF), false);

			if (minecraft.hasShiftDown() && hovering) {
				guiGraphicsExtractor.setTooltipForNextFrame(font, Component.literal(structureLocation.toString()), mouseX, mouseY);
			}
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
			if (event.button() == 0) {
				StructureListWidget.this.setSelected(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean isFocused() {
			return StructureListWidget.this.getSelected() == this;
		}

		@NotNull
		public Identifier getStructureLocation() {
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