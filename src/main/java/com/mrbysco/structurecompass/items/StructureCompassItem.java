package com.mrbysco.structurecompass.items;

import com.mojang.datafixers.util.Pair;
import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.config.StructureConfig;
import com.mrbysco.structurecompass.network.PacketHandler;
import com.mrbysco.structurecompass.network.message.OpenCompassMessage;
import com.mrbysco.structurecompass.util.StructureUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class StructureCompassItem extends Item {

	public StructureCompassItem(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
		ItemStack stack = playerIn.getItemInHand(hand);
		if (playerIn.isShiftKeyDown()) {
			if (!worldIn.isClientSide) {
				List<ResourceLocation> allStructures = StructureUtil.getAvailableStructureList(worldIn);
				PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new OpenCompassMessage(hand, stack, allStructures));
			}
		} else {
			locateStructure(stack, playerIn);
		}

		return super.use(worldIn, playerIn, hand);
	}

	/*
	 * Locates nearby structures
	 */
	private void locateStructure(ItemStack stack, Player player) {
		if (!player.level.isClientSide) {
			if (stack.hasTag() && stack.getTag().contains(Reference.structure_tag)) {
				ServerLevel level = (ServerLevel) player.level;
				CompoundTag tag = stack.getTag();

				String boundStructure = tag.getString(Reference.structure_tag);
				ResourceLocation structureLocation = ResourceLocation.tryParse(boundStructure);

				if (structureLocation != null && !StructureUtil.isBlacklisted(structureLocation)) {
					Registry<Structure> registry = player.getLevel().registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
					ResourceKey<Structure> structureKey = ResourceKey.create(Registry.STRUCTURE_REGISTRY, structureLocation);
					HolderSet<Structure> featureHolderSet = registry.getHolder(structureKey).map((holders) -> HolderSet.direct(holders)).orElse(null);
					if (featureHolderSet != null) {
						int searchRange = StructureConfig.COMMON.compassRange.get();

						boolean findUnexplored = false;
						if (StructureConfig.COMMON.locateUnexplored.get() != null) {
							findUnexplored = StructureConfig.COMMON.locateUnexplored.get();
						}

						Pair<BlockPos, Holder<Structure>> pair = level.getChunkSource().getGenerator().findNearestMapStructure(level,
								featureHolderSet, player.blockPosition(), searchRange, findUnexplored);
						BlockPos structurePos = pair != null ? pair.getFirst() : null;
						if (structurePos == null) {
							tag.putBoolean(Reference.structure_found, false);
							tag.remove(Reference.structure_location);
							tag.remove(Reference.structure_dimension);
							player.sendSystemMessage(Component.translatable("structurecompass.structure.failed", boundStructure).withStyle(ChatFormatting.RED));
						} else {
							tag.putBoolean(Reference.structure_found, true);
							tag.putLong(Reference.structure_location, structurePos.asLong());
							tag.putString(Reference.structure_dimension, level.dimension().location().toString());
							int distance = player.blockPosition().distManhattan(structurePos);
							player.sendSystemMessage(Component.translatable("structurecompass.structure.found", boundStructure, distance).withStyle(ChatFormatting.GREEN));
						}

						stack.setTag(tag);
					}
				} else {
					player.sendSystemMessage(Component.translatable("structurecompass.locate.fail").withStyle(ChatFormatting.RED));
				}
			} else {
				player.sendSystemMessage(Component.translatable("structurecompass.structure.unset.tooltip").withStyle(ChatFormatting.YELLOW));
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			final String structureName = tag.getString(Reference.structure_tag);
			final boolean structureFound = tag.getBoolean(Reference.structure_found);
			if (structureFound) {
				final ResourceLocation structureDimension = ResourceLocation.tryParse(tag.getString(Reference.structure_dimension));
				if (level != null && level.dimension().location().equals(structureDimension)) {
					tooltip.add(Component.translatable("structurecompass.structure.found.tooltip", structureName).withStyle(ChatFormatting.GREEN));
				} else {
					tooltip.add(Component.translatable("structurecompass.structure.wrong_dimension.tooltip", structureName).withStyle(ChatFormatting.RED));
				}
			} else {
				tooltip.add(Component.translatable("structurecompass.structure.failed.tooltip", structureName).withStyle(ChatFormatting.RED));
			}
		} else {
			tooltip.add(Component.translatable("structurecompass.structure.unset.tooltip").withStyle(ChatFormatting.GOLD));
		}
	}
}
