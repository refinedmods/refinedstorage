package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelFullbright;
import com.raoulvdberge.refinedstorage.tile.TileCrafter;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BlockCrafter extends BlockNode {
    public BlockCrafter() {
        super(BlockInfoBuilder.forId("crafter").tileEntity(TileCrafter::new).create());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(
            base,
            new ResourceLocation(RS.ID, "blocks/crafter/cutouts/side_connected"),
            new ResourceLocation(RS.ID, "blocks/crafter/cutouts/side_connected_90"),
            new ResourceLocation(RS.ID, "blocks/crafter/cutouts/side_connected_180"),
            new ResourceLocation(RS.ID, "blocks/crafter/cutouts/side_connected_270"),
            new ResourceLocation(RS.ID, "blocks/crafter/cutouts/front_connected")
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

    /* TODO
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileCrafter && stack.hasDisplayName()) {
                ((TileCrafter) tile).getNode().setDisplayName(stack.getDisplayName().getFormattedText()); // TODO getFormattedText
                ((TileCrafter) tile).getNode().markDirty();
            }
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.CRAFTER, player, world, pos, side);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return super.getDrops(state, builder);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
        super.getDrops(drops, world, pos, state, fortune);

        String displayName = ((TileCrafter) world.getTileEntity(pos)).getNode().getDisplayName();

        if (displayName != null) {
            for (ItemStack drop : drops) {
                if (drop.getItem() == Item.getItemFromBlock(this)) {
                    drop.setStackDisplayName(displayName);
                }
            }
        }
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
