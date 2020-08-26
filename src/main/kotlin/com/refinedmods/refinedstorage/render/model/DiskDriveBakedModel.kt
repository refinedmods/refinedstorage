package com.refinedmods.refinedstorage.render.model

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.refinedmods.refinedstorage.RSBlocks
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.tile.DiskDriveTile
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.util.Direction
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.client.model.data.IModelData
import java.util.*

class DiskDriveBakedModel(base: IBakedModel,
                          disk: IBakedModel,
                          diskNearCapacity: IBakedModel,
                          diskFull: IBakedModel,
                          diskDisconnected: IBakedModel) : DelegateBakedModel(base) {
    private class CacheKey internal constructor(val state: BlockState, @Nullable side: Direction?, diskState: Array<DiskState>, random: Random) {
        val side: Direction?
        val diskState: Array<DiskState>
        val random: Random
        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val cacheKey = o as CacheKey
            if (state != cacheKey.state) {
                return false
            }
            return if (side !== cacheKey.side) {
                false
            } else Arrays.equals(diskState, cacheKey.diskState)
        }

        override fun hashCode(): Int {
            var result = state.hashCode()
            result = 31 * result + if (side != null) side.hashCode() else 0
            result = 31 * result + Arrays.hashCode(diskState)
            return result
        }

        init {
            this.side = side
            this.diskState = diskState
            this.random = random
        }
    }

    private override val base: IBakedModel
    private val disk: IBakedModel
    private val diskNearCapacity: IBakedModel
    private val diskFull: IBakedModel
    private val diskDisconnected: IBakedModel
    private val cache: LoadingCache<CacheKey, List<BakedQuad>> = CacheBuilder.newBuilder().build<CacheKey, List<BakedQuad>>(object : CacheLoader<CacheKey, List<BakedQuad?>>() {
        override fun load(key: CacheKey): List<BakedQuad?> {
            val facing: Direction = key.state.get(RSBlocks.DISK_DRIVE.direction.property)
            val quads: MutableList<BakedQuad?> = ArrayList<Any?>(QuadTransformer.getTransformedQuads(base, facing, null, key.state, key.random, key.side))
            var x = 0
            var y = 0
            for (i in 0..7) {
                if (key.diskState[i] !== DiskState.NONE) {
                    val diskModel: IBakedModel = getDiskModel(key.diskState[i])
                    quads.addAll(QuadTransformer.getTransformedQuads(
                            diskModel,
                            facing,
                            getDiskTranslation(facing, x, y),
                            key.state,
                            key.random,
                            key.side
                    ))
                }
                x++
                if ((i + 1) % 2 == 0) {
                    y++
                    x = 0
                }
            }
            return quads
        }
    })

    private fun getDiskModel(diskState: DiskState): IBakedModel {
        return when (diskState) {
            DiskState.DISCONNECTED -> diskDisconnected
            DiskState.NEAR_CAPACITY -> diskNearCapacity
            DiskState.FULL -> diskFull
            else -> disk
        }
    }

    private fun getDiskTranslation(facing: Direction, x: Int, y: Int): Vector3f {
        val translation = Vector3f()
        if (facing === Direction.NORTH || facing === Direction.SOUTH) {
            translation.add((2f / 16f + x.toFloat() * 7f / 16f) * if (facing === Direction.NORTH) -1 else 1, 0, 0) // Add to X
        } else if (facing === Direction.EAST || facing === Direction.WEST) {
            translation.add(0, 0, (2f / 16f + x.toFloat() * 7f / 16f) * if (facing === Direction.EAST) -1 else 1) // Add to Z
        }
        translation.add(0, -(2f / 16f + y.toFloat() * 3f / 16f), 0) // Remove from Y
        return translation
    }

    @Nonnull
    fun getQuads(@Nullable state: BlockState, @Nullable side: Direction?, @Nonnull rand: Random, @Nonnull data: IModelData): List<BakedQuad> {
        val diskState: Array<DiskState> = data.getData(DiskDriveTile.DISK_STATE_PROPERTY)
                ?: return base.getQuads(state, side, rand, data)
        val key = CacheKey(state, side, diskState, rand)
        return cache.getUnchecked(key)
    }

    init {
        this.base = base
        this.disk = disk
        this.diskNearCapacity = diskNearCapacity
        this.diskFull = diskFull
        this.diskDisconnected = diskDisconnected
    }
}