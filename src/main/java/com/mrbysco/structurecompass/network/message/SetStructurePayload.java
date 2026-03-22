package com.mrbysco.structurecompass.network.message;

import com.mrbysco.structurecompass.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;

public record SetStructurePayload(InteractionHand hand,
                                  Identifier structureLocation) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SetStructurePayload> CODEC = CustomPacketPayload.codec(
			SetStructurePayload::write,
			SetStructurePayload::new);
	public static final Type<SetStructurePayload> ID = new Type<>(Reference.modLoc("set_structure"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}


	public SetStructurePayload(final RegistryFriendlyByteBuf packetBuffer) {
		this(packetBuffer.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, packetBuffer.readIdentifier());
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
		buf.writeIdentifier(structureLocation);
	}
}
