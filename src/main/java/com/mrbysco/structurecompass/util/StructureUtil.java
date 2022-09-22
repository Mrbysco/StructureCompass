package com.mrbysco.structurecompass.util;

import com.mrbysco.structurecompass.StructureCompass;
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
		if (structureLocation == null) {
			StructureCompass.LOGGER.error("Checking blacklist but fed location is null!");
			return false;
		}
		if (!StructureConfig.COMMON.structureBlacklist.get().isEmpty()) {
			if (StructureConfig.COMMON.structureBlacklist.get().contains(structureLocation.toString())) {
				return true;
			}
			List<? extends String> wildcardList = StructureConfig.COMMON.structureBlacklist.get().stream()
					.filter(value -> value.contains(":") && value.contains("*")).toList();
			for (String wildcard : wildcardList) {
				String[] blacklistSplit = wildcard.split(":");
				if ((blacklistSplit[0].equals("*") && structureLocation.getPath().equals(blacklistSplit[1])) ||
						blacklistSplit[1].equals("*") && structureLocation.getNamespace().equals(blacklistSplit[0])
				) {
					return true;
				}
			}
		}
		return false;
	}
}
