package com.mrbysco.structurecompass.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent.Context;

import java.util.ArrayList;
import java.util.List;

public class OpenCompassMessage {
	public InteractionHand hand;
	public ItemStack compass;
	public List<ResourceLocation> structureList;

	public OpenCompassMessage(InteractionHand hand, ItemStack compassStack, List<ResourceLocation> structureList) {
		this.hand = hand;
		this.compass = compassStack;
		this.structureList = structureList;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
		buf.writeItemStack(compass, false);

		buf.writeInt(this.structureList.size());
		for (ResourceLocation location : this.structureList) {
			buf.writeResourceLocation(location);
		}
	}

	public static OpenCompassMessage decode(final FriendlyByteBuf packetBuffer) {
		InteractionHand hand = packetBuffer.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		ItemStack stack = packetBuffer.readItem();
		List<ResourceLocation> allStructures = new ArrayList<>();
		int size = packetBuffer.readInt();
		for (int i = 0; i < size; i++) {
			allStructures.add(packetBuffer.readResourceLocation());
		}
		return new OpenCompassMessage(hand, stack, allStructures);
	}

	public void handle(Context ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				if (FMLEnvironment.dist.isClient()) {
					com.mrbysco.structurecompass.client.ClientHandler.openStructureScreen(hand, compass, structureList);
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
