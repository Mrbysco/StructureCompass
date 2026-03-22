package com.mrbysco.structurecompass.compat.gamestages;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameStagesHelper {
	//Structure Location - Stages
	public static final Map<Identifier, Set<String>> STRUCTURE_STAGES = new HashMap<>();

	public static boolean doesPlayerHaveRequiredStage(Identifier structureLocation) {
//		Player player = Minecraft.getInstance().player;
//		if (STRUCTURE_STAGES.containsKey(structureLocation) && player != null) {
//			Set<String> stages = STRUCTURE_STAGES.computeIfAbsent(structureLocation, s -> new HashSet<>());
//			if (!stages.isEmpty()) {
//				IStageData playerData = net.darkhax.gamestages.GameStageHelper.getPlayerData(player);
//				if (playerData != null) {
//					for (String stage : stages) {
//						if (playerData.hasStage(stage)) {
//							return true;
//						}
//					}
//				}
//			}
//			return false;
//		}
		return true;
	}
}
