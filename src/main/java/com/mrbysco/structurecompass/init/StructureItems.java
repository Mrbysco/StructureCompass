package com.mrbysco.structurecompass.init;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.StructureCompass;
import com.mrbysco.structurecompass.items.StructureCompassItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StructureItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> STRUCTURE_COMPASS = ITEMS.register("structure_compass", () ->
            new StructureCompassItem(itemBuilder().stacksTo(1).tab(StructureCompass.tabCompass)));

    private static Item.Properties itemBuilder()
    {
        return new Item.Properties();
    }
}
