package com.mrbysco.structurecompass.items;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.StructureCompass;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemStructureCompass extends Item {
    private String structure;
    private static String structure_found = Reference.MOD_PREFIX + "structureFound";
    private static String structure_location = Reference.MOD_PREFIX + "structurePosition";

    public ItemStructureCompass(Item.Properties builder, String structureName) {
        super(builder.group(StructureCompass.tabCompass));

        this.structure = structureName;

        this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            private double rotation;
            @OnlyIn(Dist.CLIENT)
            private double rota;
            @OnlyIn(Dist.CLIENT)
            private long lastUpdateTick;

            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingBaseIn) {
                if (livingBaseIn == null && !stack.isOnItemFrame()) {
                    return 0.0F;
                } else {
                    boolean livingExists = livingBaseIn != null;
                    Entity entity = livingExists ? livingBaseIn : stack.getItemFrame();
                    if (worldIn == null) {
                        worldIn = ((Entity)entity).world;
                    }

                    double d0;
                    if (worldIn.dimension.isSurfaceWorld()) {
                        double d1 = livingExists ? (double)((Entity)entity).rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
                        d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                        double d2 = this.getSpawnToAngle(worldIn, (Entity)entity, stack) / 6.2831854820251465D;
                        d0 = 0.5D - (d1 - 0.25D - d2);
                    } else {
                        d0 = Math.random();
                    }

                    if (livingExists) {
                        d0 = this.wobble(worldIn, d0);
                    }

                    return MathHelper.positiveModulo((float)d0, 1.0F);
                }
            }

            @OnlyIn(Dist.CLIENT)
            private double wobble(World worldIn, double p_185093_2_) {
                if (worldIn.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = worldIn.getGameTime();
                    double d0 = p_185093_2_ - this.rotation;
                    d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.rota += d0 * 0.1D;
                    this.rota *= 0.8D;
                    this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
                }

                return this.rotation;
            }

            @OnlyIn(Dist.CLIENT)
            private double getFrameRotation(EntityItemFrame itemFrame) {
                return (double)MathHelper.wrapDegrees(180 + itemFrame.facingDirection.getHorizontalIndex() * 90);
            }

            @OnlyIn(Dist.CLIENT)
            private double getSpawnToAngle(IWorld worldIn, Entity entityIn, ItemStack stack) {
                BlockPos pos = getBlockPos(stack, worldIn.getWorld());
                return Math.atan2((double)pos.getZ() - entityIn.posZ, (double)pos.getX() - entityIn.posX);
            }
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if(!worldIn.isRemote) {
            stack = locateStructure(playerIn, worldIn, stack);
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    private ItemStack locateStructure(Entity entityIn, World worldIn, ItemStack stack) {
        NBTTagCompound tag = stack.getTag();

        BlockPos entityPos = new BlockPos(entityIn.getPosition());
        BlockPos structurePos = worldIn.findNearestStructure(structure, entityPos, 300, false);
        if (structurePos == null) {
            BlockPos spawnPos = worldIn.getSpawnPoint();
            tag.setBoolean(structure_found, false);
            tag.setLong(structure_location, spawnPos.toLong());
        } else {
            tag.setBoolean(structure_found, true);
            tag.setLong(structure_location, structurePos.toLong());
        }

        stack.setTag(tag);
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        String structureName = structure.replace("_", " ");
        if(stack.hasTag())
        {
            NBTTagCompound tag = stack.getTag();
            boolean structureFound = tag.getBoolean(structure_found);
            if(structureFound) {
                tooltip.add(new TextComponentTranslation("structurecompass.structure.found.tooltip", new Object[] {structureName, structureName}).applyTextStyle(TextFormatting.GREEN));
            } else {
                tooltip.add(new TextComponentTranslation("structurecompass.structure.failed.tooltip", new Object[] {structureName}).applyTextStyle(TextFormatting.RED));
            }
        } else {
            tooltip.add(new TextComponentTranslation("structurecompass.structure.unset.tooltip", new Object[] {structureName, structureName}).applyTextStyle(TextFormatting.GOLD));
        }
    }

    public BlockPos getBlockPos(ItemStack stack, World world) {
        if(stack.hasTag()) {
            NBTTagCompound tag = stack.getTag();
            if(tag.hasKey(structure_found)) {
                if(tag.getBoolean(structure_found)) {
                    Long structureLong = tag.getLong(structure_location);
                    return BlockPos.fromLong(structureLong);
                } else {
                    return world.getSpawnPoint();
                }
            } else {
                return world.getSpawnPoint();
            }
        } else {
            return world.getSpawnPoint();
        }
    }
}
