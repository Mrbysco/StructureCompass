package com.mrbysco.structurecompass.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.client.property.StructureCompassAngle;
import com.mrbysco.structurecompass.client.screen.CompassScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ClientHandler {

	public static final KeyMapping.Category STRUCTURE_CATEGORY = new KeyMapping.Category(Reference.modLoc("category"));
	public static final KeyMapping KEY_TOGGLE = new KeyMapping(
			"key." + Reference.MOD_ID + ".hide",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			STRUCTURE_CATEGORY);

	public static void registerRangeSelectProperties(final RegisterRangeSelectItemModelPropertyEvent event) {
		event.register(Reference.modLoc("structure_compass_angle"), StructureCompassAngle.MAP_CODEC);
	}

	public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
		event.registerCategory(STRUCTURE_CATEGORY);
		event.register(KEY_TOGGLE);
	}

	public static void openStructureScreen(InteractionHand hand, ItemStack stack, List<Identifier> allStructures) {
		CompassScreen screen = new CompassScreen(hand, stack, allStructures);
		Minecraft.getInstance().setScreen(screen);
	}
}
