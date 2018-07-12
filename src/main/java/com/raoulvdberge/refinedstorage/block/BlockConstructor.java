package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.render.collision.constants.ConstantsCable;
import com.raoulvdberge.refinedstorage.render.collision.constants.ConstantsConstructor;
import com.raoulvdberge.refinedstorage.tile.TileConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockConstructor extends BlockCable {
    public BlockConstructor() {
        super(createBuilder("constructor").tileEntity(TileConstructor::new).create());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "connected=false,direction=north,down=false,east=true,north=false,south=false,up=false,west=true"));

        registerCoverAndFullbright(modelRegistration, RS.ID + ":blocks/constructor/cutouts/connected");
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, IBlockState state) {
        List<CollisionGroup> groups = super.getCollisions(tile, state);

        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                groups.add(ConstantsCable.HOLDER_NORTH);
                groups.add(ConstantsConstructor.HEAD_NORTH);
                break;
            case EAST:
                groups.add(ConstantsCable.HOLDER_EAST);
                groups.add(ConstantsConstructor.HEAD_EAST);
                break;
            case SOUTH:
                groups.add(ConstantsCable.HOLDER_SOUTH);
                groups.add(ConstantsConstructor.HEAD_SOUTH);
                break;
            case WEST:
                groups.add(ConstantsCable.HOLDER_WEST);
                groups.add(ConstantsConstructor.HEAD_WEST);
                break;
            case UP:
                groups.add(ConstantsCable.HOLDER_UP);
                groups.add(ConstantsConstructor.HEAD_UP);
                break;
            case DOWN:
                groups.add(ConstantsCable.HOLDER_DOWN);
                groups.add(ConstantsConstructor.HEAD_DOWN);
                break;
        }

        return groups;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        return openNetworkGui(RSGui.CONSTRUCTOR, player, world, pos, side);
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
