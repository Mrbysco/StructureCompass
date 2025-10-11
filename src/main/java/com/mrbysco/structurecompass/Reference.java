package com.mrbysco.structurecompass;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class Reference {
	public static final String MOD_ID = "structurecompass";
	public static final String MOD_PREFIX = MOD_ID + ":";

	public static final String structure_tag = Reference.MOD_PREFIX + "structureName";
	public static final String structure_found = Reference.MOD_PREFIX + "structureFound";
	public static final String structure_location = Reference.MOD_PREFIX + "structurePosition";
	public static final String structure_dimension = Reference.MOD_PREFIX + "structureDimension";

	public static final TagKey<Structure> HIDDEN_FROM_LOCATOR_SELECTION = TagKey.create(Registries.STRUCTURE, new ResourceLocation("forge", "hidden_from_locator_selection"));
}
