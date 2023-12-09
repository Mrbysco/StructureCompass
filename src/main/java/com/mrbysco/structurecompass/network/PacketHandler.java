package com.mrbysco.structurecompass.network;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.network.message.OpenCompassMessage;
import com.mrbysco.structurecompass.network.message.SetStructureMessage;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Reference.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int id = 0;

	public static void init() {
		CHANNEL.registerMessage(id++, SetStructureMessage.class, SetStructureMessage::encode, SetStructureMessage::decode, SetStructureMessage::handle);
		CHANNEL.registerMessage(id++, OpenCompassMessage.class, OpenCompassMessage::encode, OpenCompassMessage::decode, OpenCompassMessage::handle);
	}
}
