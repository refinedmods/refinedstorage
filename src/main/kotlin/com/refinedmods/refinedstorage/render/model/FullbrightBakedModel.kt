package com.refinedmods.refinedstorage.render.model

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.util.Direction
import net.minecraft.util.Identifier
import net.minecraftforge.client.model.data.EmptyModelData
import net.minecraftforge.client.model.data.IModelData
import java.util.*

class FullbrightBakedModel(base: IBakedModel, doCaching: Boolean, vararg textures: Identifier) : DelegateBakedModel(base) {
    private val textures: Set<Identifier>
    private val doCaching: Boolean
    @Nonnull
    fun getQuads(@Nullable state: BlockState?, @Nullable side: Direction?, @Nonnull rand: Random?, @Nonnull data: IModelData?): List<BakedQuad?> {
        if (state == null) {
            return base.getQuads(state, side, rand, data)
        }
        return if (!doCaching) {
            transformQuads(base.getQuads(state, side, rand, data), textures)
        } else CACHE.getUnchecked(CacheKey(base, textures, rand, state, side))
    }

    private class CacheKey(base: IBakedModel, textures: Set<Identifier>, random: Random?, state: BlockState, side: Direction?) {
        val base: IBakedModel
        val textures: Set<Identifier>
        val random: Random?
        val state: BlockState
        val side: Direction?
        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val cacheKey = o as CacheKey
            if (cacheKey.side !== side) {
                return false
            }
            return if (state != cacheKey.state) {
                false
            } else true
        }

        override fun hashCode(): Int {
            return state.hashCode() + 31 * if (side != null) side.hashCode() else 0
        }

        init {
            this.base = base
            this.textures = textures
            this.random = random
            this.state = state
            this.side = side
        }
    }

    companion object {
        private val CACHE: LoadingCache<CacheKey, List<BakedQuad?>> = CacheBuilder.newBuilder().build<CacheKey, List<BakedQuad?>>(object : CacheLoader<CacheKey, List<BakedQuad?>>() {
            override fun load(key: CacheKey): List<BakedQuad?> {
                return transformQuads(key.base.getQuads(key.state, key.side, key.random, EmptyModelData.INSTANCE), key.textures)
            }
        })

        private fun transformQuads(oldQuads: List<BakedQuad?>, textures: Set<Identifier>): List<BakedQuad?> {
            val quads: MutableList<BakedQuad?> = ArrayList<Any?>(oldQuads)
            for (i in quads.indices) {
                val quad: BakedQuad? = quads[i]
                if (textures.contains(quad.func_187508_a().getName())) {
                    quads[i] = transformQuad(quad)
                }
            }
            return quads
        }

        private fun transformQuad(quad: BakedQuad?): BakedQuad {
            val vertexData: IntArray = quad.getVertexData().clone()

            // Set lighting to fullbright on all vertices
            vertexData[6] = 0x00F000F0
            vertexData[6 + 8] = 0x00F000F0
            vertexData[6 + 8 + 8] = 0x00F000F0
            vertexData[6 + 8 + 8 + 8] = 0x00F000F0
            return BakedQuad(
                    vertexData,
                    quad.getTintIndex(),
                    quad.getFace(),
                    quad.func_187508_a(),
                    quad.func_239287_f_() // shouldApplyDiffuseLighting
            )
        }
    }

    init {
        this.textures = HashSet<E>(Arrays.asList<Array<Identifier>>(*textures))
        this.doCaching = doCaching
    }
}