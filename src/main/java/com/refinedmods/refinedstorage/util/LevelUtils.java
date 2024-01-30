package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.render.Styles;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public final class LevelUtils {
    private LevelUtils() {
    }

    public static void updateBlock(@Nullable Level level, BlockPos pos) {
        if (level != null && level.isLoaded(pos)) {
            BlockState state = level.getBlockState(pos);

            level.sendBlockUpdated(pos, state, state, 1 | 2);
        }
    }

    public static IItemHandler getItemHandler(@Nullable Level level, BlockPos pos, Direction side) {
        if (level == null) {
            return null;
        }
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
        if (handler == null) {
            if (level instanceof WorldlyContainer) {
                handler = new SidedInvWrapper((WorldlyContainer) level, side);
            } else if (level instanceof Container) {
                handler = new InvWrapper((Container) level);
            }
        }
        return handler;
    }

    public static IFluidHandler getFluidHandler(@Nullable Level level, BlockPos pos, Direction side) {
        if (level == null) {
            return null;
        }
        return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
    }

    public static FakePlayer getFakePlayer(ServerLevel level, @Nullable UUID owner) {
        if (owner != null) {
            GameProfileCache profileCache = level.getServer().getProfileCache();

            Optional<GameProfile> profile = profileCache.get(owner);

            if (profile.isPresent()) {
                return FakePlayerFactory.get(level, profile.get());
            }
        }

        return FakePlayerFactory.getMinecraft(level);
    }

    public static void sendNoPermissionMessage(Player player) {
        player.sendSystemMessage(
            Component.translatable("misc.refinedstorage.security.no_permission").setStyle(Styles.RED));
    }

    public static HitResult rayTracePlayer(Level level, Player player) {
        double reachDistance = player.getBlockReach();

        Vec3 base = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        Vec3 target = base.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance);

        return level.clip(new ClipContext(base, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }
}
