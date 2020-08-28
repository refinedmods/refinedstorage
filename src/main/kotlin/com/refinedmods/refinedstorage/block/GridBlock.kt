package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.grid.GridType
//import com.refinedmods.refinedstorage.tile.grid.GridTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@RegisterBlock(RS.ID, GridBlock.NORMAL_ID)
@RegisterBlockItem(RS.ID, GridBlock.NORMAL_ID, "MISC")
open class GridBlock(private val type: GridType=GridType.NORMAL):
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES)
//        BlockEntityProvider
{
    companion object {
        const val NORMAL_ID = "grid"
        const val CRAFTING_ID = "crafting_grid"
        const val PATTERN_ID = "pattern_grid"
        const val FLUID_ID = "fluid_grid"

    }
    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

    init {
        defaultState = defaultState.with(CONNECTED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(CONNECTED)
    }

//    override fun createBlockEntity(world: BlockView): BlockEntity? {
//        return NoOpBlockEntity()
        // TODO BlockEntities
//        return GridTile(type)
//    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) { instance().gridManager.openGrid(GridBlockGridFactory.ID, player as ServerPlayerEntity?, pos) }
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }
}

@RegisterBlock(RS.ID, GridBlock.CRAFTING_ID)
@RegisterBlockItem(RS.ID, GridBlock.CRAFTING_ID, "MISC")
class CraftingGridBlock: GridBlock(GridType.CRAFTING)

@RegisterBlock(RS.ID, GridBlock.PATTERN_ID)
@RegisterBlockItem(RS.ID, GridBlock.PATTERN_ID, "MISC")
class PatternGridBlock: GridBlock(GridType.PATTERN)

@RegisterBlock(RS.ID, GridBlock.FLUID_ID)
@RegisterBlockItem(RS.ID, GridBlock.FLUID_ID, "MISC")
class FluidGridBlock: GridBlock(GridType.FLUID)