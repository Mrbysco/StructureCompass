package com.mrbysco.structurecompass.client.property;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class StructureCompassAngle implements RangeSelectItemModelProperty {
	public static final MapCodec<StructureCompassAngle> MAP_CODEC = StructureCompassAngleState.MAP_CODEC.xmap(StructureCompassAngle::new,
			angle -> angle.state);
	private final StructureCompassAngleState state;

	public StructureCompassAngle(boolean wobble) {
		this(new StructureCompassAngleState(wobble));
	}

	private StructureCompassAngle(StructureCompassAngleState state) {
		this.state = state;
	}

	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, int seed) {
		return this.state.get(stack, level, livingEntity, seed);
	}

	@Override
	public MapCodec<? extends RangeSelectItemModelProperty> type() {
		return MAP_CODEC;
	}
}
