package com.mrbysco.structurecompass.network.message;

import com.mrbysco.structurecompass.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public record SetStructurePayload(InteractionHand hand,
                                  ResourceLocation structureLocation) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SetStructurePayload> CODEC = CustomPacketPayload.codec(
			SetStructurePayload::write,
			SetStructurePayload::new);
	public static final Type<SetStructurePayload> ID = new Type<>(new ResourceLocation(Reference.MOD_ID, "set_structure"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}


	public SetStructurePayload(final RegistryFriendlyByteBuf packetBuffer) {
		this(packetBuffer.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, packetBuffer.readResourceLocation());
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
		buf.writeResourceLocation(structureLocation);
	}
}
