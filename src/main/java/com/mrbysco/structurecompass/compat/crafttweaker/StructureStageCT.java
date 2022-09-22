package com.mrbysco.structurecompass.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;

@ZenRegister
@Name("mods.structurecompass.staging")
public class StructureStageCT {
	@Method
	public static void setStructureStages(ResourceLocation containerName, String... stages) {
		CraftTweakerAPI.apply(new ActionSetStructureStaging(containerName, stages));
	}
}
