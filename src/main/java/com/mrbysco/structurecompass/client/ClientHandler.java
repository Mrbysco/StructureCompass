package com.mrbysco.structurecompass.client;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.init.StructureItems;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {

		ItemModelsProperties.register(StructureItems.STRUCTURE_COMPASS.get(), new ResourceLocation("angle"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
			private double rotation;
			@OnlyIn(Dist.CLIENT)
			private double rota;
			@OnlyIn(Dist.CLIENT)
			private long lastUpdateTick;

			@OnlyIn(Dist.CLIENT)
			public float call(ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity livingBaseIn) {
				if (livingBaseIn == null && !stack.isFramed()) {
					return 0.0F;
				} else {
					boolean livingExists = livingBaseIn != null;
					Entity entity = livingExists ? livingBaseIn : stack.getFrame();
					if (worldIn == null && entity.level instanceof ClientWorld) {
						worldIn = (ClientWorld)entity.level;
					}

					double d0;
					if (worldIn.dimensionType().natural()) {
						double d1 = livingExists ? (double)entity.yRot : this.getFrameRotation((ItemFrameEntity)entity);
						d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
						double d2 = this.getSpawnToAngle(worldIn, (Entity)entity, stack) / (double)((float)Math.PI * 2F);
						d0 = 0.5D - (d1 - 0.25D - d2);
					} else {
						d0 = Math.random();
					}

					if (livingExists) {
						d0 = this.wobble(worldIn, d0);
					}

					return MathHelper.positiveModulo((float)d0, 1.0F);
				}
			}

			@OnlyIn(Dist.CLIENT)
			private double wobble(ClientWorld worldIn, double p_185093_2_) {
				if (worldIn.getGameTime() != this.lastUpdateTick) {
					this.lastUpdateTick = worldIn.getGameTime();
					double d0 = p_185093_2_ - this.rotation;
					d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
					this.rota += d0 * 0.1D;
					this.rota *= 0.8D;
					this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
				}

				return this.rotation;
			}

			@OnlyIn(Dist.CLIENT)
			private double getFrameRotation(ItemFrameEntity itemFrame) {
				Direction direction = itemFrame.getDirection();
				int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
				return (double)MathHelper.wrapDegrees(180 + direction.get2DDataValue() * 90 + itemFrame.getRotation() * 45 + i);
			}

			@OnlyIn(Dist.CLIENT)
			private double getSpawnToAngle(ClientWorld worldIn, Entity entityIn, ItemStack stack) {
				BlockPos pos = getBlockPos(stack, worldIn);
				return Math.atan2((double)pos.getZ() - entityIn.getZ(), (double)pos.getX() - entityIn.getX());
			}

			public BlockPos getBlockPos(ItemStack stack, ClientWorld world) {
				if(stack.hasTag()) {
					CompoundNBT tag = stack.getTag();
					if (tag != null && tag.contains(Reference.structure_found)) {
						if (tag.getBoolean(Reference.structure_found)) {
							long structureLong = tag.getLong(Reference.structure_location);
							return BlockPos.of(structureLong);
						}
					}
				}
				return world.dimensionType().natural() ? world.getSharedSpawnPos() : null;
			}
		});
	}
}
