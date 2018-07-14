package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelFullbright;
import com.raoulvdberge.refinedstorage.tile.TileCrafter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCrafter extends BlockNode {
    public BlockCrafter() {
        super(BlockInfoBuilder.forId("crafter").tileEntity(TileCrafter::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(
            base,
            RS.ID + ":blocks/crafter/cutouts/side_connected",
            RS.ID + ":blocks/crafter/cutouts/side_connected_90",
            RS.ID + ":blocks/crafter/cutouts/side_connected_180",
            RS.ID + ":blocks/crafter/cutouts/side_connected_270",
            RS.ID + ":blocks/crafter/cutouts/front_connected"
        ));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.ANY_FACE_PLAYER;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileCrafter && stack.hasDisplayName()) {
                ((TileCrafter) tile).getNode().setDisplayName(stack.getDisplayName());
                ((TileCrafter) tile).getNode().markDirty();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.CRAFTER, player, world, pos, side);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        super.getDrops(drops, world, pos, state, fortune);

        String displayName = ((TileCrafter) world.getTileEntity(pos)).getNode().getDisplayName();

        if (displayName != null) {
            for (ItemStack drop : drops) {
                if (drop.getItem() == Item.getItemFromBlock(this)) {
                    drop.setStackDisplayName(displayName);
                }
            }
        }
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
