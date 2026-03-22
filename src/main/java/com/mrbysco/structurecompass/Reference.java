package com.mrbysco.structurecompass;

import net.minecraft.resources.Identifier;

public class Reference {
	public static final String MOD_ID = "structurecompass";
	public static final String MOD_PREFIX = MOD_ID + ":";

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
