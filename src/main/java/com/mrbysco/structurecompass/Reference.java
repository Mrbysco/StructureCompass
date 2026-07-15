package com.mrbysco.structurecompass;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;

public class Reference {
	public static final String MOD_ID = "structurecompass";
	public static final String MOD_PREFIX = MOD_ID + ":";

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static final StreamCodec<ByteBuf, InteractionHand> INTERACTION_HAND = ByteBufCodecs.BOOL.map(
			bool -> bool ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
			hand -> hand == InteractionHand.MAIN_HAND
	);
}
