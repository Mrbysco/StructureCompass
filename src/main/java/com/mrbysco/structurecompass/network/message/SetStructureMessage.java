package com.mrbysco.structurecompass.network.message;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.items.StructureCompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SetStructureMessage {
	public Hand hand;
	public ResourceLocation structureLocation;

	public SetStructureMessage(Hand hand, ResourceLocation structureLocation){
		this.hand = hand;
		this.structureLocation = structureLocation;
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(hand == Hand.MAIN_HAND ? 0 : 1);
		buf.writeResourceLocation(structureLocation);
	}

	public static SetStructureMessage decode(final PacketBuffer packetBuffer) {
		return new SetStructureMessage(packetBuffer.readInt() == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND, packetBuffer.readResourceLocation());
	}

	public void handle(Supplier<Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ItemStack stack = ctx.getSender().getItemInHand(hand);
				if(stack.getItem() instanceof StructureCompassItem) {
					CompoundNBT tag = new CompoundNBT();
					tag.putString(Reference.structure_tag, structureLocation.toString());
					stack.setTag(tag);
				}
			}
		});
		ctx.setPacketHandled(true);
	}
}
