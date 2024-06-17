package com.mrbysco.structurecompass.network;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.network.handler.ClientPayloadHandler;
import com.mrbysco.structurecompass.network.handler.ServerPayloadHandler;
import com.mrbysco.structurecompass.network.message.OpenCompassPayload;
import com.mrbysco.structurecompass.network.message.SetStructurePayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(Reference.MOD_ID);

		registrar.playToClient(OpenCompassPayload.ID, OpenCompassPayload.CODEC, ClientPayloadHandler.getInstance()::handleData);
		registrar.playToServer(SetStructurePayload.ID, SetStructurePayload.CODEC, ServerPayloadHandler.getInstance()::handleStructureData);
	}
}
