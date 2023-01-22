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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StructureUtil {
	public static List<ResourceLocation> getAvailableStructureList(Level level) {
		List<ResourceLocation> structureList = new ArrayList<>();
		Registry<ConfiguredStructureFeature<?, ?>> configuredRegistry = level.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
		configuredRegistry.keySet().forEach(location -> {
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

	@Nullable
	public static Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> findNearestMapFeature(ServerLevel serverLevel, HolderSet<ConfiguredStructureFeature<?, ?>> featureHolderSet, BlockPos pos, int range, boolean locateUnexplored) {
		ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
		Set<Holder<Biome>> set = featureHolderSet.stream().flatMap((featureHolder) -> {
			return featureHolder.value().biomes().stream();
		}).collect(Collectors.toSet());
		if (set.isEmpty()) {
			return null;
		} else {
			Set<Holder<Biome>> set1 = generator.getBiomeSource().possibleBiomes();
			if (Collections.disjoint(set1, set)) {
				return null;
			} else {
				Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair = null;
				double locateRange = StructureConfig.COMMON.compassRange.get();
				Map<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> map = new Object2ObjectArrayMap<>();

				for (Holder<ConfiguredStructureFeature<?, ?>> holder : featureHolderSet) {
					if (!set1.stream().noneMatch(holder.value().biomes()::contains)) {
						for (StructurePlacement structureplacement : generator.getPlacementsForFeature(holder)) {
							map.computeIfAbsent(structureplacement, (p_211663_) -> {
								return new ObjectArraySet();
							}).add(holder);
						}
					}
				}

				List<Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>>> list = new ArrayList<>(map.size());

				for (Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> entry : map.entrySet()) {
					StructurePlacement structureplacement1 = entry.getKey();
					if (structureplacement1 instanceof ConcentricRingsStructurePlacement concentricringsstructureplacement) {
						BlockPos blockpos = generator.getNearestGeneratedStructure(pos, concentricringsstructureplacement);
						double d1 = pos.distManhattan(blockpos);
						if (d1 < locateRange) {
							locateRange = d1;
							pair = Pair.of(blockpos, entry.getValue().iterator().next());
						}
					} else if (structureplacement1 instanceof RandomSpreadStructurePlacement) {
						list.add(entry);
					}
				}

				if (!list.isEmpty()) {
					int i = SectionPos.blockToSectionCoord(pos.getX());
					int j = SectionPos.blockToSectionCoord(pos.getZ());

					for (int k = 0; k <= range; ++k) {
						boolean flag = false;

						for (Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> entry1 : list) {
							RandomSpreadStructurePlacement randomspreadstructureplacement = (RandomSpreadStructurePlacement) entry1.getKey();
							Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair1 = ChunkGenerator.getNearestGeneratedStructure(entry1.getValue(), serverLevel, serverLevel.structureFeatureManager(), i, j, k, locateUnexplored, serverLevel.getSeed(), randomspreadstructureplacement);
							if (pair1 != null) {
								flag = true;
								double d2 = pos.distManhattan(pair1.getFirst());
								if (d2 < locateRange) {
									locateRange = d2;
									pair = pair1;
								}
							}
						}

						if (flag) {
							return pair;
						}
					}
				}

				return pair;
			}
		}
	}
}
