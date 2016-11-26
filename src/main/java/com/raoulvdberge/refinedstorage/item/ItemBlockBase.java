package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.block.EnumPlacementType;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockBase extends ItemBlock {
    private EnumPlacementType placementType;

    public ItemBlockBase(Block block, EnumPlacementType placementType, boolean subtypes) {
        super(block);

        setRegistryName(block.getRegistryName());

        this.placementType = placementType;

        if (subtypes) {
            setMaxDamage(0);
            setHasSubtypes(true);
        }
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (getHasSubtypes()) {
            return getUnlocalizedName() + "." + stack.getItemDamage();
        }

        return getUnlocalizedName();
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean result = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

        if (result && placementType != null) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileBase) {
                ((TileBase) tile).setDirection(placementType.getFrom(side, pos, player));
            }
        }

        return result;
    }
}
