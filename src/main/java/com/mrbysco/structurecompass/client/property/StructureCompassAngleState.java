package com.mrbysco.structurecompass.client.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.structurecompass.component.StructureInfo;
import com.mrbysco.structurecompass.registry.StructureComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class StructureCompassAngleState extends NeedleDirectionHelper {
	public static final MapCodec<StructureCompassAngleState> MAP_CODEC = RecordCodecBuilder.mapCodec(
			wobble -> wobble.group(
							Codec.BOOL.optionalFieldOf("wobble", Boolean.FALSE).forGetter(StructureCompassAngleState::wobble)
					)
					.apply(wobble, StructureCompassAngleState::new)
	);
	private final Wobbler wobbler;

	public StructureCompassAngleState(boolean wobble) {
		super(wobble);
		this.wobbler = this.newWobbler(0.8F);
	}

	@Override
	protected float calculate(ItemStack stack, ClientLevel level, int seed, Entity targetPos) {
		StructureInfo compassData = stack.get(StructureComponents.STRUCTURE_INFO.get());
		GlobalPos globalpos = compassData != null ? compassData.globalPos() : null;
		long i = level.getGameTime();
		boolean valid = isValidCompassTargetPos(targetPos, globalpos);
		return !valid
				? 0.0F
				: this.getRotationTowardsCompassTarget(targetPos, i, globalpos.pos());
	}

	private float getRotationTowardsCompassTarget(Entity entity, long gameTime, BlockPos targetOis) {
		float f = (float) getAngleFromEntityToPos(entity, targetOis);
		float f1 = getWrappedVisualRotationY(entity);
		if (entity instanceof Player player && player.isLocalPlayer() && player.level().tickRateManager().runsNormally()) {
			if (this.wobbler.shouldUpdate(gameTime)) {
				this.wobbler.update(gameTime, 0.5F - (f1 - 0.25F));
			}

			float f3 = f + this.wobbler.rotation();
			return Mth.positiveModulo(f3, 1.0F);
		}

		float f2 = 0.5F - (f1 - 0.25F - f);
		return Mth.positiveModulo(f2, 1.0F);
	}

	private static boolean isValidCompassTargetPos(Entity entity, @Nullable GlobalPos pos) {
		return pos != null
				&& pos.dimension() == entity.level().dimension()
				&& !(pos.pos().distToCenterSqr(entity.position()) < 1.0E-5F);
	}

	private static double getAngleFromEntityToPos(Entity entity, BlockPos pos) {
		Vec3 vec3 = Vec3.atCenterOf(pos);
		return Math.atan2(vec3.z() - entity.getZ(), vec3.x() - entity.getX()) / (float) (Math.PI * 2);
	}

	private static float getWrappedVisualRotationY(Entity entity) {
		return Mth.positiveModulo(entity.getVisualRotationYInDegrees() / 360.0F, 1.0F);
	}
}