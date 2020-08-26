package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridDiskState
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

@RegisterBlock(RS.ID, PortableGridBlock.ID)
open class PortableGridBlock(private val type: PortableGridBlockItem.Type = PortableGridBlockItem.Type.NORMAL):
        BaseBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES),
        BlockEntityProvider
{
    override fun createBlockEntity(world: BlockView?): BlockEntity? = PortableGridTile(type)

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(DISK_STATE)
        builder.add(ACTIVE)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape
            = SHAPE


    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        if (!world.isClient) {
//            instance().gridManager.openGrid(PortableGridBlockGridFactory.ID, player as ServerPlayerEntity?, pos)
//            (world.getBlockEntity(pos) as PortableGridTile?)!!.onOpened()
//        }
        return ActionResult.SUCCESS
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        if (!world.isClient) {
            (world.getBlockEntity(pos) as PortableGridTile?)!!.applyDataFromItemToTile(itemStack)
            (world.getBlockEntity(pos) as PortableGridTile?)!!.updateState()
        }
    }

    companion object {
        const val ID = "portable_grid"
        const val CREATIVE_ID = "creative_portable_grid"
        val DISK_STATE: EnumProperty<PortableGridDiskState> = EnumProperty.of("disk_state", PortableGridDiskState::class.java)
        val ACTIVE: BooleanProperty = BooleanProperty.of("active")
        private val SHAPE: VoxelShape = createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.2, 16.0)
    }

    init {
        defaultState = stateManager.defaultState.with(DISK_STATE, PortableGridDiskState.NONE).with(ACTIVE, false)
    }
}

@RegisterBlock(RS.ID, PortableGridBlock.CREATIVE_ID)
class CreativePortableGridBlock: PortableGridBlock(PortableGridBlockItem.Type.CREATIVE)