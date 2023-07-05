package com.mrbysco.structurecompass.datagen;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.init.StructureItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompassDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new CompassRecipeProvider(packOutput));

			CompassBlockTagProvider blockTags;
			generator.addProvider(event.includeServer(), blockTags =  new CompassBlockTagProvider(packOutput, lookupProvider, helper));
			generator.addProvider(event.includeServer(), new CompassItemTagProvider(packOutput, lookupProvider, blockTags, helper));
		}
	}

	public static class CompassRecipeProvider extends RecipeProvider {

		public CompassRecipeProvider(PackOutput packOutput) {
			super(packOutput);
		}

		@Override
		protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
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
					.unlockedBy("has_compass", has(Items.COMPASS)).save(consumer);
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
