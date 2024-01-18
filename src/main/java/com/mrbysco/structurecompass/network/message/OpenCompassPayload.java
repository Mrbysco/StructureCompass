package com.mrbysco.structurecompass.network.message;

import com.mrbysco.structurecompass.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record OpenCompassPayload(InteractionHand hand, ItemStack compassStack,
								 List<ResourceLocation> structureList) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "open_compass");

	public OpenCompassPayload(final FriendlyByteBuf packetBuffer) {
		this(packetBuffer.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, packetBuffer.readItem(), new ArrayList<>());
		int size = packetBuffer.readInt();
		for (int i = 0; i < size; i++) {
			this.structureList.add(packetBuffer.readResourceLocation());
		}
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
		buf.writeItem(compassStack);

		buf.writeInt(this.structureList.size());
		for (ResourceLocation location : this.structureList) {
			buf.writeResourceLocation(location);
		}
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
