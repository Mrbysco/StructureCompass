package com.mrbysco.structurecompass;

import com.mrbysco.structurecompass.config.StructureConfig;
import com.mrbysco.structurecompass.init.StructureItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class StructureCompass {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public static final ItemGroup tabCompass = new ItemGroup(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.COMPASS);
        }
    };

    public StructureCompass() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureConfig.commonSpec);
        eventBus.register(StructureConfig.class);

        StructureItems.ITEMS.register(eventBus);
    }
}
