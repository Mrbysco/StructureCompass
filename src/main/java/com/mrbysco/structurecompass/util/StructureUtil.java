package com.mrbysco.structurecompass.util;

import com.mrbysco.structurecompass.config.StructureConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public class StructureUtil {
	public static List<ResourceLocation> getAvailableStructureList(Level level) {
		List<ResourceLocation> structureList = new ArrayList<>();
		Registry<Structure> registry = level.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
		registry.keySet().forEach(location -> {
			if (!isBlacklisted(location) && !structureList.contains(location)) {
				structureList.add(location);
			}
		});

		return structureList;
	}

	public static boolean isBlacklisted(ResourceLocation structureLocation) {
		return isBlacklisted(structureLocation.toString());
	}

	public static boolean isBlacklisted(String structureLocation) {
		return !StructureConfig.COMMON.structureBlacklist.get().isEmpty() && StructureConfig.COMMON.structureBlacklist.get().contains(structureLocation);
	}
}
