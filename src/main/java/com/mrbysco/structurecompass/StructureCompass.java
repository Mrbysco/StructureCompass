package com.mrbysco.structurecompass;

import com.mojang.logging.LogUtils;
import com.mrbysco.structurecompass.client.ClientHandler;
import com.mrbysco.structurecompass.client.KeyHandler;
import com.mrbysco.structurecompass.config.StructureConfig;
import com.mrbysco.structurecompass.init.StructureItems;
import com.mrbysco.structurecompass.network.PacketHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(Reference.MOD_ID)
public class StructureCompass {
	public static final Logger LOGGER = LogUtils.getLogger();

	public StructureCompass() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureConfig.commonSpec);
		eventBus.register(StructureConfig.class);

		eventBus.addListener(this::setup);

		StructureItems.ITEMS.register(eventBus);
		StructureItems.CREATIVE_MODE_TABS.register(eventBus);

		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerKeyMappings);

			NeoForge.EVENT_BUS.register(new KeyHandler());
		}
	}

	private void setup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
	}
}
