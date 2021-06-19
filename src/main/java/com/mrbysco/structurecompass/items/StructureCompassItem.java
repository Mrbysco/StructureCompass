package com.mrbysco.structurecompass.items;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.config.StructureConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class StructureCompassItem extends Item {

    public StructureCompassItem(Properties builder) {
        super(builder);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack stack = playerIn.getItemInHand(hand);
        if(playerIn.isShiftKeyDown()) {
            if(worldIn.isClientSide) {
                com.mrbysco.structurecompass.client.ClientHandler.openStructureScreen(playerIn, hand, stack);
            }
        } else {
            locateStructure(stack, playerIn);
        }

        return super.use(worldIn, playerIn, hand);
    }

    /*
     * Locates nearby structures
     */
    private void locateStructure(ItemStack stack, PlayerEntity player) {
        if(!player.level.isClientSide) {
            if(stack.hasTag() && stack.getTag().contains(Reference.structure_tag)) {
                ServerWorld worldIn = (ServerWorld) player.level;
                CompoundNBT tag = stack.getTag();

                String boundStructure = tag.getString(Reference.structure_tag);
                ResourceLocation structureLocation = ResourceLocation.tryParse(boundStructure);

                if(structureLocation != null) {
                    Structure<?> structure = ForgeRegistries.STRUCTURE_FEATURES.getValue(structureLocation);
                    if(structure != null) {
                        int searchRange = StructureConfig.COMMON.compassRange.get();

                        boolean findUnexplored = false;
                        if (StructureConfig.COMMON.locateUnexplored.get() != null) {
                            findUnexplored = StructureConfig.COMMON.locateUnexplored.get();
                        }

                        BlockPos structurePos = worldIn.findNearestMapFeature(structure, player.blockPosition(), searchRange, findUnexplored);
                        if (structurePos == null) {
                            BlockPos spawnPos = worldIn.getSharedSpawnPos();
                            tag.putBoolean(Reference.structure_found, false);
                            tag.putLong(Reference.structure_location, spawnPos.asLong());

                            player.sendMessage(new TranslationTextComponent("structurecompass.structure.failed", boundStructure).withStyle(TextFormatting.GOLD), Util.NIL_UUID);
                        } else {
                            tag.putBoolean(Reference.structure_found, true);
                            tag.putLong(Reference.structure_location, structurePos.asLong());
                        }

                        stack.setTag(tag);
                    }
                } else {
                    player.sendMessage(new TranslationTextComponent("structurecompass.locate.fail").withStyle(TextFormatting.RED), Util.NIL_UUID);
                }
            } else {
                player.sendMessage(new TranslationTextComponent("structurecompass.structure.unset.tooltip").withStyle(TextFormatting.YELLOW), Util.NIL_UUID);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            String structureName = tag.getString(Reference.structure_tag);
            boolean structureFound = tag.getBoolean(Reference.structure_found);
            if(structureFound) {
                tooltip.add(new TranslationTextComponent("structurecompass.structure.found.tooltip", structureName, structureName).withStyle(TextFormatting.GREEN));
            } else {
                tooltip.add(new TranslationTextComponent("structurecompass.structure.failed.tooltip", structureName).withStyle(TextFormatting.GOLD));
            }
        } else {
            tooltip.add(new TranslationTextComponent("structurecompass.structure.unset.tooltip").withStyle(TextFormatting.GOLD));
        }
    }
}
