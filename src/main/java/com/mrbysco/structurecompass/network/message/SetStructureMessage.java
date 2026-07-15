package com.mrbysco.structurecompass.network.message;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.items.StructureCompassItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SetStructureMessage {
	public InteractionHand hand;
	public ResourceLocation structureLocation;
	public boolean isTag;

	public SetStructureMessage(InteractionHand hand, ResourceLocation structureLocation, boolean isTag) {
		this.hand = hand;
		this.structureLocation = structureLocation;
		this.isTag = isTag;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
		buf.writeResourceLocation(structureLocation);
		buf.writeBoolean(isTag);
	}

	public static SetStructureMessage decode(final FriendlyByteBuf packetBuffer) {
		return new SetStructureMessage(packetBuffer.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, packetBuffer.readResourceLocation(), packetBuffer.readBoolean());
	}

	public void handle(Supplier<Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ItemStack stack = ctx.getSender().getItemInHand(hand);
				if (stack.getItem() instanceof StructureCompassItem) {
					CompoundTag tag = stack.getOrCreateTag();
					tag.putString(Reference.structure_tag, structureLocation.toString());
					tag.putBoolean(Reference.structure_is_tag, isTag);
					stack.setTag(tag);
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
