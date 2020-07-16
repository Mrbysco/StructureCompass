package com.mrbysco.structurecompass.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.structurecompass.Reference;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;
import java.util.List;

public class CompassScreen extends Screen {
    private final PlayerEntity editingPlayer;
    private final ItemStack compass;
    private final Hand hand;

    private List<String> structureList;
    private final List<String> unsortedStructureLists;

    private String defaultInputFieldText = "";
    protected TextFieldWidget inputField;

    public CompassScreen(PlayerEntity player, ItemStack compassIn, Hand handIn, List<String> availableStructureList) {
        super(new TranslationTextComponent(Reference.MOD_PREFIX + "compass.screen"));
        this.editingPlayer = player;
        this.compass = compassIn;
        this.hand = handIn;
        this.structureList = Collections.unmodifiableList(availableStructureList);
        this.unsortedStructureLists = Collections.unmodifiableList(this.structureList);

        if(compassIn.hasTag() && compassIn.getTag().contains(Reference.structure_tag)) {
            this.defaultInputFieldText = compassIn.getTag().getString(Reference.structure_tag);
        }
    }

    @Override
    protected void init() {
        this.inputField = new TextFieldWidget(this.font, ((this.width - 192) / 2) + 4, (this.height / 2) - 4, 192, 12, I18n.format("structurecompass.chat.editBox")) {
            protected String getNarrationMessage() {
                return super.getNarrationMessage(); //+ CompassScreen.this.commandSuggestionHelper.func_228129_c_();
            }
        };
        this.inputField.setMaxStringLength(256);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setText(this.defaultInputFieldText);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        this.renderBackground();
        this.setFocused((IGuiEventListener)null);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.setFocused(this.inputField);
        this.inputField.setFocused2(true);
        this.fill(((this.width - 192) / 2), (this.height / 2) - 8, ((this.width - 192) / 2) + 204, (this.height / 2) + 8, this.minecraft.gameSettings.getChatBackgroundColor(Integer.MIN_VALUE));
        this.inputField.render(mouseX, mouseY, partialTick);

        super.render(mouseX, mouseY, partialTick);
    }
}
