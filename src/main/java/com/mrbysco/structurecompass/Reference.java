package com.mrbysco.structurecompass;

import net.minecraft.resources.ResourceLocation;

public class Reference {
	public static final String MOD_ID = "structurecompass";
	public static final String MOD_PREFIX = MOD_ID + ":";

	public static ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
