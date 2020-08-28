package com.refinedmods.refinedstorage.util

//import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.TextColor
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object WorldUtils {
    fun updateBlock(world: World?, pos: BlockPos?) {
        TODO()
        /*
    if (world != null && world.canSetBlock(pos)) {
        val state = world.getBlockState(pos)
        world.notifyBlockUpdate(pos, state, state, 0b11)
    }*/
    }

    /*
fun getItemHandler(tile: BlockEntity?, side: Direction): IItemHandler {
    if (tile == null) {
        return null
    }
    var handler: IItemHandler? = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null)
    if (handler == null) {
        if (side != null && tile is ISidedInventory) {
            handler = SidedInvWrapper(tile as ISidedInventory?, side)
        } else if (tile is IInventory) {
            handler = InvWrapper(tile as IInventory?)
        }
    }
    return handler
}

fun getFluidHandler(@Nullable tile: BlockEntity?, side: Direction?): IFluidHandler? {
    return if (tile != null) {
        tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).orElse(null)
    } else null
}

fun getFakePlayer(world: ServerWorld, @Nullable owner: UUID?): FakePlayer {
    if (owner != null) {
        val profileCache: PlayerProfileCache = world.getServer().getPlayerProfileCache()
        val profile: GameProfile = profileCache.getProfileByUUID(owner)
        if (profile != null) {
            return FakePlayerFactory.get(world, profile)
        }
    }
    return FakePlayerFactory.getMinecraft(world)
}
*/
// TODO is this the best way?
// ah, supposed to be in import com.refinedmods.refinedstorage.render.Styles
    private val RED = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.RED))

    fun sendNoPermissionMessage(player: PlayerEntity) {
        player.sendMessage(
            TranslatableText("misc.refinedstorage.security.no_permission")
                .setStyle(RED), true
        )
    }
/*
    fun rayTracePlayer(world: World, player: PlayerEntity): RayTraceResult {
        val reachDistance: Double = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()
        val base: Vector3d = player.getEyePosition(1.0f)
        val look: Vector3d = player.getLookVec()
        val target: Vector3d = base.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance)
        return world.rayTraceBlocks(RayTraceContext(base, target, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player))
    }
 */
}