package com.mrbysco.structurecompass.network;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.network.message.SetStructureMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Reference.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int id = 0;

	public static void init(){
		CHANNEL.registerMessage(id++, SetStructureMessage.class, SetStructureMessage::encode, SetStructureMessage::decode, SetStructureMessage::handle);
	}
}
