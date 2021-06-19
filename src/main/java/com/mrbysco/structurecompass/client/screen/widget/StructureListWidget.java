package com.mrbysco.structurecompass.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.structurecompass.client.screen.CompassScreen;
import com.mrbysco.structurecompass.client.screen.widget.StructureListWidget.ListEntry;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;

public class StructureListWidget extends ExtendedList<ListEntry> {
	private final CompassScreen parent;
	private final int listWidth;

	public StructureListWidget(CompassScreen parent, int listWidth, int top, int bottom) {
		super(parent.getMinecraft(), listWidth, parent.height, top, bottom, parent.getFontRenderer().lineHeight * 2 + 8);
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
		parent.buildStructureList(this::addEntry, location -> new ListEntry(location, this.parent));
	}

	@Override
	protected void renderBackground(MatrixStack mStack) {
		this.parent.renderBackground(mStack);
	}

	public class ListEntry extends ExtendedList.AbstractListEntry<ListEntry> {
		private final ResourceLocation structureLocation;
		private final CompassScreen parent;

		ListEntry(ResourceLocation location, CompassScreen parent) {
			this.structureLocation = location;
			this.parent = parent;
		}

		@Override
		public void render(MatrixStack mStack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
			String structureName = structureLocation.toString();
			ITextComponent name = new StringTextComponent(structureName);
			FontRenderer font = this.parent.getFontRenderer();
			font.draw(mStack, LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(name, listWidth))),
					(this.parent.width / 2) - (font.width(structureName) / 2) + 3, top + 6, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			parent.setSelected(this);
			StructureListWidget.this.setSelected(this);
			return false;
		}

		public ResourceLocation getStructureLocation() {
			return structureLocation;
		}
	}
}