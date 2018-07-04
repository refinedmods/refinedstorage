package com.raoulvdberge.refinedstorage.render.model.baked;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.item.ItemCover;
import com.raoulvdberge.refinedstorage.render.QuadBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BakedModelCover implements IBakedModel {
    @Nullable
    private ItemStack stack;
    private IBakedModel base;

    public BakedModelCover(IBakedModel base, @Nullable ItemStack stack) {
        this.base = base;
        this.stack = stack;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new ArrayList<>(base.getQuads(state, side, rand));

        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();

        if (stack != null) {
            ItemStack item = ItemCover.getItem(stack);

            if (!item.isEmpty()) {
                IBlockState coverState = CoverManager.getBlockState(item);

                if (coverState != null) {
                    IBakedModel coverModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(coverState);

                    sprite = BakedModelCableCover.getSprite(coverModel, coverState, side, rand);
                }
            }
        }

        quads.addAll(QuadBuilder.withFormat(DefaultVertexFormats.ITEM)
            .setFrom(0, 0, 0)
            .setTo(16, 16, 2)

            .addFace(EnumFacing.UP, 16, 0, 2, 0, sprite)
            .addFace(EnumFacing.DOWN, 0, 16, 14, 16, sprite)
            .addFace(EnumFacing.EAST, 14, 16, 0, 16, sprite)
            .addFace(EnumFacing.WEST, 0, 2, 0, 16, sprite)

            .addFace(EnumFacing.NORTH, 0, 16, 0, 16, sprite)
            .addFace(EnumFacing.SOUTH, 0, 16, 0, 16, sprite)

            .bake()
        );

        return quads;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return base.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        if (stack != null) {
            return base.getOverrides();
        }

        return new ItemOverrideList(Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return new BakedModelCover(base, stack);
            }
        };
    }

    @Override
    public boolean isAmbientOcclusion(IBlockState state) {
        return base.isAmbientOcclusion(state);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IBakedModel, Matrix4f> matrix = base.handlePerspective(cameraTransformType);
        Pair<? extends IBakedModel, Matrix4f> bakedModel = ForgeHooksClient.handlePerspective(this, cameraTransformType);

        return Pair.of(bakedModel.getKey(), matrix.getRight());
    }

    @Override
    public boolean isAmbientOcclusion() {
        return base.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return base.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return base.getParticleTexture();
    }
}
