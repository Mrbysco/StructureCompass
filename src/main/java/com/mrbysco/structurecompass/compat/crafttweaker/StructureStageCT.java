package com.mrbysco.structurecompass.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.structurecompass.staging")
public class StructureStageCT {
	@ZenCodeType.Method
	public static void setStructureStages(ResourceLocation containerName, String... stages) {
		CraftTweakerAPI.apply(new ActionSetStructureStaging(containerName, stages));
	}
}
