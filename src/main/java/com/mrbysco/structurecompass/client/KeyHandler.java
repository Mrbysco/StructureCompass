package com.mrbysco.structurecompass.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrbysco.structurecompass.component.StructureInfo;
import com.mrbysco.structurecompass.registry.StructureComponents;
import com.mrbysco.structurecompass.registry.StructureItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
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
	public void onPlayerTick(PlayerTickEvent.Post event) {
		final Player player = event.getEntity();
		if (player.tickCount % 10 == 0 && player.isHolding(StructureItems.STRUCTURE_COMPASS.get())) {
			if (!hidden) {
				ItemStack stack = player.getMainHandItem();
				if (!stack.is(StructureItems.STRUCTURE_COMPASS.get())) {
					stack = player.getOffhandItem();
					if (!stack.is(StructureItems.STRUCTURE_COMPASS.get())) {
						return;
					}
				}
				if (stack.has(StructureComponents.STRUCTURE_INFO)) {
					StructureInfo info = stack.get(StructureComponents.STRUCTURE_INFO);
					final ResourceLocation structureDimension = info.dimension().location();
					if (player.level().dimension().location().equals(structureDimension)) {
						int distance = player.blockPosition().distManhattan(info.pos());
						player.displayClientMessage(Component.translatable("structurecompass.locate.distance", distance).withStyle(ChatFormatting.YELLOW), true);
					}
				}
			}
		}
	}
}
