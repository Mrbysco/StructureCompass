package com.mrbysco.structurecompass;

import com.mrbysco.structurecompass.client.ClientHandler;
import com.mrbysco.structurecompass.config.StructureConfig;
import com.mrbysco.structurecompass.init.StructureItems;
import com.mrbysco.structurecompass.network.PacketHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class StructureCompass {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public static final ItemGroup tabCompass = new ItemGroup(Reference.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.COMPASS);
        }
    };

    public StructureCompass() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureConfig.commonSpec);
        eventBus.register(StructureConfig.class);

        StructureItems.ITEMS.register(eventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(ClientHandler::onClientSetup));
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
    }
}
