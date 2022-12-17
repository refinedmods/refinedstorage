package com.refinedmods.refinedstorage.util;

import com.mojang.authlib.GameProfile;
import com.refinedmods.refinedstorage.render.Styles;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public final class LevelUtils {
    private LevelUtils() {
    }

    public static void updateBlock(@Nullable Level level, BlockPos pos) {
        if (level != null && level.isLoaded(pos)) {
            BlockState state = level.getBlockState(pos);

            level.sendBlockUpdated(pos, state, state, 1 | 2);
        }
    }

    public static IItemHandler getItemHandler(@Nullable BlockEntity blockEntity, Direction side) {
        if (blockEntity == null) {
            return null;
        }

        IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).orElse(null);
        if (handler == null) {
            if (side != null && blockEntity instanceof WorldlyContainer) {
                handler = new SidedInvWrapper((WorldlyContainer) blockEntity, side);
            } else if (blockEntity instanceof Container) {
                handler = new InvWrapper((Container) blockEntity);
            }
        }

        return handler;
    }

    public static IFluidHandler getFluidHandler(@Nullable BlockEntity blockEntity, Direction side) {
        if (blockEntity != null) {
            return blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, side).orElse(null);
        }

        return null;
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
        player.sendSystemMessage(Component.translatable("misc.refinedstorage.security.no_permission").setStyle(Styles.RED));
    }

    public static HitResult rayTracePlayer(Level level, Player player) {
        double reachDistance = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();

        Vec3 base = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        Vec3 target = base.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance);

        return level.clip(new ClipContext(base, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }
}
