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
import org.jetbrains.annotations.NotNull;

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
				public float call(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingBaseIn, int seed) {
					if (livingBaseIn == null && !stack.isFramed()) {
						return 0.0F;
					} else {
						boolean livingExists = livingBaseIn != null;
						Entity entity = livingExists ? livingBaseIn : stack.getFrame();
						if (clientLevel == null && entity.level instanceof ClientLevel) {
							clientLevel = (ClientLevel) entity.level;
						}

						double d0;
						StructurePos globalPos = getStructurePos(stack);
						if (globalPos != null && clientLevel.dimension().location().equals(globalPos.dimensionLocation())) {
							double d1 = livingExists ? (double) entity.getYRot() : this.getFrameRotation((ItemFrame) entity);
							d1 = Mth.positiveModulo(d1 / 360.0D, 1.0D);
							double d2 = this.getSpawnToAngle((Entity) entity, globalPos.pos()) / (double) ((float) Math.PI * 2F);
							d0 = 0.5D - (d1 - 0.25D - d2);
						} else {
							d0 = Math.random();
						}

						if (livingExists) {
							d0 = this.wobble(clientLevel, d0);
						}

						return Mth.positiveModulo((float) d0, 1.0F);
					}
				}

				@OnlyIn(Dist.CLIENT)
				private double wobble(ClientLevel clientLevel, double p_185093_2_) {
					if (clientLevel.getGameTime() != this.lastUpdateTick) {
						this.lastUpdateTick = clientLevel.getGameTime();
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
				private double getSpawnToAngle(Entity entityIn, @NotNull BlockPos pos) {
					return Math.atan2((double) pos.getZ() - entityIn.getZ(), (double) pos.getX() - entityIn.getX());
				}

				public StructurePos getStructurePos(ItemStack stack) {
					if (stack.hasTag()) {
						final CompoundTag tag = stack.getTag();
						if (tag != null && tag.contains(Reference.structure_found) && tag.getBoolean(Reference.structure_found)) {
							if (tag.contains(Reference.structure_location) && tag.contains(Reference.structure_dimension)) {
								final BlockPos structurePos = BlockPos.of(tag.getLong(Reference.structure_location));
								final ResourceLocation dimensionLoc = ResourceLocation.tryParse(tag.getString(Reference.structure_dimension));
								return new StructurePos(structurePos, dimensionLoc);
							}
						}
					}
					return null;
				}
			});
		});
	}

	public static void openStructureScreen(InteractionHand hand, ItemStack stack, List<ResourceLocation> allStructures) {
		CompassScreen screen = new CompassScreen(hand, stack, allStructures);
		Minecraft.getInstance().setScreen(screen);
	}

	public record StructurePos(BlockPos pos, ResourceLocation dimensionLocation) {
	}
}
