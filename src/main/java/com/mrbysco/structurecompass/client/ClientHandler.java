package com.mrbysco.structurecompass.client;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.client.screen.CompassScreen;
import com.mrbysco.structurecompass.init.StructureItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;
import java.util.List;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(StructureItems.STRUCTURE_COMPASS.get(), new ResourceLocation("angle"), new ItemPropertyFunction() {
				@OnlyIn(Dist.CLIENT)
				private double rotation;
				@OnlyIn(Dist.CLIENT)
				private double rota;
				@OnlyIn(Dist.CLIENT)
				private long lastUpdateTick;

				@OnlyIn(Dist.CLIENT)
				public float call(ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity livingBaseIn, int p_174679_) {
					if (livingBaseIn == null && !stack.isFramed()) {
						return 0.0F;
					} else {
						boolean livingExists = livingBaseIn != null;
						Entity entity = livingExists ? livingBaseIn : stack.getFrame();
						if (worldIn == null && entity.level instanceof ClientLevel) {
							worldIn = (ClientLevel) entity.level;
						}

						double d0;
						if (worldIn.dimensionType().natural()) {
							double d1 = livingExists ? (double) entity.getYRot() : this.getFrameRotation((ItemFrame) entity);
							d1 = Mth.positiveModulo(d1 / 360.0D, 1.0D);
							double d2 = this.getSpawnToAngle(worldIn, (Entity) entity, stack) / (double) ((float) Math.PI * 2F);
							d0 = 0.5D - (d1 - 0.25D - d2);
						} else {
							d0 = Math.random();
						}

						if (livingExists) {
							d0 = this.wobble(worldIn, d0);
						}

						return Mth.positiveModulo((float) d0, 1.0F);
					}
				}

				@OnlyIn(Dist.CLIENT)
				private double wobble(ClientLevel worldIn, double p_185093_2_) {
					if (worldIn.getGameTime() != this.lastUpdateTick) {
						this.lastUpdateTick = worldIn.getGameTime();
						double d0 = p_185093_2_ - this.rotation;
						d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
						this.rota += d0 * 0.1D;
						this.rota *= 0.8D;
						this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0D);
					}

					return this.rotation;
				}

				@OnlyIn(Dist.CLIENT)
				private double getFrameRotation(ItemFrame itemFrame) {
					Direction direction = itemFrame.getDirection();
					int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
					return (double) Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + itemFrame.getRotation() * 45 + i);
				}

				@OnlyIn(Dist.CLIENT)
				private double getSpawnToAngle(ClientLevel worldIn, Entity entityIn, ItemStack stack) {
					BlockPos pos = getBlockPos(stack, worldIn);
					return Math.atan2((double) pos.getZ() - entityIn.getZ(), (double) pos.getX() - entityIn.getX());
				}

				public BlockPos getBlockPos(ItemStack stack, ClientLevel world) {
					if (stack.hasTag()) {
						CompoundTag tag = stack.getTag();
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
		});
	}

	public static void openStructureScreen(InteractionHand hand, ItemStack stack, List<ResourceLocation> allStructures) {
		CompassScreen screen = new CompassScreen(hand, stack, allStructures);
		Minecraft.getInstance().setScreen(screen);
	}
}
