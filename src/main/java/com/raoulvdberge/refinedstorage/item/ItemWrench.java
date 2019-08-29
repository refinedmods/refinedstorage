package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.AdvancedRayTraceResult;
import com.raoulvdberge.refinedstorage.render.collision.AdvancedRayTracer;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWrench extends ItemBase {
    public ItemWrench() {
        super(new ItemInfo(RS.ID, "wrench"));

        setMaxStackSize(1);
    }

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
    }
}