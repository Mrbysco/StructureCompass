package com.mrbysco.structurecompass.util;

import com.mojang.datafixers.util.Pair;
import com.mrbysco.structurecompass.StructureCompass;
import com.mrbysco.structurecompass.config.StructureConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StructureUtil {
	public static List<ResourceLocation> getAvailableStructureList(Level level) {
		List<ResourceLocation> structureList = new ArrayList<>();
		Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
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

	public static Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel serverLevel,
																			HolderSet<Structure> structureHolderSet,
																			BlockPos pos, int range, boolean findUnexplored) {
		ChunkGeneratorStructureState chunkgeneratorstructurestate = serverLevel.getChunkSource().getGeneratorState();
		ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
		Map<StructurePlacement, Set<Holder<Structure>>> map = new Object2ObjectArrayMap<>();

		for (Holder<Structure> holder : structureHolderSet) {
			for (StructurePlacement structureplacement : chunkgeneratorstructurestate.getPlacementsForStructure(holder)) {
				map.computeIfAbsent(structureplacement, (placement) -> new ObjectArraySet()).add(holder);
			}
		}

		if (map.isEmpty()) {
			return null;
		} else {
			Pair<BlockPos, Holder<Structure>> pair2 = null;
			double d2 = StructureConfig.COMMON.compassRange.get();
			StructureManager structuremanager = serverLevel.structureManager();
			List<Map.Entry<StructurePlacement, Set<Holder<Structure>>>> list = new ArrayList<>(map.size());

			for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry : map.entrySet()) {
				StructurePlacement structurePlacement = entry.getKey();
				if (structurePlacement instanceof ConcentricRingsStructurePlacement concentricringsstructureplacement) {
					Pair<BlockPos, Holder<Structure>> pair = generator.getNearestGeneratedStructure(entry.getValue(),
							serverLevel, structuremanager, pos, findUnexplored, concentricringsstructureplacement);
					if (pair != null) {
						BlockPos blockpos = pair.getFirst();
						double d0 = pos.distManhattan(blockpos);
						if (d0 < d2) {
							d2 = d0;
							pair2 = pair;
						}
					}
				} else if (structurePlacement instanceof RandomSpreadStructurePlacement) {
					list.add(entry);
				}
			}

			if (!list.isEmpty()) {
				int i = SectionPos.blockToSectionCoord(pos.getX());
				int j = SectionPos.blockToSectionCoord(pos.getZ());

				for (int k = 0; k <= range; ++k) {
					boolean flag = false;

					for (Map.Entry<StructurePlacement, Set<Holder<Structure>>> entry1 : list) {
						RandomSpreadStructurePlacement randomSpreadStructurePlacement = (RandomSpreadStructurePlacement) entry1.getKey();
						Pair<BlockPos, Holder<Structure>> pair1 = ChunkGenerator.getNearestGeneratedStructure(entry1.getValue(),
								serverLevel, structuremanager, i, j, k, findUnexplored, serverLevel.getSeed(), randomSpreadStructurePlacement);
						if (pair1 != null) {
							flag = true;
							double d1 = pos.distManhattan(pair1.getFirst());
							if (d1 < d2) {
								d2 = d1;
								pair2 = pair1;
							}
						}
					}

					if (flag) {
						return pair2;
					}
				}
			}

			return pair2;
		}
	}
}
