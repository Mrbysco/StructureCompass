package com.mrbysco.structurecompass.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ToggleButton extends Button {

	private boolean value;
	private final Component falseMessage;
	private final Component trueMessage;

	public ToggleButton(int x, int y, int width, int height, boolean defaultValue, Component falseMessage, Component trueMessage, Button.OnPress pressedAction, CreateNarration createNarration) {
		super(x, y, width, height, defaultValue ? trueMessage : falseMessage, pressedAction, createNarration);
		this.value = defaultValue;
		this.falseMessage = falseMessage;
		this.trueMessage = trueMessage;
	}

	public boolean getValue() {
		return this.value;
	}

	public void setValue(boolean value) {
		this.value = value;
		this.setMessage(value ? trueMessage : falseMessage);
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.extractDefaultSprite(guiGraphics);
		this.extractDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
	}

	public static class Builder {
		private final boolean defaultValue;
		private final Component falseMessage;
		private final Component trueMessage;
		private final OnPress onPress;
		@Nullable
		private Tooltip tooltip;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;
		private CreateNarration createNarration = Button.DEFAULT_NARRATION;

		public Builder(boolean defaultValue, Component falseMessage, Component trueMessage, OnPress onPress) {
			this.defaultValue = defaultValue;
			this.falseMessage = falseMessage;
			this.trueMessage = trueMessage;
			this.onPress = onPress;
		}

		public Builder pos(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder size(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder bounds(int x, int y, int width, int height) {
			return this.pos(x, y).size(width, height);
		}

		public Builder tooltip(@Nullable Tooltip tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public Builder createNarration(Button.CreateNarration createNarration) {
			this.createNarration = createNarration;
			return this;
		}

		public ToggleButton build() {
			ToggleButton button = new ToggleButton(this.x, this.y, this.width, this.height, this.defaultValue, this.falseMessage, this.trueMessage, this.onPress, this.createNarration);
			button.setTooltip(this.tooltip);
			return button;
		}
	}
}
