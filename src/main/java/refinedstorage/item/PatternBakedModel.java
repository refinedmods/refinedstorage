package refinedstorage.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import refinedstorage.apiimpl.autocrafting.CraftingPattern;
import refinedstorage.gui.GuiBase;

import javax.annotation.Nullable;
import java.util.List;

public class PatternBakedModel implements IBakedModel {
    private IBakedModel patternModel;

    public PatternBakedModel(IBakedModel patternModel) {
        this.patternModel = patternModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return patternModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return patternModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return patternModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return patternModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return patternModel.getParticleTexture();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return patternModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(patternModel.getOverrides().getOverrides()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                CraftingPattern pattern = ItemPattern.getPatternFromCache(world, stack);

                if (GuiBase.isShiftKeyDown() && pattern.isValid() && pattern.getOutputs().size() == 1) {
                    return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(pattern.getOutputs().get(0), world, entity);
                }

                return super.handleItemState(originalModel, stack, world, entity);
            }
        };
    }
}
