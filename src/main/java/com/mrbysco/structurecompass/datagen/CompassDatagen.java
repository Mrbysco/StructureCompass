package com.mrbysco.structurecompass.datagen;

import com.mrbysco.structurecompass.init.StructureItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompassDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new CompassRecipeProvider(packOutput));
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
}
