package com.mrbysco.structurecompass.init;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.items.ItemStructureCompass;
import com.mrbysco.structurecompass.items.ItemOldStructureCompass;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StructureItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> test_compass = ITEMS.register("test_compass", () -> new ItemStructureCompass(itemBuilder(), "Igloo"));

    public static final RegistryObject<Item> buried_treasure_compass = ITEMS.register("buried_treasure_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Buried_Treasure"));
    public static final RegistryObject<Item> desert_pyramid_compass = ITEMS.register("desert_pyramid_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Desert_Pyramid"));
    public static final RegistryObject<Item> end_city_compass = ITEMS.register("end_city_compass", () -> new ItemOldStructureCompass(itemBuilder(), "EndCity"));
    public static final RegistryObject<Item> fortress_compass = ITEMS.register("fortress_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Fortress"));
    public static final RegistryObject<Item> igloo_compass = ITEMS.register("igloo_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Igloo"));
    public static final RegistryObject<Item> jungle_pyramid_compass = ITEMS.register("jungle_pyramid_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Jungle_Pyramid"));
    public static final RegistryObject<Item> mansion_compass = ITEMS.register("mansion_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Manson"));
    public static final RegistryObject<Item> mineshaft_compass = ITEMS.register("mineshaft_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Mineshaft"));
    public static final RegistryObject<Item> monument_compass = ITEMS.register("monument_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Monument"));
    public static final RegistryObject<Item> ocean_ruin_compass = ITEMS.register("ocean_ruin_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Ocean_Ruin"));
    public static final RegistryObject<Item> pillager_outpost_compass = ITEMS.register("pillager_outpost_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Pillager_Outpost"));
    public static final RegistryObject<Item> shipwreck_compass = ITEMS.register("shipwreck_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Shipwreck"));
    public static final RegistryObject<Item> stronghold_compass = ITEMS.register("stronghold_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Stronghold"));
    public static final RegistryObject<Item> swamp_hut_compass = ITEMS.register("swamp_hut_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Swamp_Hut"));
    public static final RegistryObject<Item> village_compass = ITEMS.register("village_compass", () -> new ItemOldStructureCompass(itemBuilder(), "Village"));

    private static Item.Properties itemBuilder()
    {
        return new Item.Properties();
    }
}
