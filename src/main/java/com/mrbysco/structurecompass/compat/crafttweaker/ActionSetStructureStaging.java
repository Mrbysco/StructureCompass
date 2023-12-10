//package com.mrbysco.structurecompass.compat.crafttweaker;
//
//import com.blamejared.crafttweaker.api.action.base.IUndoableAction;
//import com.blamejared.crafttweaker.api.zencode.IScriptLoadSource;
//import com.mrbysco.structurecompass.Reference;
//import com.mrbysco.structurecompass.compat.gamestages.GameStagesHelper;
//import net.minecraft.resources.ResourceLocation;
//import org.apache.logging.log4j.Logger;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class ActionSetStructureStaging implements IUndoableAction {
//	private final ResourceLocation structureLocation;
//	private final String[] stages;
//
//	public ActionSetStructureStaging(ResourceLocation structureLocation, String[] stages) {
//		this.structureLocation = structureLocation;
//		this.stages = stages;
//	}
//
//	@Override
//	public boolean shouldApplyOn(IScriptLoadSource source, Logger logger) {
//		return true;
//	}
//
//	@Override
//	public void undo() {
//		Set<String> strings = GameStagesHelper.STRUCTURE_STAGES.computeIfAbsent(structureLocation, s -> new HashSet<>());
//		List.of(this.stages).forEach(strings::remove);
//		GameStagesHelper.STRUCTURE_STAGES.put(structureLocation, strings);
//	}
//
//	@Override
//	public String describeUndo() {
//		return "Removing the stages \"" + Arrays.toString(stages) + "\" of structure: \"" + structureLocation + "\"";
//	}
//
//	@Override
//	public void apply() {
//		Set<String> strings = GameStagesHelper.STRUCTURE_STAGES.computeIfAbsent(structureLocation, s -> new HashSet<>());
//		strings.addAll(Arrays.asList(stages));
//		GameStagesHelper.STRUCTURE_STAGES.put(structureLocation, strings);
//	}
//
//	@Override
//	public String describe() {
//		return "Set the stages of structure: \"" + structureLocation + "\" to: " + Arrays.toString(stages);
//	}
//
//	@Override
//	public String systemName() {
//		return Reference.MOD_ID;
//	}
//}
