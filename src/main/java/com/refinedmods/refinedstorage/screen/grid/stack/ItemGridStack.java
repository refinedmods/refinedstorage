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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class ItemGridStack implements IGridStack {
    private static final String ERROR_PLACEHOLDER = "<Error>";

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
    private List<ITextComponent> cachedTooltip;

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
                logger.warn("Could not retrieve item name of {}", stack.getItem().getRegistryName());

                cachedName = ERROR_PLACEHOLDER;
            }
        }

        return cachedName;
    }

    @Override
    public String getModId() {
        if (cachedModId == null) {
            cachedModId = stack.getItem().getCreatorModId(stack);

            if (cachedModId == null) {
                cachedModId = ERROR_PLACEHOLDER;
            }

            cachedModId = cachedModId.toLowerCase().replace(" ", "");
        }

        return cachedModId;
    }

    @Override
    public String getModName() {
        if (cachedModName == null) {
            cachedModName = getModNameByModId(getModId());

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

            for (ResourceLocation owningTag : ItemTags.getCollection().getOwningTags(stack.getItem())) {
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
                tooltip = RenderUtils.getTooltipFromItem(stack);
            } catch (Throwable t) {
                logger.warn("Could not retrieve item tooltip of {}", stack.getItem().getRegistryName());

                tooltip = new ArrayList<>();
                tooltip.add(new StringTextComponent(ERROR_PLACEHOLDER));
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
        return isCraftable() || zeroed ? 0 : stack.getCount();
    }

    @Override
    public void setQuantity(int amount) {
        if (amount <= 0) {
            setZeroed(true);
        } else {
            stack.setCount(amount);
        }
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
            text = I18n.format("gui.refinedstorage.grid.craft");
        } else if (stack.getCount() > 1) {
            text = API.instance().getQuantityFormatter().formatWithUnits(getQuantity());
        }

        screen.renderItem(matrixStack, x, y, stack, true, text, color);
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
