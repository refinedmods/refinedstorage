package com.refinedmods.refinedstorage.screen.grid.stack;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ItemGridStack implements IGridStack {
    private final Logger logger = LogManager.getLogger(getClass());

    private UUID id;
    @Nullable
    private UUID otherId;
    private final ItemStack stack;
    private boolean craftable;
    @Nullable
    private StorageTrackerEntry entry;
    private boolean zeroed;

    private Set<String> cachedTags;
    private String cachedName;
    private String cachedModId;
    private String cachedModName;
    private String cachedTooltip;

    public ItemGridStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemGridStack(UUID id, @Nullable UUID otherId, ItemStack stack, boolean craftable, StorageTrackerEntry entry) {
        this.id = id;
        this.otherId = otherId;
        this.stack = stack;
        this.craftable = craftable;
        this.entry = entry;
    }

    public void setZeroed(boolean zeroed) {
        this.zeroed = zeroed;
    }

    @Nullable
    static String getModNameByModId(String modId) {
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(modId);

        return modContainer.map(container -> container.getModInfo().getDisplayName()).orElse(null);
    }

    public ItemStack getStack() {
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
                logger.warn("Could not retrieve item name of " + stack.getItem().toString(), t);

                cachedName = "<Error>";
            }
        }

        return cachedName;
    }

    @Override
    public String getModId() {
        if (cachedModId == null) {
            cachedModId = stack.getItem().getCreatorModId(stack);

            if (cachedModId == null) {
                cachedModId = "<Error>";
            }
        }

        return cachedModId;
    }

    @Override
    public String getModName() {
        if (cachedModName == null) {
            cachedModName = getModNameByModId(getModId());

            if (cachedModName == null) {
                cachedModName = "<Error>";
            }
        }

        return cachedModName;
    }

    @Override
    public Set<String> getTags() {
        if (cachedTags == null) {
            cachedTags = new HashSet<>();

            for (ResourceLocation owningTag : ItemTags.getCollection().getOwningTags(stack.getItem())) {
                cachedTags.add(owningTag.getPath());
            }
        }

        return cachedTags;
    }

    @Override
    public String getTooltip() {
        if (cachedTooltip == null) {
            try {
                cachedTooltip = String.join("\n", RenderUtils.getTooltipFromItem(stack));
            } catch (Throwable t) {
                logger.warn("Could not retrieve item tooltip of " + stack.getItem().toString(), t);

                cachedTooltip = "<Error>";
            }
        }

        return cachedTooltip;
    }

    @Override
    public int getQuantity() {
        // The isCraftable check is needed so sorting is applied correctly
        return isCraftable() ? 0 : stack.getCount();
    }

    @Override
    public String getFormattedFullQuantity() {
        if (zeroed) {
            return "0";
        }

        return API.instance().getQuantityFormatter().format(getQuantity());
    }

    @Override
    public void draw(MatrixStack matrixStack, BaseScreen<?> screen, int x, int y) {
        String text = null;
        int color = RenderSettings.INSTANCE.getSecondaryColor();

        if (zeroed) {
            text = "0";
            color = 16733525;
        } else if (craftable) {
            text = new TranslationTextComponent("gui.refinedstorage.grid.craft").getString();
        } else if (stack.getCount() > 1) {
            text = API.instance().getQuantityFormatter().formatWithUnits(getQuantity());
        }

        screen.renderItem(matrixStack, x, y, stack, true, text, color);
    }

    @Override
    public Object getIngredient() {
        return stack;
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
