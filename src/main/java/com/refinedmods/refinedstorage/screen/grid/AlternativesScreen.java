package com.refinedmods.refinedstorage.screen.grid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.container.AlternativesContainerMenu;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

public class AlternativesScreen extends BaseScreen<AlternativesContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RS.ID, "textures/gui/alternatives.png");

    private static final int VISIBLE_ROWS = 5;

    private final Screen parent;
    private final ScrollbarWidget scrollbar;

    private final List<Line> lines = new ArrayList<>();

    private int type;
    private int slot;
    private ItemStack item;
    private FluidStack fluid;

    private AlternativesScreen(Screen parent, Player player, Component title) {
        super(new AlternativesContainerMenu(player), 175, 143, player.getInventory(), title);

        this.parent = parent;
        this.scrollbar = new ScrollbarWidget(this, 155, 20, 12, 89);
    }

    public AlternativesScreen(Screen parent, Player player, Component title, ItemStack item, int slot) {
        this(parent, player, title);

        this.type = IType.ITEMS;
        this.slot = slot;
        this.item = item;
        this.fluid = null;
    }

    public AlternativesScreen(Screen parent, Player player, Component title, FluidStack fluid, int slot) {
        this(parent, player, title);

        this.type = IType.FLUIDS;
        this.slot = slot;
        this.item = null;
        this.fluid = fluid;
    }

    @Override
    public void onPostInit(int x, int y) {
        lines.clear();

        if (item != null) {
            lines.add(new ItemLine(item));

            Collection<TagKey<Item>> tagsOfItem = ForgeRegistries.ITEMS
                .tags()
                .getReverseTag(item.getItem())
                .stream()
                .flatMap(IReverseTag::getTagKeys)
                .collect(Collectors.toSet());

            for (TagKey<Item> owningTag : tagsOfItem) {
                lines.add(new TagLine(owningTag.location(), GridBlockEntity.ALLOWED_ITEM_TAGS.getValue().get(slot).contains(owningTag.location())));

                int itemCount = 0;

                ItemListLine line = new ItemListLine();

                for (Item itemInTag : ForgeRegistries.ITEMS.tags().getTag(owningTag)) {
                    if (itemCount > 0 && itemCount % 8 == 0) {
                        lines.add(line);
                        line = new ItemListLine();
                    }

                    itemCount++;

                    line.addItem(new ItemStack(itemInTag));
                }

                lines.add(line);
            }
        } else if (fluid != null) {
            lines.add(new FluidLine(fluid));

            Collection<TagKey<Fluid>> tagsOfFluid = ForgeRegistries.FLUIDS
                .tags()
                .getReverseTag(fluid.getFluid())
                .stream()
                .flatMap(IReverseTag::getTagKeys)
                .collect(Collectors.toSet());

            for (TagKey<Fluid> owningTag : tagsOfFluid) {
                lines.add(new TagLine(owningTag.location(), GridBlockEntity.ALLOWED_FLUID_TAGS.getValue().get(slot).contains(owningTag.location())));

                int fluidCount = 0;

                FluidListLine line = new FluidListLine();

                for (Fluid fluidInTag : ForgeRegistries.FLUIDS.tags().getTag(owningTag)) {
                    if (fluidCount > 0 && fluidCount % 8 == 0) {
                        lines.add(line);
                        line = new FluidListLine();
                    }

                    fluidCount++;

                    line.addFluid(new FluidStack(fluidInTag, FluidType.BUCKET_VOLUME));
                }

                lines.add(line);
            }
        }

        // Do an initial layout
        int xx = 8;
        int yy = 20;

        for (int i = 0; i < lines.size(); ++i) {
            boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + VISIBLE_ROWS;

            if (visible) {
                lines.get(i).layoutDependantControls(true, leftPos + xx + 3, topPos + yy + 3);

                yy += 18;
            }
        }

        Button apply = addButton(x + 7, y + 114, 50, 20, Component.translatable("gui.refinedstorage.alternatives.apply"), lines.size() > 1, true, btn -> apply());
        addButton(x + apply.getWidth() + 7 + 4, y + 114, 50, 20, Component.translatable("gui.cancel"), true, true, btn -> close());
    }

    @Override
    public void tick(int x, int y) {
        scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);
    }

    private int getRows() {
        return lines.size();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        scrollbar.render(graphics);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());

        int x = 8;
        int y = 20;

        for (int i = 0; i < lines.size(); ++i) {
            boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + VISIBLE_ROWS;

            if (visible) {
                lines.get(i).layoutDependantControls(true, leftPos + x + 3, topPos + y + 3);
                lines.get(i).render(graphics, x, y);

                y += 18;
            } else {
                lines.get(i).layoutDependantControls(false, -100, -100);
            }
        }

        x = 8;
        y = 20;

        for (int i = 0; i < lines.size(); ++i) {
            boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + VISIBLE_ROWS;

            if (visible) {
                lines.get(i).renderTooltip(graphics, x, y, mouseX, mouseY);

                y += 18;
            }
        }
    }

    @Override
    public void mouseMoved(double mx, double my) {
        scrollbar.mouseMoved(mx, my);

        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return scrollbar.mouseClicked(mx, my, button) || super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        return this.scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close();

            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    private void close() {
        minecraft.setScreen(parent);
    }

    private void apply() {
        Set<ResourceLocation> allowed = new HashSet<>();

        for (Line line : lines) {
            if (line instanceof TagLine) {
                TagLine tagLine = (TagLine) line;

                if (tagLine.widget.selected()) {
                    allowed.add(tagLine.tagName);
                }
            }
        }

        if (type == IType.ITEMS) {
            List<Set<ResourceLocation>> existing = GridBlockEntity.ALLOWED_ITEM_TAGS.getValue();

            existing.set(slot, allowed);

            BlockEntitySynchronizationManager.setParameter(GridBlockEntity.ALLOWED_ITEM_TAGS, existing);
        } else if (type == IType.FLUIDS) {
            List<Set<ResourceLocation>> existing = GridBlockEntity.ALLOWED_FLUID_TAGS.getValue();

            existing.set(slot, allowed);

            BlockEntitySynchronizationManager.setParameter(GridBlockEntity.ALLOWED_FLUID_TAGS, existing);
        }

        close();
    }

    private interface Line {
        default void render(GuiGraphics graphics, int x, int y) {
        }

        default void renderTooltip(GuiGraphics graphics, int x, int y, int mx, int my) {
        }

        default void layoutDependantControls(boolean visible, int x, int y) {
        }
    }

    private class ItemLine implements Line {
        private final ItemStack item;

        public ItemLine(ItemStack item) {
            this.item = item;
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            renderItem(graphics, x + 3, y + 2, item);
            renderString(graphics, x + 4 + 19, y + 7, item.getHoverName().getString());
        }
    }

    private class FluidLine implements Line {
        private final FluidStack fluid;

        public FluidLine(FluidStack item) {
            this.fluid = item;
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y) {
            FluidRenderer.INSTANCE.render(graphics, x + 3, y + 2, fluid);
            renderString(graphics, x + 4 + 19, y + 7, fluid.getDisplayName().getString());
        }
    }

    private class TagLine implements Line {
        private final ResourceLocation tagName;
        private final CheckboxWidget widget;

        public TagLine(ResourceLocation tagName, boolean checked) {
            this.tagName = tagName;
            this.widget = addCheckBox(-100, -100, Component.literal(RenderUtils.shorten(tagName.toString(), 22)), checked, btn -> {
                // NO OP
            });

            widget.setFGColor(0xFF373737);
            widget.setShadow(false);
        }

        @Override
        public void layoutDependantControls(boolean visible, int x, int y) {
            widget.visible = visible;
            widget.setX(x);
            widget.setY(y);
        }
    }

    private class ItemListLine implements Line {
        private final List<ItemStack> items = new ArrayList<>();

        public void addItem(ItemStack stack) {
            items.add(stack);
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y) {
            for (ItemStack itemInList : items) {
                renderItem(graphics, x + 3, y, itemInList);

                x += 17;
            }
        }

        @Override
        public void renderTooltip(GuiGraphics graphics, int x, int y, int mx, int my) {
            for (ItemStack itemInList : items) {
                if (RenderUtils.inBounds(x + 3, y, 16, 16, mx, my)) {
                    AlternativesScreen.this.renderTooltip(graphics, itemInList, mx, my, RenderUtils.getTooltipFromItem(itemInList));
                }

                x += 17;
            }
        }
    }

    private class FluidListLine implements Line {
        private final List<FluidStack> fluids = new ArrayList<>();

        public void addFluid(FluidStack stack) {
            fluids.add(stack);
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y) {
            for (FluidStack fluidInList : fluids) {
                FluidRenderer.INSTANCE.render(graphics, x + 3, y, fluidInList);

                x += 17;
            }
        }

        @Override
        public void renderTooltip(GuiGraphics graphics, int x, int y, int mx, int my) {
            for (FluidStack fluidInList : fluids) {
                if (RenderUtils.inBounds(x + 3, y, 16, 16, mx, my)) {
                    AlternativesScreen.this.renderTooltip(graphics, mx, my, fluidInList.getDisplayName().getString());
                }

                x += 17;
            }
        }
    }
}
