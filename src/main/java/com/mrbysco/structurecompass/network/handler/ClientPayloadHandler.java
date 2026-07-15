package com.mrbysco.structurecompass.network.handler;

import com.mrbysco.structurecompass.network.message.OpenCompassPayload;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleData(final OpenCompassPayload data, final IPayloadContext context) {
		context.enqueueWork(() -> {
					com.mrbysco.structurecompass.client.ClientHandler.openStructureScreen(data.hand(), data.compassStack(), data.structureList(), data.tagList());
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("structurecompass.networking.open_compass.failed", e.getMessage()));
					return null;
				});
	}
}
