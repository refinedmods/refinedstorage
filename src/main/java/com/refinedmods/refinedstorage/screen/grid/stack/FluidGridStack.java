package com.refinedmods.refinedstorage.screen.grid.stack;

import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker;
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.BaseScreen;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FluidGridStack implements IGridStack {
    private static final String ERROR_PLACEHOLDER = "<Error>";
    private static final Logger LOGGER = LogManager.getLogger(FluidGridStack.class);

    private final UUID id;
    private final FluidStack stack;
    private final boolean craftable;
    @Nullable
    private UUID otherId;
    @Nullable
    private StorageTrackerEntry entry;
    private boolean zeroed;

    private Set<String> cachedTags;
    private String cachedName;
    private List<Component> cachedTooltip;
    private String cachedModId;
    private String cachedModName;

    public FluidGridStack(UUID id, @Nullable UUID otherId, FluidStack stack,
                          boolean craftable, @Nullable StorageTrackerEntry entry) {
        this.id = id;
        this.otherId = otherId;
        this.stack = stack;
        this.craftable = craftable;
        this.entry = entry;
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
                LOGGER.warn("Could not retrieve fluid name of {}", BuiltInRegistries.FLUID.getKey(stack.getFluid()));

                cachedName = ERROR_PLACEHOLDER;
            }
        }

        return cachedName;
    }

    @Override
    public String getModId() {
        if (cachedModId == null) {
            ResourceLocation registryName = BuiltInRegistries.FLUID.getKey(stack.getFluid());

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
            cachedTags = BuiltInRegistries.FLUID.getResourceKey(stack.getFluid())
                .flatMap(k -> BuiltInRegistries.FLUID.getHolder(k)
                    .map(holder -> holder.tags()
                        .map(TagKey::location)
                        .map(ResourceLocation::getPath)
                        .collect(Collectors.toSet())))
                .orElse(Collections.emptySet());
        }

        return cachedTags;
    }

    @Override
    public List<Component> getTooltip(boolean bypassCache) {
        if (bypassCache || cachedTooltip == null) {
            List<Component> tooltip;
            try {
                tooltip = Lists.newArrayList(stack.getDisplayName());
            } catch (Throwable t) {
                LOGGER.warn("Could not retrieve fluid tooltip of {}", BuiltInRegistries.FLUID.getKey(stack.getFluid()));
                tooltip = Lists.newArrayList(Component.literal(ERROR_PLACEHOLDER));
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
    public void draw(GuiGraphics graphics, BaseScreen<?> screen, int x, int y) {
        FluidRenderer.INSTANCE.render(graphics, x, y, stack);

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

        screen.renderQuantity(graphics, x, y, text, color);
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

    public static FluidGridStack of(
        final IStorageCache<FluidStack> cache,
        @Nullable final IStackList<FluidStack> craftablesList,
        final IStorageTracker<FluidStack> storageTracker,
        final StackListResult<FluidStack> delta
    ) {
        StackListEntry<FluidStack> craftingEntry = craftablesList == null ? null : cache.getCraftablesList().getEntry(delta.getStack(), IComparer.COMPARE_NBT);
        return new FluidGridStack(
            delta.getId(),
            craftingEntry != null ? craftingEntry.getId() : null,
            delta.getStack().copy(), // copy is very important as the same stack will be shared between server<->client on single player
            false,
            storageTracker.get(delta.getStack())
        );
    }

    public static FluidGridStack of(
        final StackListEntry<FluidStack> entry,
        final IStorageTracker<FluidStack> storageTracker,
        @Nullable final IStackList<FluidStack> oppositeList,
        final boolean craftable
    ) {
        StackListEntry<FluidStack> otherEntry =
            oppositeList == null ? null : oppositeList.getEntry(entry.getStack(), IComparer.COMPARE_NBT);
        return new FluidGridStack(
            entry.getId(),
            otherEntry != null ? otherEntry.getId() : null,
            entry.getStack().copy(), // copy is very important as the same stack will be shared between server<->client on single player
            craftable,
            storageTracker.get(entry.getStack())
        );
    }
}
