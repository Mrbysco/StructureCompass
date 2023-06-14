package com.mrbysco.structurecompass;

import com.mojang.logging.LogUtils;
import com.mrbysco.structurecompass.client.ClientHandler;
import com.mrbysco.structurecompass.client.KeyHandler;
import com.mrbysco.structurecompass.config.StructureConfig;
import com.mrbysco.structurecompass.init.StructureItems;
import com.mrbysco.structurecompass.network.PacketHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerKeyMappings);

			MinecraftForge.EVENT_BUS.register(new KeyHandler());
		});
	}

	private void setup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
	}
}
