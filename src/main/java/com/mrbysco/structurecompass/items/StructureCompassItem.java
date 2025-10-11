package com.mrbysco.structurecompass.items;

import com.mojang.datafixers.util.Pair;
import com.mrbysco.structurecompass.component.StructureInfo;
import com.mrbysco.structurecompass.config.StructureConfig;
import com.mrbysco.structurecompass.network.message.OpenCompassPayload;
import com.mrbysco.structurecompass.registry.StructureComponents;
import com.mrbysco.structurecompass.util.AsyncLocator;
import com.mrbysco.structurecompass.util.StructureUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
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
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Optional;

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
				((ServerPlayer) playerIn).connection.send(new OpenCompassPayload(hand, stack, allStructures));
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
		if (player.level() instanceof ServerLevel level) {
			if (stack.has(StructureComponents.STRUCTURE)) {
				ResourceLocation structureLocation = stack.get(StructureComponents.STRUCTURE);

				if (structureLocation != null && !StructureUtil.isBlacklisted(structureLocation)) {
					Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
					ResourceKey<Structure> structureKey = ResourceKey.create(Registries.STRUCTURE, structureLocation);
					HolderSet<Structure> featureHolderSet = registry.getHolder(structureKey).map((holders) -> HolderSet.direct(holders)).orElse(null);
					if (featureHolderSet != null) {
						Optional<Holder.Reference<Structure>> optionalHolder = registry.getHolder(structureKey);
						if (optionalHolder.isPresent() && optionalHolder.get().is(Tags.Structures.HIDDEN_FROM_LOCATOR_SELECTION)) {
							stack.remove(StructureComponents.STRUCTURE);
							player.sendSystemMessage(Component.translatable("structurecompass.locate.structure_prohibited").withStyle(ChatFormatting.RED));
							return;
						}
						player.sendSystemMessage(Component.translatable("structurecompass.structure.locating", structureLocation.toString()).withStyle(ChatFormatting.YELLOW));

						boolean findUnexplored = false;
						if (StructureConfig.COMMON.locateUnexplored.get()) {
							findUnexplored = StructureConfig.COMMON.locateUnexplored.get();
						}

						if (StructureConfig.COMMON.locateAsync.get()) {
							var async = AsyncLocator.locate(level,
									featureHolderSet, player.blockPosition(), 100, findUnexplored);
							async.thenOnServerThread(pair -> bindPosition(
									stack,
									structureLocation,
									player,
									level,
									pair
							));
						} else {
							Pair<BlockPos, Holder<Structure>> pair = StructureUtil.findNearestMapStructure(level,
									featureHolderSet, player.blockPosition(), 100, findUnexplored);
							bindPosition(stack, structureLocation, player, level, pair);
						}
					}
				} else {
					player.sendSystemMessage(Component.translatable("structurecompass.locate.fail").withStyle(ChatFormatting.RED));
				}
			} else {
				player.sendSystemMessage(Component.translatable("structurecompass.structure.unset.tooltip").withStyle(ChatFormatting.YELLOW));
			}
		}
	}

	private void bindPosition(ItemStack stack, ResourceLocation boundStructure, Player player, Level level, Pair<BlockPos, Holder<Structure>> pair) {
		BlockPos structurePos = pair != null ? pair.getFirst() : null;
		if (structurePos == null) {
			stack.remove(StructureComponents.STRUCTURE_INFO);
			int range = StructureConfig.COMMON.compassRange.get();
			player.sendSystemMessage(Component.translatable("structurecompass.structure.failed", boundStructure.toString(), range).withStyle(ChatFormatting.RED));
		} else {
			StructureInfo info = new StructureInfo(structurePos, level.dimension());
			stack.set(StructureComponents.STRUCTURE_INFO, info);
			int distance = player.blockPosition().distManhattan(structurePos);
			player.sendSystemMessage(Component.translatable("structurecompass.structure.found", boundStructure.toString(), distance).withStyle(ChatFormatting.GREEN));
		}

		player.getCooldowns().addCooldown(this, 100);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (stack.has(StructureComponents.STRUCTURE)) {
			final String structureName = stack.get(StructureComponents.STRUCTURE).toString();
			if (stack.has(StructureComponents.STRUCTURE_INFO)) {
				StructureInfo info = stack.get(StructureComponents.STRUCTURE_INFO);
				if (context != null && net.minecraft.client.Minecraft.getInstance().player != null &&
						net.minecraft.client.Minecraft.getInstance().player.level().dimension().location().equals(info.dimension().location())) {
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
