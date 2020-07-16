package com.mrbysco.structurecompass.util;

import com.mrbysco.structurecompass.config.StructureConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;

import java.util.ArrayList;
import java.util.List;

public class StructureUtil {
    public static List<String> getAvailableStructures() {
        List<String> structureList = new ArrayList<>();

        for(ResourceLocation structure : GameData.getStructureFeatures().keySet()) {
            String structureLocation = structure.toString();
            if(!isBlacklisted(structureLocation)) {
                if(structureList.isEmpty() || !structureList.contains(structureLocation)) {
                    structureList.add(structureLocation);
                }
            }
        }

        return structureList;
    }

    public static boolean isBlacklisted(String structureLocation) {
        return StructureConfig.COMMON.structureBlacklist.get().isEmpty() ? false : StructureConfig.COMMON.structureBlacklist.get().contains(structureLocation);
    }
}
