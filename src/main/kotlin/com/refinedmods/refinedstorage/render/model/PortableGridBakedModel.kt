package com.refinedmods.refinedstorage.render.model

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.refinedmods.refinedstorage.RSBlocks
import com.refinedmods.refinedstorage.block.PortableGridBlock
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGrid
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridDiskState
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.ItemOverrideList
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import java.util.*

class PortableGridBakedModel(baseConnected: IBakedModel,
                             baseDisconnected: IBakedModel,
                             disk: IBakedModel,
                             diskNearCapacity: IBakedModel,
                             diskFull: IBakedModel,
                             diskDisconnected: IBakedModel) : DelegateBakedModel(baseConnected) {
    private val baseConnected: IBakedModel
    private val baseDisconnected: IBakedModel
    private val disk: IBakedModel
    private val diskNearCapacity: IBakedModel
    private val diskFull: IBakedModel
    private val diskDisconnected: IBakedModel
    private val itemOverrideList = CustomItemOverrideList()
    private val cache: LoadingCache<CacheKey, List<BakedQuad?>> = CacheBuilder.newBuilder().build<CacheKey, List<BakedQuad?>>(object : CacheLoader<CacheKey, List<BakedQuad?>>() {
        override fun load(@Nonnull key: CacheKey): List<BakedQuad?> {
            val direction: Direction = key.state.get(RSBlocks.PORTABLE_GRID.direction.property)
            val active = key.state.get<Boolean>(PortableGridBlock.ACTIVE)
            val diskState = key.state.get<PortableGridDiskState>(PortableGridBlock.DISK_STATE)
            val quads: MutableList<BakedQuad?> = ArrayList<Any?>(QuadTransformer.getTransformedQuads(
                    if (active) baseConnected else baseDisconnected,
                    direction,
                    null,
                    key.state,
                    key.random,
                    key.side
            ))
            val diskModel: IBakedModel? = getDiskModel(diskState)
            if (diskModel != null) {
                quads.addAll(QuadTransformer.getTransformedQuads(diskModel, direction, null, key.state, key.random, key.side))
            }
            return quads
        }
    })

    @Nullable
    private fun getDiskModel(state: PortableGridDiskState): IBakedModel? {
        return when (state) {
            PortableGridDiskState.NORMAL -> disk
            PortableGridDiskState.NEAR_CAPACITY -> diskNearCapacity
            PortableGridDiskState.FULL -> diskFull
            PortableGridDiskState.DISCONNECTED -> diskDisconnected
            PortableGridDiskState.NONE -> null
            else -> null
        }
    }

    override val overrides: ItemOverrideList
        get() = itemOverrideList

    override fun getQuads(@Nullable state: BlockState?, @Nullable side: Direction, rand: Random): List<BakedQuad?>? {
        return if (state != null) {
            cache.getUnchecked(CacheKey(state, side, rand))
        } else super.getQuads(state, side, rand)
    }

    private inner class CustomItemOverrideList : ItemOverrideList() {
        @Nullable
        fun func_239290_a_(model: IBakedModel?, stack: ItemStack?, @Nullable world: ClientWorld?, @Nullable entity: LivingEntity?): IBakedModel {
            val portableGrid = PortableGrid(null, stack, -1)
            return if (portableGrid.isGridActive()) {
                PortableGridItemBakedModel(baseConnected, getDiskModel(portableGrid.diskState))
            } else {
                PortableGridItemBakedModel(baseDisconnected, getDiskModel(portableGrid.diskState))
            }
        }
    }

    private class CacheKey(val state: BlockState, side: Direction, random: Random) {
        val side: Direction
        val random: Random
        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val cacheKey = o as CacheKey
            return state == cacheKey.state && side === cacheKey.side && random == cacheKey.random
        }

        override fun hashCode(): Int {
            return Objects.hash(state, side, random)
        }

        init {
            this.side = side
            this.random = random
        }
    }

    init {
        this.baseConnected = baseConnected
        this.baseDisconnected = baseDisconnected
        this.disk = disk
        this.diskNearCapacity = diskNearCapacity
        this.diskFull = diskFull
        this.diskDisconnected = diskDisconnected
    }
}