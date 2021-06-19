package com.mrbysco.structurecompass.compat.crafttweaker;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import com.mrbysco.structurecompass.compat.gamestages.GameStagesHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ActionSetStructureStaging implements IUndoableAction {
	private final ResourceLocation structureLocation;
	private final String[] stages;

	public ActionSetStructureStaging(ResourceLocation structureLocation, String[] stages) {
		this.structureLocation = structureLocation;
		this.stages = stages;
	}

	@Override
	public boolean shouldApplyOn(LogicalSide side) {
		return true;
	}

	@Override
	public void undo() {
		Set<String> stages = GameStagesHelper.STRUCTURE_STAGES.computeIfAbsent(structureLocation, s -> new HashSet<>());
		stages.removeAll(Arrays.asList(stages));
	}

	@Override
	public String describeUndo() {
		return "Removing the stages \"" + Arrays.toString(stages) + "\" of structure: \"" + structureLocation + "\"";
	}

	@Override
	public void apply() {
		Set<String> strings = GameStagesHelper.STRUCTURE_STAGES.computeIfAbsent(structureLocation, s -> new HashSet<>());
		strings.addAll(Arrays.asList(stages));
	}

	@Override
	public String describe() {
		return "Set the stages of structure: \"" + structureLocation + "\" to: " + Arrays.toString(stages);
	}
}
