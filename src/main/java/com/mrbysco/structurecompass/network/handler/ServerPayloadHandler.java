package com.mrbysco.structurecompass.network.handler;

import com.mrbysco.structurecompass.items.StructureCompassItem;
import com.mrbysco.structurecompass.network.message.SetStructurePayload;
import com.mrbysco.structurecompass.registry.StructureComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleStructureData(final SetStructurePayload payload, final IPayloadContext context) {
		// Do something with the data, on the main thread
		context.enqueueWork(() -> {
					Player player = context.player();
					if (player != null) {
						ItemStack stack = player.getItemInHand(payload.hand());
						if (stack.getItem() instanceof StructureCompassItem) {
							stack.set(StructureComponents.STRUCTURE, payload.structureLocation());
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("structurecompass.networking.set_structure.failed", e.getMessage()));
					return null;
				});
	}
}
