package com.mrbysco.structurecompass.network.message;

import com.mrbysco.structurecompass.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;

public record SetStructurePayload(InteractionHand hand, Identifier structureLocation,
                                  boolean isTag) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SetStructurePayload> CODEC = StreamCodec.composite(
			Reference.INTERACTION_HAND,
			SetStructurePayload::hand,
			Identifier.STREAM_CODEC,
			SetStructurePayload::structureLocation,
			ByteBufCodecs.BOOL,
			SetStructurePayload::isTag,
			SetStructurePayload::new);

	public static final Type<SetStructurePayload> ID = new Type<>(Reference.modLoc("set_structure"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

}
