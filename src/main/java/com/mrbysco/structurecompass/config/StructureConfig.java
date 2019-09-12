package com.mrbysco.structurecompass.config;

import com.mrbysco.structurecompass.StructureCompass;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import static net.minecraftforge.fml.Logging.CORE;

public class StructureConfig {
    public static class Common {
        public final IntValue compassRange;
        public final BooleanValue locateUnexplored;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings")
                    .push("general");

            compassRange = builder
                    .comment("Sets the range in which the strucute compasses can check for structures [default: 300]")
                    .defineInRange("compassRange", 300, 0, Integer.MAX_VALUE);

            locateUnexplored = builder
                    .comment("Defines if the structure compass should locate unexplored structures [default: false]")
                    .define("locateUnexplored", false);

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
    public static void onLoad(final ModConfig.Loading configEvent) {
        StructureCompass.LOGGER.debug(StructureCompass.STRUCTURECOMPASS, "Loaded Statues' config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
        StructureCompass.LOGGER.fatal(CORE, "Statues' config just got changed on the file system!");
    }
}
