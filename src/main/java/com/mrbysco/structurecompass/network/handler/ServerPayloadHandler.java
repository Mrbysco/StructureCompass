package com.mrbysco.structurecompass.network.handler;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.items.StructureCompassItem;
import com.mrbysco.structurecompass.network.message.SetStructurePayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleStructureData(final SetStructurePayload payload, final PlayPayloadContext context) {
		// Do something with the data, on the main thread
		context.workHandler().submitAsync(() -> {
					context.player().ifPresent(player -> {
						ItemStack stack = player.getItemInHand(payload.hand());
						if (stack.getItem() instanceof StructureCompassItem) {
							CompoundTag tag = new CompoundTag();
							tag.putString(Reference.structure_tag, payload.structureLocation().toString());
							stack.setTag(tag);
						}
					});
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("structurecompass.networking.set_structure.failed", e.getMessage()));
					return null;
				});
	}
}
