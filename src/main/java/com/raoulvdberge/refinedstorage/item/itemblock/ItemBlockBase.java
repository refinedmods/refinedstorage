package com.raoulvdberge.refinedstorage.item.itemblock;

import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.item.info.IItemInfo;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ItemBlockBase extends BlockItem {
    private BlockBase block;

    public ItemBlockBase(BlockBase block, IItemInfo info) {
        super(block, new Item.Properties());

        this.block = block;

        setRegistryName(block.getInfo().getId());
    }
/* TODO
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
    }*/
}
