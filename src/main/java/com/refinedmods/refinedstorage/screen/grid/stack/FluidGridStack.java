package com.refinedmods.refinedstorage.screen.grid.stack;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class FluidGridStack implements IGridStack {
    private static final String ERROR_PLACEHOLDER = "<Error>";
    private final Logger logger = LogManager.getLogger(getClass());

    private final UUID id;
    @Nullable
    private UUID otherId;
    private final FluidStack stack;
    @Nullable
    private StorageTrackerEntry entry;
    private final boolean craftable;
    private boolean zeroed;

    private Set<String> cachedTags;
    private String cachedName;
    private List<ITextComponent> cachedTooltip;
    private String cachedModId;
    private String cachedModName;

    public FluidGridStack(UUID id, @Nullable UUID otherId, FluidStack stack, @Nullable StorageTrackerEntry entry, boolean craftable) {
        this.id = id;
        this.otherId = otherId;
        this.stack = stack;
        this.entry = entry;
        this.craftable = craftable;
    }

    public void setZeroed(boolean zeroed) {
        this.zeroed = zeroed;
    }

    public FluidStack getStack() {
        return stack;
    }

    @Override
    public boolean isCraftable() {
        return craftable;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Nullable
    @Override
    public UUID getOtherId() {
        return otherId;
    }

    @Override
    public void updateOtherId(@Nullable UUID otherId) {
        this.otherId = otherId;
    }

    @Override
    public String getName() {
        if (cachedName == null) {
            try {
                cachedName = stack.getDisplayName().getString();
            } catch (Throwable t) {
                logger.warn("Could not retrieve fluid name of {}", stack.getFluid().getRegistryName());

                cachedName = ERROR_PLACEHOLDER;
            }
        }

        return cachedName;
    }

    @Override
    public String getModId() {
        if (cachedModId == null) {
            ResourceLocation registryName = stack.getFluid().getRegistryName();

            if (registryName != null) {
                cachedModId = registryName.getNamespace();
            } else {
                cachedModId = ERROR_PLACEHOLDER;
            }
        }

        return cachedModId;
    }

    @Override
    public String getModName() {
        if (cachedModName == null) {
            cachedModName = ItemGridStack.getModNameByModId(getModId());

            if (cachedModName == null) {
                cachedModName = ERROR_PLACEHOLDER;
            }
        }

        return cachedModName;
    }

    @Override
    public Set<String> getTags() {
        if (cachedTags == null) {
            cachedTags = new HashSet<>();

            for (ResourceLocation owningTag : FluidTags.getAllTags().getMatchingTags(stack.getFluid())) {
                cachedTags.add(owningTag.getPath());
            }
        }

        return cachedTags;
    }

    @Override
    public List<ITextComponent> getTooltip(boolean bypassCache) {
        if (bypassCache || cachedTooltip == null) {
            List<ITextComponent> tooltip;
            try {
                tooltip = Arrays.asList(stack.getDisplayName());
            } catch (Throwable t) {
                logger.warn("Could not retrieve fluid tooltip of {}", stack.getFluid().getRegistryName());

                tooltip = Arrays.asList(new StringTextComponent(ERROR_PLACEHOLDER));
            }

            if (bypassCache) {
                return tooltip;
            } else {
                cachedTooltip = tooltip;
            }
        }

        return cachedTooltip;
    }

    @Override
    public int getQuantity() {
        // The isCraftable check is needed so sorting is applied correctly
        return isCraftable() || zeroed ? 0 : stack.getAmount();
    }

    @Override
    public void setQuantity(int amount) {
        if (amount <= 0) {
            setZeroed(true);
        } else {
            stack.setAmount(amount);
        }
    }

    @Override
    public String getFormattedFullQuantity() {
        if (zeroed) {
            return "0 mB";
        }

        return API.instance().getQuantityFormatter().format(getQuantity()) + " mB";
    }

    @Override
    public void draw(MatrixStack matrixStack, BaseScreen<?> screen, int x, int y) {
        FluidRenderer.INSTANCE.render(matrixStack, x, y, stack);

        String text;
        int color = RenderSettings.INSTANCE.getSecondaryColor();

        if (zeroed) {
            text = "0";
            color = 16733525;
        } else if (isCraftable()) {
            text = I18n.get("gui.refinedstorage.grid.craft");
        } else {
            text = API.instance().getQuantityFormatter().formatInBucketFormWithOnlyTrailingDigitsIfZero(getQuantity());
        }

        screen.renderQuantity(matrixStack, x, y, text, color);
    }

    @Override
    public Object getIngredient() {
        return getStack();
    }

    @Nullable
    @Override
    public StorageTrackerEntry getTrackerEntry() {
        return entry;
    }

    @Override
    public void setTrackerEntry(@Nullable StorageTrackerEntry entry) {
        this.entry = entry;
    }
}
