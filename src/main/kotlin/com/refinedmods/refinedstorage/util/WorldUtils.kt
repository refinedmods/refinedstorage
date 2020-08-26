package com.refinedmods.refinedstorage.util

import com.mojang.authlib.GameProfile
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ISidedInventory
import net.minecraft.server.management.PlayerProfileCache
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceContext
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.wrapper.InvWrapper
import net.minecraftforge.items.wrapper.SidedInvWrapper
import java.util.*

object WorldUtils {
    fun updateBlock(@Nullable world: World?, pos: BlockPos?) {
        if (world != null && world.isBlockPresent(pos)) {
            val state = world.getBlockState(pos)
            world.notifyBlockUpdate(pos, state, state, 1 or 2)
        }
    }

    fun getItemHandler(@Nullable tile: BlockEntity?, side: Direction?): IItemHandler? {
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

    fun sendNoPermissionMessage(player: PlayerEntity) {
        player.sendMessage(TranslationTextComponent("misc.refinedstorage.security.no_permission").setStyle(Styles.RED), player.getUniqueID())
    }

    fun rayTracePlayer(world: World, player: PlayerEntity): RayTraceResult {
        val reachDistance: Double = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()
        val base: Vector3d = player.getEyePosition(1.0f)
        val look: Vector3d = player.getLookVec()
        val target: Vector3d = base.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance)
        return world.rayTraceBlocks(RayTraceContext(base, target, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player))
    }
}