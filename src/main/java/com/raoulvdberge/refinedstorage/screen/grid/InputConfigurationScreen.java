package com.raoulvdberge.refinedstorage.screen.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.InputConfigurationContainer;
import com.raoulvdberge.refinedstorage.render.FluidRenderer;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.widget.CheckBoxWidget;
import com.raoulvdberge.refinedstorage.screen.widget.ScrollbarWidget;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.grid.GridTile;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputConfigurationScreen extends BaseScreen {
    private final Screen parent;
    private final ScrollbarWidget scrollbar;

    private final List<Line> lines = new ArrayList<>();

    private int type;
    private int slot;
    private ItemStack item;
    private FluidStack fluid;

    private InputConfigurationScreen(Screen parent, PlayerEntity player, ITextComponent title) {
        super(new InputConfigurationContainer(player), 175, 143, null, title);

        this.parent = parent;
        this.scrollbar = new ScrollbarWidget(this, 155, 20, 12, 89);
    }

    public InputConfigurationScreen(Screen parent, PlayerEntity player, ITextComponent title, ItemStack item, int slot) {
        this(parent, player, title);

        this.type = IType.ITEMS;
        this.slot = slot;
        this.item = item;
        this.fluid = null;
    }

    public InputConfigurationScreen(Screen parent, PlayerEntity player, ITextComponent title, FluidStack fluid, int slot) {
        this(parent, player, title);

        this.type = IType.FLUIDS;
        this.slot = slot;
        this.item = null;
        this.fluid = fluid;
    }

    @Override
    public void onPostInit(int x, int y) {
        Button apply = addButton(x + 7, y + 114, 50, 20, I18n.format("gui.refinedstorage.input_configuration.apply"), true, true, btn -> apply());
        addButton(x + apply.getWidth() + 7 + 4, y + 114, 50, 20, I18n.format("gui.cancel"), true, true, btn -> close());

        lines.clear();

        if (item != null) {
            lines.add(new ItemLine(item));

            for (ResourceLocation owningTag : ItemTags.getCollection().getOwningTags(item.getItem())) {
                lines.add(new TagLine(owningTag, GridTile.ALLOWED_ITEM_TAGS.getValue().get(slot).contains(owningTag)));

                int itemCount = 0;

                ItemListLine line = new ItemListLine();

                for (Item item : ItemTags.getCollection().get(owningTag).getAllElements()) {
                    if (itemCount > 0 && itemCount % 7 == 0) {
                        lines.add(line);
                        line = new ItemListLine();
                    }

                    itemCount++;

                    line.addItem(new ItemStack(item));
                }

                lines.add(line);
            }
        } else if (fluid != null) {
            lines.add(new FluidLine(fluid));

            for (ResourceLocation owningTag : FluidTags.getCollection().getOwningTags(fluid.getFluid())) {
                lines.add(new TagLine(owningTag, GridTile.ALLOWED_FLUID_TAGS.getValue().get(slot).contains(owningTag)));

                int fluidCount = 0;

                FluidListLine line = new FluidListLine();

                for (Fluid fluid : FluidTags.getCollection().get(owningTag).getAllElements()) {
                    if (fluidCount > 0 && fluidCount % 7 == 0) {
                        lines.add(line);
                        line = new FluidListLine();
                    }

                    fluidCount++;

                    line.addFluid(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME));
                }

                lines.add(line);
            }
        }
    }

    @Override
    public void tick(int x, int y) {
        scrollbar.setEnabled(getRows() > getVisibleRows());
        scrollbar.setMaxOffset(getRows() - getVisibleRows());
    }

    private int getRows() {
        return lines.size();
    }

    private int getVisibleRows() {
        return 5;
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/input_configuration.png");

        blit(x, y, 0, 0, xSize, ySize);

        scrollbar.render();
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, title.getFormattedText());

        int x = 8;
        int y = 20;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < lines.size(); ++i) {
            boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + getVisibleRows();

            if (visible) {
                lines.get(i).layoutDependantControls(true, guiLeft + x + 3, guiTop + y + 3);
                lines.get(i).render(x, y);

                y += 18;
            } else {
                lines.get(i).layoutDependantControls(false, -100, -100);
            }
        }

        x = 8;
        y = 20;

        for (int i = 0; i < lines.size(); ++i) {
            boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + getVisibleRows();

            if (visible) {
                lines.get(i).renderTooltip(x, y, mouseX, mouseY);

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
        minecraft.displayGuiScreen(parent);
    }

    private void apply() {
        Set<ResourceLocation> allowed = new HashSet<>();

        for (Line line : lines) {
            if (line instanceof TagLine) {
                TagLine tagLine = (TagLine) line;

                if (tagLine.widget.isChecked()) {
                    allowed.add(tagLine.tagName);
                }
            }
        }

        if (type == IType.ITEMS) {
            List<Set<ResourceLocation>> existing = GridTile.ALLOWED_ITEM_TAGS.getValue();

            existing.set(slot, allowed);

            TileDataManager.setParameter(GridTile.ALLOWED_ITEM_TAGS, existing);
        } else if (type == IType.FLUIDS) {
            List<Set<ResourceLocation>> existing = GridTile.ALLOWED_FLUID_TAGS.getValue();

            existing.set(slot, allowed);

            TileDataManager.setParameter(GridTile.ALLOWED_FLUID_TAGS, existing);
        }

        close();
    }

    private interface Line {
        default void render(int x, int y) {
        }

        default void renderTooltip(int x, int y, int mx, int my) {
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
        public void render(int x, int y) {
            renderItem(x + 3, y + 2, item);
            renderString(x + 4 + 19, y + 7, item.getDisplayName().getFormattedText());
        }
    }

    private class FluidLine implements Line {
        private final FluidStack fluid;

        public FluidLine(FluidStack item) {
            this.fluid = item;
        }

        @Override
        public void render(int x, int y) {
            FluidRenderer.INSTANCE.render(x + 3, y + 2, fluid);
            renderString(x + 4 + 19, y + 7, fluid.getDisplayName().getFormattedText());
        }
    }

    private class TagLine implements Line {
        private final ResourceLocation tagName;
        private final CheckBoxWidget widget;

        public TagLine(ResourceLocation tagName, boolean checked) {
            this.tagName = tagName;
            this.widget = addCheckBox(-100, -100, RenderUtils.shorten(tagName.toString(), 22), checked, (btn) -> {
            });

            widget.setFGColor(0xFF373737);
            widget.setShadow(false);
        }

        @Override
        public void layoutDependantControls(boolean visible, int x, int y) {
            widget.visible = visible;
            widget.x = x;
            widget.y = y;
        }
    }

    private class ItemListLine implements Line {
        private final List<ItemStack> items = new ArrayList<>();

        public ItemListLine addItem(ItemStack stack) {
            items.add(stack);

            return this;
        }

        @Override
        public void render(int x, int y) {
            for (ItemStack item : items) {
                renderItem(x + 3, y, item);

                x += 18;
            }
        }

        @Override
        public void renderTooltip(int x, int y, int mx, int my) {
            for (ItemStack item : items) {
                if (RenderUtils.inBounds(x + 3, y, 16, 16, mx, my)) {
                    InputConfigurationScreen.this.renderTooltip(item, mx, my, RenderUtils.getTooltipFromItem(item));
                }

                x += 18;
            }
        }
    }

    private class FluidListLine implements Line {
        private final List<FluidStack> fluids = new ArrayList<>();

        public FluidListLine addFluid(FluidStack stack) {
            fluids.add(stack);

            return this;
        }

        @Override
        public void render(int x, int y) {
            for (FluidStack fluid : fluids) {
                FluidRenderer.INSTANCE.render(x + 3, y, fluid);

                x += 18;
            }
        }

        @Override
        public void renderTooltip(int x, int y, int mx, int my) {
            for (FluidStack fluid : fluids) {
                if (RenderUtils.inBounds(x + 3, y, 16, 16, mx, my)) {
                    InputConfigurationScreen.this.renderTooltip(mx, my, fluid.getDisplayName().getFormattedText());
                }

                x += 18;
            }
        }
    }
}
