package com.rwtema.funkylocomotion.api;

import net.minecraft.block.Block;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public abstract class FunkyRegistry {
    @Nullable
    public static FunkyRegistry INSTANCE = null;

    public abstract void registerMoveFactoryBlock(Block b, IMoveFactory factory);

    public abstract void registerMoveFactoryTileEntityClass(Class<?> tile, IMoveFactory factory);

    public abstract void registerMoveFactoryBlockClass(Class<? extends Block> b, IMoveFactory factory);

    public abstract <T> void registerProxy(Object object, Capability<T> capability, T type);
}
