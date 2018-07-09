package com.raoulvdberge.refinedstorage.block.info;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import java.util.function.Supplier;

public final class BlockInfoBuilder {
    private Material material = Material.ROCK;
    private String id;
    private String modId;
    private Object modObject;
    private float hardness = 1.9F;
    private SoundType soundType = SoundType.STONE;
    private Supplier<TileBase> tileSupplier;

    private BlockInfoBuilder() {
    }

    public static BlockInfoBuilder forMod(Object modObject, String modId, String id) {
        BlockInfoBuilder builder = new BlockInfoBuilder();

        builder.modObject = modObject;
        builder.modId = modId;
        builder.id = id;

        return builder;
    }

    public static BlockInfoBuilder forId(String id) {
        return forMod(RS.INSTANCE, RS.ID, id);
    }

    public BlockInfoBuilder material(Material material) {
        this.material = material;

        return this;
    }

    public BlockInfoBuilder soundType(SoundType soundType) {
        this.soundType = soundType;

        return this;
    }

    public BlockInfoBuilder hardness(float hardness) {
        this.hardness = hardness;

        return this;
    }

    public BlockInfoBuilder tileEntity(Supplier<TileBase> tileSupplier) {
        this.tileSupplier = tileSupplier;

        return this;
    }

    public BlockInfo create() {
        return new BlockInfo(material, soundType, hardness, id, modId, modObject, tileSupplier);
    }
}
