package com.mrbysco.structurecompass;

import com.mojang.logging.LogUtils;
import com.mrbysco.structurecompass.client.ClientHandler;
import com.mrbysco.structurecompass.client.KeyHandler;
import com.mrbysco.structurecompass.config.StructureConfig;
import com.mrbysco.structurecompass.init.StructureItems;
import com.mrbysco.structurecompass.network.PacketHandler;
import com.mrbysco.structurecompass.util.AsyncLocator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

@Mod(Reference.MOD_ID)
public class StructureCompass {
	public static final Logger LOGGER = LogUtils.getLogger();

	public StructureCompass(IEventBus eventBus) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureConfig.commonSpec);
		eventBus.register(StructureConfig.class);

		eventBus.addListener(PacketHandler::setupPackets);

		StructureItems.ITEMS.register(eventBus);
		StructureItems.CREATIVE_MODE_TABS.register(eventBus);

		NeoForge.EVENT_BUS.addListener(this::serverAboutToStart);
		NeoForge.EVENT_BUS.addListener(this::onServerStopping);

		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerKeyMappings);

			NeoForge.EVENT_BUS.register(new KeyHandler());
		}
	}

	private void serverAboutToStart(final ServerAboutToStartEvent event) {
		AsyncLocator.handleServerAboutToStartEvent();
	}

	private void onServerStopping(final ServerStoppingEvent event) {
		AsyncLocator.handleServerStoppingEvent();
	}
}
