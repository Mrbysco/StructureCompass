package com.mrbysco.structurecompass.config;

import com.mrbysco.structurecompass.StructureCompass;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class StructureConfig {
	public static class Common {
		public final ModConfigSpec.IntValue compassRange;
		public final BooleanValue locateUnexplored;
		public final BooleanValue locateAsync;
		public final ConfigValue<List<? extends String>> structureBlacklist;

		Common(ModConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("general");

			compassRange = builder
					.comment("Sets the range in blocks in which the structure compasses can locate structures [default: 10000]")
					.defineInRange("compassRange", 10000, 0, Integer.MAX_VALUE);

			locateUnexplored = builder
					.comment("Defines if the structure compass should locate unexplored structures [default: false]")
					.define("locateUnexplored", false);

			locateAsync = builder
					.comment("Defines if the structure compass should locate structures asynchronously [default: false]")
					.define("locateAsync", false);

			structureBlacklist = builder
					.comment("Defines which structures can't be searched with the Structure Compass\n" +
							"(Supports wildcard *, Example: 'minecraft:*' will blacklist anything in the minecraft domain)")
					.defineListAllowEmpty(List.of("structureBlacklist"), () -> List.of(""), o -> (o instanceof String));

			builder.pop();
		}
	}

	public static final ModConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
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
