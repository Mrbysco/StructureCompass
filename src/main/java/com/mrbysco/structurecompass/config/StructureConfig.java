package com.mrbysco.structurecompass.config;

import com.mrbysco.structurecompass.StructureCompass;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

public class StructureConfig {
    public static class Common {
        public final IntValue compassRange;
        public final BooleanValue locateUnexplored;
        public final ConfigValue<List<? extends String>> structureBlacklist;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings")
                    .push("general");

            compassRange = builder
                    .comment("Sets the range in which the structure compasses can check for structures [default: 300]")
                    .defineInRange("compassRange", 300, 0, Integer.MAX_VALUE);

            locateUnexplored = builder
                    .comment("Defines if the structure compass should locate unexplored structures [default: false]")
                    .define("locateUnexplored", false);

            structureBlacklist = builder
                    .comment("Defines which structures can't be searched with the Structure Compass")
                    .defineListAllowEmpty(Collections.singletonList("structureBlacklist"), () -> Collections.singletonList(""), o -> (o instanceof String));

            builder.pop();
        }
    }

    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        StructureCompass.LOGGER.debug("Loaded Structure Compass' config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        StructureCompass.LOGGER.warn("Structure Compass' config just got changed on the file system!");
    }
}
