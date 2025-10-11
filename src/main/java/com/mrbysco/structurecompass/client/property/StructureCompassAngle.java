package com.mrbysco.structurecompass.client.property;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		return this.state.get(stack, level, owner, seed);
	}

	@NotNull
	@Override
	public MapCodec<? extends RangeSelectItemModelProperty> type() {
		return MAP_CODEC;
	}
}
