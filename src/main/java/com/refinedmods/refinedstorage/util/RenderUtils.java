package com.refinedmods.refinedstorage.util;

import com.google.common.collect.ImmutableMap;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RenderUtils {
    private RenderUtils() {
    }

    private static Vector3f getRotationVector(Direction direction) {
        return switch (direction) {
            case NORTH -> new Vector3f(0, 0, 0);
            case EAST -> new Vector3f(0, -90, 0);
            case SOUTH -> new Vector3f(0, 180, 0);
            case WEST -> new Vector3f(0, 90, 0);
            case UP -> new Vector3f(90, 0, 180);
            case DOWN -> new Vector3f(-90, 0, 0);
        };
    }

    public static Quaternionf getQuaternion(Direction direction) {
        Vector3f vec = getRotationVector(direction);
        return new Quaternionf().rotateXYZ(
            vec.x() * Mth.DEG_TO_RAD,
            vec.y() * Mth.DEG_TO_RAD,
            vec.z() * Mth.DEG_TO_RAD
        );
    }

    public static String shorten(String text, int length) {
        if (text.length() > length) {
            text = text.substring(0, length) + "...";
        }
        return text;
    }

    public static int getOffsetOnScale(int pos, float scale) {
        float multiplier = (pos / scale);

        return (int) multiplier;
    }

    public static void addCombinedItemsToTooltip(List<Component> tooltip, boolean displayAmount, List<ItemStack> stacks) {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i) {
            if (!stacks.get(i).isEmpty() && !combinedIndices.contains(i)) {
                ItemStack stack = stacks.get(i);

                MutableComponent data = stack.getHoverName().plainCopy();

                int amount = stack.getCount();

                for (int j = i + 1; j < stacks.size(); ++j) {
                    if (API.instance().getComparer().isEqual(stack, stacks.get(j))) {
                        amount += stacks.get(j).getCount();

                        combinedIndices.add(j);
                    }
                }

                if (displayAmount) {
                    data = Component.literal(amount + "x ").append(data);
                }

                tooltip.add(data.setStyle(Styles.GRAY));
            }
        }
    }

    public static void addCombinedFluidsToTooltip(List<Component> tooltip, boolean displayMb, List<FluidStack> stacks) {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i) {
            if (!stacks.get(i).isEmpty() && !combinedIndices.contains(i)) {
                FluidStack stack = stacks.get(i);

                MutableComponent data = stack.getDisplayName().plainCopy();

                int amount = stack.getAmount();

                for (int j = i + 1; j < stacks.size(); ++j) {
                    if (API.instance().getComparer().isEqual(stack, stacks.get(j), IComparer.COMPARE_NBT)) {
                        amount += stacks.get(j).getAmount();

                        combinedIndices.add(j);
                    }
                }

                if (displayMb) {
                    data = Component.literal(API.instance().getQuantityFormatter().formatInBucketForm(amount) + " ").append(data);
                }

                tooltip.add(data.setStyle(Styles.GRAY));
            }
        }
    }

    // @Volatile: From Screen#getTooltipFromItem
    public static List<Component> getTooltipFromItem(ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        return stack.getTooltipLines(minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
    }

    public static boolean inBounds(int x, int y, int w, int h, double ox, double oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }

    public static TextureAtlasSprite getSprite(BakedModel coverModel, BlockState coverState, Direction facing, RandomSource rand) {
        TextureAtlasSprite sprite = null;

        try {
            for (RenderType layer : coverModel.getRenderTypes(coverState, rand, ModelData.EMPTY)) {
                for (BakedQuad bakedQuad : coverModel.getQuads(coverState, facing, rand, ModelData.EMPTY, layer)) {
                    return bakedQuad.getSprite();
                }

                for (BakedQuad bakedQuad : coverModel.getQuads(coverState, null, rand)) {
                    if (sprite == null) {
                        sprite = bakedQuad.getSprite();
                    }

                    if (bakedQuad.getDirection() == facing) {
                        return bakedQuad.getSprite();
                    }
                }
            }
        } catch (Exception e) {
            // NO OP
        }

        if (sprite == null) {
            try {
                sprite = coverModel.getParticleIcon();
            } catch (Exception e) {
                // NO OP
            }
        }

        if (sprite == null) {
            for (BakedQuad quad : Minecraft.getInstance().getModelManager().getMissingModel().getQuads(coverState, facing, rand)) {
                return quad.getSprite();
            }
        }

        return sprite;
    }

    public static ItemTransforms getDefaultBlockTransforms() {
        var thirdperson = getTransform(0, 2.5f, 0, 75, 45, 0, 0.375f);
        return new ItemTransforms(
            thirdperson,
            thirdperson,
            getTransform(0, 0, 0, 0, 225, 0, 0.4f),
            getTransform(0, 0, 0, 0, 45, 0, 0.4f),
            ItemTransform.NO_TRANSFORM,
            getTransform(-3, 1, 0, 30, 225, 0, 0.625f),
            getTransform(0, 3, 0, 0, 0, 0, 0.25f),
            getTransform(0, 0, 0, 0, 0, 0, 0.5f),
            ImmutableMap.of());
    }

    private static ItemTransform getTransform(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new ItemTransform(
            new Vector3f(ax, ay, az),
            new Vector3f(tx / 16, ty / 16, tz / 16),
            new Vector3f(s, s, s)
        );
    }
}
