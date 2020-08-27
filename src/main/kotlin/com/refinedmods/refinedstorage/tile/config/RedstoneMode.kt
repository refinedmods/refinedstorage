package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.nbt.CompoundTag
import java.util.function.BiConsumer
import java.util.function.Function

enum class RedstoneMode {
    IGNORE, HIGH, LOW;

    fun isEnabled(powered: Boolean): Boolean {
        return when (this) {
            IGNORE -> true
            HIGH -> powered
            LOW -> !powered
        }
    }

    fun write(tag: CompoundTag) {
        tag.putInt(NBT, ordinal)
    }

    companion object {
        private const val NBT = "RedstoneMode"
        @JvmStatic
        fun read(tag: CompoundTag): RedstoneMode {
            return if (tag.contains(NBT)) {
                getById(tag.getInt(NBT))
            } else IGNORE
        }

        fun getById(id: Int): RedstoneMode {
            return if (id < 0 || id >= values().size) IGNORE else values()[id]
        }

        @JvmStatic
        fun <T> createParameter(): TileDataParameter<Int, T> where T : BlockEntity, T : IRedstoneConfigurable? {
            return TileDataParameter<Int, T>(
                    value = IGNORE.ordinal,
                    serializer = TrackedDataHandlerRegistry.INTEGER,
                    valueProducer = Function { t: T? -> t!!.redstoneMode.ordinal },
                    valueConsumer = BiConsumer { t: T?, v: Int? -> t!!.redstoneMode = getById(v!!) }
            )
        }
    }
}