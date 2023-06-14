package com.mrbysco.structurecompass.init;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.items.StructureCompassItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StructureItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);

	public static final RegistryObject<Item> STRUCTURE_COMPASS = ITEMS.register("structure_compass", () ->
			new StructureCompassItem(itemBuilder().stacksTo(1)));

	private static Item.Properties itemBuilder() {
		return new Item.Properties();
	}

	public static final RegistryObject<CreativeModeTab> COMPASS_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> StructureItems.STRUCTURE_COMPASS.get().getDefaultInstance())
			.title(Component.translatable("itemGroup.structurecompass"))
			.displayItems((parameters, output) -> {
				output.accept(StructureItems.STRUCTURE_COMPASS.get());
			}).build());
}
