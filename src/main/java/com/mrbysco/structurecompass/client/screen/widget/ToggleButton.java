package com.mrbysco.structurecompass.client.screen.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ToggleButton extends Button {

	private boolean value;
	private final Component falseMessage;
	private final Component trueMessage;

	public ToggleButton(int x, int y, int width, int height, boolean defaultValue, Component falseMessage, Component trueMessage, Button.OnPress pressedAction) {
		super(x, y, width, height, defaultValue ? trueMessage : falseMessage, pressedAction);
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
}