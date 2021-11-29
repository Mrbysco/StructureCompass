package com.mrbysco.structurecompass.compat.gamestages;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameStagesHelper {
	//Structure Location - Stages
	public static final Map<ResourceLocation, Set<String>> STRUCTURE_STAGES = new HashMap<>();

//	public static boolean doesPlayerHaveRequiredStage(Player player, ResourceLocation structureLocation) { TODO: Re-implement once GameStages is back
//		if(STRUCTURE_STAGES.containsKey(structureLocation)) {
//			Set<String> stages = STRUCTURE_STAGES.computeIfAbsent(structureLocation, s -> new HashSet<>());
//			if(!stages.isEmpty()) {
//				IStageData playerData = net.darkhax.gamestages.GameStageHelper.getPlayerData(player);
//				if(playerData != null) {
//					for(String stage : stages) {
//						if(playerData.hasStage(stage)) {
//							return true;
//						}
//					}
//				}
//			}
//			return false;
//		}
//		return true;
//	}
}
