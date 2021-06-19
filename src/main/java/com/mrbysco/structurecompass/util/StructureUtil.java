package com.mrbysco.structurecompass.util;

import com.mrbysco.structurecompass.config.StructureConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public class StructureUtil {
    public static List<ResourceLocation> getAvailableStructureList() {
        List<ResourceLocation> structureList = new ArrayList<>();

        for (Structure<?> structureFeature : net.minecraftforge.registries.ForgeRegistries.STRUCTURE_FEATURES) {
            ResourceLocation location = structureFeature.getRegistryName();
            if(location != null) {
                if(!isBlacklisted(location.toString()) && !structureList.contains(structureFeature.getRegistryName())) {
                    structureList.add(structureFeature.getRegistryName());
                }
            }
        }

        return structureList;
    }

    public static boolean isBlacklisted(String structureLocation) {
        return !StructureConfig.COMMON.structureBlacklist.get().isEmpty() && StructureConfig.COMMON.structureBlacklist.get().contains(structureLocation);
    }
}
