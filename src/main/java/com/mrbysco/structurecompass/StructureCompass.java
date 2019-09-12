package com.mrbysco.structurecompass;

import com.mrbysco.structurecompass.config.StructureConfig;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod(Reference.MOD_ID)
public class StructureCompass
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    public static final Marker STRUCTURECOMPASS = MarkerManager.getMarker("STRUCTURECOMPASS");

    public static final ItemGroup tabCompass = new ItemGroup(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.COMPASS);
        }
    };

    public StructureCompass() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StructureConfig.commonSpec);
        FMLJavaModLoadingContext.get().getModEventBus().register(StructureConfig.class);
    }
}
