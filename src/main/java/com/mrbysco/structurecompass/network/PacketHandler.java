package com.mrbysco.structurecompass.network;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.network.handler.ClientPayloadHandler;
import com.mrbysco.structurecompass.network.handler.ServerPayloadHandler;
import com.mrbysco.structurecompass.network.message.OpenCompassPayload;
import com.mrbysco.structurecompass.network.message.SetStructurePayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(Reference.MOD_ID);
		
		registrar.play(OpenCompassPayload.ID, OpenCompassPayload::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleData));
		registrar.play(SetStructurePayload.ID, SetStructurePayload::new, handler -> handler
				.server(ServerPayloadHandler.getInstance()::handleStructureData));
	}
}
