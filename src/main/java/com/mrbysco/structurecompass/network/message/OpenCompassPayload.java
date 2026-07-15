package com.mrbysco.structurecompass.network.message;

import com.mrbysco.structurecompass.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record OpenCompassPayload(InteractionHand hand, ItemStack compassStack,
                                 List<ResourceLocation> structureList,
                                 List<ResourceLocation> tagList) implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf, OpenCompassPayload> CODEC = StreamCodec.composite(
			Reference.INTERACTION_HAND,
			OpenCompassPayload::hand,
			ItemStack.STREAM_CODEC,
			OpenCompassPayload::compassStack,
			ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
			OpenCompassPayload::structureList,
			ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
			OpenCompassPayload::tagList,
			OpenCompassPayload::new);


	public static final Type<OpenCompassPayload> ID = new Type<>(Reference.modLoc("open_compass"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
