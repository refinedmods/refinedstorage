package com.refinedmods.refinedstorage.screen.grid.stack;

import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker;
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.RenderSettings;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemGridStack implements IGridStack {
    private static final String ERROR_PLACEHOLDER = "<Error>";

    private static final Logger LOGGER = LogManager.getLogger(ItemGridStack.class);
    private final ItemStack stack;
    private UUID id;
    @Nullable
    private UUID otherId;
    private boolean craftable;
    @Nullable
    private StorageTrackerEntry entry;
    private boolean zeroed;

    private Set<String> cachedTags;
    private String cachedName;
    private String cachedModId;
    private String cachedModName;
    private List<Component> cachedTooltip;

    public ItemGridStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemGridStack(UUID id, @Nullable UUID otherId, ItemStack stack, boolean craftable,
                         StorageTrackerEntry entry) {
        this.id = id;
        this.otherId = otherId;
        this.stack = stack;
        this.craftable = craftable;
        this.entry = entry;
    }

    @Nullable
    static String getModNameByModId(String modId) {
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(modId);

        return modContainer.map(container -> container.getModInfo().getDisplayName()).orElse(null);
    }

    public void setZeroed(boolean zeroed) {
        this.zeroed = zeroed;
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
                cachedName = stack.getHoverName().getString();
            } catch (Throwable t) {
                LOGGER.warn("Could not retrieve item name of {}", BuiltInRegistries.ITEM.getKey(stack.getItem()));

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
            cachedTags = BuiltInRegistries.ITEM.getResourceKey(stack.getItem())
                .flatMap(k -> BuiltInRegistries.ITEM.getHolder(k)
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
                tooltip = RenderUtils.getTooltipFromItem(stack);
            } catch (Throwable t) {
                LOGGER.warn("Could not retrieve item tooltip of {}", BuiltInRegistries.ITEM.getKey(stack.getItem()));

                tooltip = new ArrayList<>();
                tooltip.add(Component.literal(ERROR_PLACEHOLDER));
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
    public void draw(GuiGraphics graphics, BaseScreen<?> screen, int x, int y) {
        String text = null;
        int color = RenderSettings.INSTANCE.getSecondaryColor();

        if (zeroed) {
            text = "0";
            color = 16733525;
        } else if (craftable) {
            text = I18n.get("gui.refinedstorage.grid.craft");
        } else if (stack.getCount() > 1) {
            text = API.instance().getQuantityFormatter().formatWithUnits(getQuantity());
        }

        screen.renderItem(graphics, x, y, stack, true, text, color);
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

    public static ItemGridStack of(
        final IStorageCache<ItemStack> cache,
        @Nullable final IStackList<ItemStack> craftablesList,
        final IStorageTracker<ItemStack> storageTracker,
        final StackListResult<ItemStack> delta
    ) {
        StackListEntry<ItemStack> craftingEntry = craftablesList == null ? null : cache.getCraftablesList().getEntry(delta.getStack(), IComparer.COMPARE_NBT);
        return new ItemGridStack(
            delta.getId(),
            craftingEntry != null ? craftingEntry.getId() : null,
            delta.getStack().copy(), // copy is very important as the same stack will be shared between server<->client on single player
            false,
            storageTracker.get(delta.getStack())
        );
    }

    public static ItemGridStack of(
        final StackListEntry<ItemStack> entry,
        final IStorageTracker<ItemStack> storageTracker,
        @Nullable final IStackList<ItemStack> oppositeList,
        final boolean craftable
    ) {
        StackListEntry<ItemStack> otherEntry = oppositeList == null ? null : oppositeList.getEntry(entry.getStack(), IComparer.COMPARE_NBT);
        return new ItemGridStack(
            entry.getId(),
            otherEntry != null ? otherEntry.getId() : null,
            entry.getStack().copy(), // copy is very important as the same stack will be shared between server<->client on single player
            craftable,
            storageTracker.get(entry.getStack())
        );
    }
}
