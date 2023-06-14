package com.mrbysco.structurecompass.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.init.StructureItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	public static boolean hidden = false;

	@SubscribeEvent
	public void onKeyInput(InputEvent.Key event) {
		final Minecraft minecraft = Minecraft.getInstance();
		final Player player = minecraft.player;

		if (minecraft.screen != null && event.getAction() != GLFW.GLFW_PRESS) return;

		if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 292)) return;

		if (ClientHandler.KEY_TOGGLE.consumeClick()) {
			if (player != null) {
				hidden = !hidden;
				player.sendSystemMessage(Component.translatable("structurecompass.locate.toggled", hidden ? "off" : "on"));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		final Player player = event.player;
		if (player.tickCount % 10 == 0 && player.isHolding(StructureItems.STRUCTURE_COMPASS.get())) {
			if (!hidden) {
				ItemStack stack = player.getMainHandItem();
				if (!stack.is(StructureItems.STRUCTURE_COMPASS.get())) {
					stack = player.getOffhandItem();
					if (!stack.is(StructureItems.STRUCTURE_COMPASS.get())) {
						return;
					}
				}
				CompoundTag tag = stack.getTag();
				if (tag != null && tag.contains(Reference.structure_location) && tag.contains(Reference.structure_dimension)) {
					final ResourceLocation structureDimension = ResourceLocation.tryParse(tag.getString(Reference.structure_dimension));
					if (player.level().dimension().location().equals(structureDimension)) {
						final BlockPos structurePos = BlockPos.of(tag.getLong(Reference.structure_location));
						int distance = player.blockPosition().distManhattan(structurePos);
						player.displayClientMessage(Component.translatable("structurecompass.locate.distance", distance).withStyle(ChatFormatting.YELLOW), true);
					}
				}
			}
		}
	}
}
