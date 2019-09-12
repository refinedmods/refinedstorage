package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;

public class ItemWrench extends ItemBase {
    public ItemWrench() {
        super(new ItemInfo(RS.ID, "wrench"));

        //setMaxStackSize(1);
    }
/* TODO
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            return EnumActionResult.FAIL;
        }

        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileNode && ((TileNode) tile).getNode().getNetwork() != null && !((TileNode) tile).getNode().getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
            WorldUtils.sendNoPermissionMessage(player);

            return EnumActionResult.FAIL;
        }

        IBlockState state = world.getBlockState(pos);

        Block block = state.getBlock();

        if (block instanceof BlockCable && tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            CoverManager manager = ((ICoverable) ((TileNode) tile).getNode()).getCoverManager();

            @SuppressWarnings("deprecation")
            AdvancedRayTraceResult result = AdvancedRayTracer.rayTrace(
                pos,
                AdvancedRayTracer.getStart(player),
                AdvancedRayTracer.getEnd(player),
                ((BlockCable) block).getCollisions(tile, block.getActualState(state, world, pos))
            );

            if (result != null && result.getGroup().getDirection() != null) {
                EnumFacing facingSelected = result.getGroup().getDirection();

                if (manager.hasCover(facingSelected)) {
                    ItemStack cover = manager.getCover(facingSelected).getType().createStack();

                    ItemCover.setItem(cover, manager.getCover(facingSelected).getStack());

                    manager.setCover(facingSelected, null);

                    WorldUtils.updateBlock(world, pos);

                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), cover);

                    return EnumActionResult.SUCCESS;
                }
            }
        }

        block.rotateBlock(world, pos, player.getHorizontalFacing().getOpposite());

        return EnumActionResult.SUCCESS;
    }*/
}