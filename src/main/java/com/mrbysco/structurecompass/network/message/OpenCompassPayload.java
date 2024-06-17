package com.mrbysco.structurecompass.network.message;

import com.mrbysco.structurecompass.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record OpenCompassPayload(InteractionHand hand, ItemStack compassStack,
                                 List<ResourceLocation> structureList) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenCompassPayload> CODEC = CustomPacketPayload.codec(
			OpenCompassPayload::write,
			OpenCompassPayload::new);
	public static final Type<OpenCompassPayload> ID = new Type<>(new ResourceLocation(Reference.MOD_ID, "open_compass"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public OpenCompassPayload(final RegistryFriendlyByteBuf packetBuffer) {
		this(packetBuffer.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, ItemStack.STREAM_CODEC.decode(packetBuffer), new ArrayList<>());
		int size = packetBuffer.readInt();
		for (int i = 0; i < size; i++) {
			this.structureList.add(packetBuffer.readResourceLocation());
		}
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
		ItemStack.STREAM_CODEC.encode(buf, compassStack);

		buf.writeInt(this.structureList.size());
		for (ResourceLocation location : this.structureList) {
			buf.writeResourceLocation(location);
		}
	}
}
