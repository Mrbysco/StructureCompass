package com.mrbysco.structurecompass.datagen;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.client.property.StructureCompassAngle;
import com.mrbysco.structurecompass.registry.StructureItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.RangeSelectItemModel.Entry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class CompassDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new CompassRecipeProvider.Runner(packOutput, lookupProvider));

		generator.addProvider(true, new CompassBlockTagProvider(packOutput, lookupProvider));
		generator.addProvider(true, new CompassItemTagProvider(packOutput, lookupProvider));
		generator.addProvider(true, new CompassStructureProvider(packOutput, lookupProvider));

		generator.addProvider(true, new CompassModelProvider(packOutput));
		generator.addProvider(true, new CompassLangProvider(packOutput));
	}

	public static class CompassModelProvider extends ModelProvider {

		public CompassModelProvider(PackOutput output) {
			super(output, Reference.MOD_ID);
		}

		@Override
		protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
			generateStructureCompass(itemModels, StructureItems.STRUCTURE_COMPASS.get());
		}

		public void generateStructureCompass(ItemModelGenerators itemModels, Item item) {
			List<Entry> list = itemModels.createCompassModels(item);
			itemModels.itemModelOutput
					.accept(
							item,
							ItemModelUtils.rangeSelect(new StructureCompassAngle(false), 32.0F, list)
					);
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
			add("structurecompass.locate.structure_prohibited", "You are not allowed to locate this structure");
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
			add("structurecompass.networking.open_compass.failed", "Failed to open compass screen: %s");

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

		public CompassRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			super(provider, recipeOutput);
		}

		@Override
		protected void buildRecipes() {
			shaped(RecipeCategory.TOOLS, StructureItems.STRUCTURE_COMPASS.get())
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
					.unlockedBy("has_compass", has(Items.COMPASS)).save(output);
		}

		public static class Runner extends RecipeProvider.Runner {
			public Runner(PackOutput output, CompletableFuture<Provider> completableFuture) {
				super(output, completableFuture);
			}

			@Override
			protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
				return new CompassRecipeProvider(provider, recipeOutput);
			}

			@Override
			public String getName() {
				return "Structure Compass Recipes";
			}
		}
	}

	public static class CompassBlockTagProvider extends BlockTagsProvider {
		public CompassBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, Reference.MOD_ID);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
		}
	}

	public static class CompassItemTagProvider extends ItemTagsProvider {

		public CompassItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, Reference.MOD_ID);
		}

		@Override
		public void addTags(HolderLookup.Provider lookupProvider) {
			this.tag(ItemTags.COMPASSES).add(StructureItems.STRUCTURE_COMPASS.get());
		}
	}

	public static class CompassStructureProvider extends StructureTagsProvider {

		public CompassStructureProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, Reference.MOD_ID);
		}

		@Override
		public void addTags(HolderLookup.Provider lookupProvider) {
			this.tag(Tags.Structures.HIDDEN_FROM_LOCATOR_SELECTION);
		}
	}
}
