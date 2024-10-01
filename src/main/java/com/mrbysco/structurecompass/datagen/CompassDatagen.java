package com.mrbysco.structurecompass.datagen;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.registry.StructureItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CompassDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new CompassRecipeProvider(packOutput, lookupProvider));

			CompassBlockTagProvider blockTags;
			generator.addProvider(event.includeServer(), blockTags = new CompassBlockTagProvider(packOutput, lookupProvider, helper));
			generator.addProvider(event.includeServer(), new CompassItemTagProvider(packOutput, lookupProvider, blockTags, helper));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeClient(), new CompassLangProvider(packOutput));
		}
	}

	public static class CompassLangProvider extends LanguageProvider {

		public CompassLangProvider(PackOutput packOutput) {
			super(packOutput, Reference.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			add("itemGroup.structurecompass", "Structure Compass");
			addItem(StructureItems.STRUCTURE_COMPASS, "Structure Compass");
			add("structurecompass.screen.selection.title", "Structure Selection");
			add("structurecompass.screen.selection.select", "Select");
			add("structurecompass.screen.selection.selected", "Structure selected, right-click the compass to locate it");
			add("structurecompass.screen.search", "Search");
			add("structurecompass.screen.search.a_to_z", "A-Z");
			add("structurecompass.screen.search.z_to_a", "Z-A");
			add("structurecompass.locate.invalid", "Bound structure invalid. Please re-bind");
			add("structurecompass.locate.fail", "Bound structure could not be located nearby");
			add("structurecompass.locate.distance", "Bound structure is %s blocks away");
			add("structurecompass.locate.toggled", "Structure Compass hud toggled %s");
			add("structurecompass.structure.locating", "Attempting to locate %s, please wait...");
			add("structurecompass.structure.found", "%s has been located %s blocks away, compass is pointing towards the structure");
			add("structurecompass.structure.found.tooltip", "%s has been located, compass is pointing towards the structure");
			add("structurecompass.structure.failed", "%s can not be located within a %s block radius. Please explore further and try again later");
			add("structurecompass.structure.failed.tooltip", "%s can not be located, perhaps try again later");
			add("structurecompass.structure.wrong_dimension.tooltip", "You are not in the dimension in which %s was found");
			add("structurecompass.structure.unset.tooltip", "No structure has been set, shift right-click to select a structure");
			add("category.structurecompass.main", "Structure Compass");
			add("key.structurecompass.hide", "Hide Structure Compass HUD");
			add("structurecompass.networking.set_structure.failed", "Failed to set structure: %s");

			addConfig("title", "Structure Compass Config", null);
			addConfig("general", "General", "General Settings");
			addConfig("compassRange", "Compass Range", "Sets the range in blocks in which the structure compasses can locate structures");
			addConfig("locateUnexplored", "Locate Unexplored", "Defines if the structure compass should only locate unexplored structures. A structure is tagged as explored when the compass is used to find it.");
			addConfig("locateAsync", "Locate Asynchronously", "Defines if the structure compass should locate structures asynchronously");
			addConfig("structureBlacklist", "Structure Blacklist", "Defines which structures can't be searched with the Structure Compass\n(Supports wildcard *, Example: 'minecraft:*' will blacklist anything in the minecraft domain)");
		}

		/**
		 * Add the translation for a config entry
		 *
		 * @param path        The path of the config entry
		 * @param name        The name of the config entry
		 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
		 */
		private void addConfig(String path, String name, @org.jetbrains.annotations.Nullable String description) {
			this.add(Reference.MOD_ID + ".configuration." + path, name);
			if (description != null && !description.isEmpty())
				this.add(Reference.MOD_ID + ".configuration." + path + ".tooltip", description);
		}
	}

	public static class CompassRecipeProvider extends RecipeProvider {

		public CompassRecipeProvider(PackOutput packOutput, CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider) {
			super(packOutput, lookupProvider);
		}

		@Override
		protected void buildRecipes(RecipeOutput recipeOutput) {
			ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StructureItems.STRUCTURE_COMPASS.get())
					.pattern("WTS")
					.pattern("O#D")
					.pattern("MCP")
					.define('#', Items.COMPASS)
					.define('W', Blocks.SNOW_BLOCK)
					.define('T', Blocks.TERRACOTTA)
					.define('S', Blocks.SANDSTONE)
					.define('O', Blocks.OAK_LOG)
					.define('D', Blocks.DARK_OAK_LOG)
					.define('M', Blocks.MOSSY_COBBLESTONE)
					.define('C', Blocks.COBBLESTONE)
					.define('P', Blocks.CARVED_PUMPKIN)
					.unlockedBy("has_compass", has(Items.COMPASS)).save(recipeOutput);
		}
	}

	public static class CompassBlockTagProvider extends BlockTagsProvider {
		public CompassBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
			super(output, lookupProvider, Reference.MOD_ID, existingFileHelper);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
		}
	}

	public static class CompassItemTagProvider extends ItemTagsProvider {

		public CompassItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
									  TagsProvider<Block> blockTagProvider, ExistingFileHelper existingFileHelper) {
			super(output, lookupProvider, blockTagProvider.contentsGetter(), Reference.MOD_ID, existingFileHelper);
		}

		@Override
		public void addTags(HolderLookup.Provider lookupProvider) {
			this.tag(ItemTags.COMPASSES).add(StructureItems.STRUCTURE_COMPASS.get());
		}
	}
}
