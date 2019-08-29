package com.raoulvdberge.refinedstorage.item.itemblock;

import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockBase extends ItemBlock {
    private BlockBase block;

    public ItemBlockBase(BlockBase block, boolean subtypes) {
        super(block);

        this.block = block;

        setRegistryName(block.getInfo().getId());

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
    public String getTranslationKey(ItemStack stack) {
        if (getHasSubtypes()) {
            return getTranslationKey() + "." + stack.getItemDamage();
        }

        return getTranslationKey();
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean result = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

        if (result && block.getDirection() != null) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileBase) {
                ((TileBase) tile).setDirection(block.getDirection().getFrom(side, pos, player));
            }
        }

        return result;
    }
}
