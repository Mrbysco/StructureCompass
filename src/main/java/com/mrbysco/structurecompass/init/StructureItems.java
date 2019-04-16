package com.mrbysco.structurecompass.init;

import com.google.common.base.Preconditions;
import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.items.ItemStructureCompass;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class StructureItems {
    public static ItemStructureCompass village_compass, mineshaft_compass, mansion_compass, igloo_compass,
            desert_pyramid_compass, jungle_pyramid_compass, swamp_hut_compass, stronghold_compass,
            monument_compass, fortress_compass, end_city_compass, ocean_ruin_compass, buried_treasure_compass,
            shipwreck_compass;

    public static ArrayList<Item> ITEMS = new ArrayList<>();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        village_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Village"), "village_compass");
        mineshaft_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Mineshaft"), "mineshaft_compass");
        mansion_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Manson"), "mansion_compass");
        igloo_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Igloo"), "igloo_compass");
        desert_pyramid_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Desert_Pyramid"), "desert_pyramid_compass");
        jungle_pyramid_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Jungle_Pyramid"), "jungle_pyramid_compass");
        swamp_hut_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Swamp_Hut"), "swamp_hut_compass");
        stronghold_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Stronghold"), "stronghold_compass");
        monument_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Monument"), "monument_compass");
        fortress_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Fortress"), "fortress_compass");
        end_city_compass = registerItem(new ItemStructureCompass(itemBuilder(), "EndCity"), "end_city_compass");
        ocean_ruin_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Ocean_Ruin"), "ocean_ruin_compass");
        buried_treasure_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Buried_Treasure"), "buried_treasure_compass");
        shipwreck_compass = registerItem(new ItemStructureCompass(itemBuilder(), "Shipwreck"), "shipwreck_compass");

        registry.registerAll(ITEMS.toArray(new Item[0]));
    }

    public static <T extends Item> T registerItem(T item, String name)
    {
        ITEMS.add(item);

        item.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        Preconditions.checkNotNull(item, "registryName");
        return item;
    }

    private static Item.Properties itemBuilder()
    {
        return new Item.Properties();
    }
}
