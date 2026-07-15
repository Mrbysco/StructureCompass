package com.mrbysco.structurecompass.util;

import com.mojang.datafixers.util.Pair;
import com.mrbysco.structurecompass.StructureCompass;
import com.mrbysco.structurecompass.config.StructureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

public class StructureUtil {
	public static List<Identifier> getAvailableStructureList(Level level) {
		List<Identifier> structureList = new ArrayList<>();
		Registry<Structure> registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
		registry.asHolderIdMap().forEach(holder -> {
			if (holder.is(Tags.Structures.HIDDEN_FROM_LOCATOR_SELECTION)) return;
			Identifier location = holder.getKey().identifier();
			if (!isBlacklisted(location) && !structureList.contains(location)) {
				structureList.add(location);
			}
		});

		return structureList;
	}

	public static List<Identifier> getAvailableTagList(Level level) {
		List<Identifier> tagList = new ArrayList<>();
		Registry<Structure> registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
		registry.getTags().forEach(tag -> {
			if (!tagList.contains(tag.key().location())) {
				tagList.add(tag.key().location());
			}
		});

		// Remove hidden tag
		tagList.removeIf(tag -> tag.equals(
				Tags.Structures.HIDDEN_FROM_LOCATOR_SELECTION.location()
		));

		return tagList;
	}

	public static boolean isBlacklisted(Identifier structureLocation) {
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

	public static Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel serverLevel,
	                                                                        HolderSet<Structure> structureHolderSet, BlockPos pos, int range, boolean findUnexplored) {
		ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
		Pair<BlockPos, Holder<Structure>> nearest = generator.findNearestMapStructure(serverLevel, structureHolderSet, pos, range, findUnexplored);
		if (nearest == null) return null;
		return nearest.getFirst().distManhattan(pos) <= StructureConfig.COMMON.compassRange.get() ? nearest : null;
	}

	public static Component getStructureName(Identifier structureLocation, boolean isTag) {
		if (isTag) {
			return Component.translatableWithFallback(getTagTranslationKey(structureLocation), "#" + structureLocation.toString());
		}
		return Component.translatableWithFallback(structureLocation.toLanguageKey("structure"), structureLocation.toString());
	}

	private static String getTagTranslationKey(Identifier tagIdentifier) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("tag.");

		Identifier registryIdentifier = Registries.STRUCTURE.identifier();

		stringBuilder.append(registryIdentifier.toShortLanguageKey().replace("/", "."))
				.append(".")
				.append(tagIdentifier.getNamespace())
				.append(".")
				.append(tagIdentifier.getPath().replace("/", "."));

		return stringBuilder.toString();
	}
}
