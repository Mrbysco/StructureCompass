package com.mrbysco.structurecompass.client.screen.widget;

import com.mrbysco.structurecompass.client.screen.CompassScreen;
import com.mrbysco.structurecompass.client.screen.widget.StructureListWidget.ListEntry;
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
		parent.buildStructureList(this::addEntry, location -> new ListEntry(location, this.parent));
	}

	public class ListEntry extends ObjectSelectionList.Entry<ListEntry> {
		private final Identifier structureLocation;
		private final CompassScreen parent;

		ListEntry(Identifier location, CompassScreen parent) {
			this.structureLocation = location;
			this.parent = parent;
		}

		@Override
		public void extractContent(GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, boolean b, float v) {
			String structureName = structureLocation.toString();
			Component name = Component.literal(structureName);
			Font font = this.parent.getFontRenderer();
			int top = getContentY();
			guiGraphicsExtractor.text(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, listWidth))),
					(this.parent.width / 2) - (font.width(structureName) / 2) + 3, top + 6, ARGB.opaque(0xFFFFFF), false);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
			parent.setSelected(this);
			StructureListWidget.this.setSelected(this);
			return false;
		}

		@Override
		public void setFocused(boolean focused) {
			if (focused) {
				parent.setSelected(this);
				StructureListWidget.this.setSelected(this);
			}
		}

		@Override
		public boolean isFocused() {
			return StructureListWidget.this.getSelected() == this;
		}

		public Identifier getStructureLocation() {
			return structureLocation;
		}

		@NotNull
		@Override
		public Component getNarration() {
			return Component.literal(getStructureLocation().getPath());
		}
	}
}