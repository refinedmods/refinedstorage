package com.refinedmods.refinedstorage.screen.grid;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.AlternativesContainer;
import com.refinedmods.refinedstorage.render.FluidRenderer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlternativesScreen extends BaseScreen<AlternativesContainer> {
    private static final int VISIBLE_ROWS = 5;

    private final Screen parent;
    private final ScrollbarWidget scrollbar;

    private final List<Line> lines = new ArrayList<>();

    private int type;
    private int slot;
    private ItemStack item;
    private FluidStack fluid;

    private AlternativesScreen(Screen parent, PlayerEntity player, ITextComponent title) {
        super(new AlternativesContainer(player), 175, 143, null, title);

        this.parent = parent;
        this.scrollbar = new ScrollbarWidget(this, 155, 20, 12, 89);
    }

    public AlternativesScreen(Screen parent, PlayerEntity player, ITextComponent title, ItemStack item, int slot) {
        this(parent, player, title);

        this.type = IType.ITEMS;
        this.slot = slot;
        this.item = item;
        this.fluid = null;
    }

    public AlternativesScreen(Screen parent, PlayerEntity player, ITextComponent title, FluidStack fluid, int slot) {
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

            for (ResourceLocation owningTag : ItemTags.getCollection().getOwningTags(item.getItem())) {
                lines.add(new TagLine(owningTag, GridTile.ALLOWED_ITEM_TAGS.getValue().get(slot).contains(owningTag)));

                int itemCount = 0;

                ItemListLine line = new ItemListLine();

                for (Item itemInTag : ItemTags.getCollection().get(owningTag).getAllElements()) {
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

            for (ResourceLocation owningTag : FluidTags.getCollection().getOwningTags(fluid.getFluid())) {
                lines.add(new TagLine(owningTag, GridTile.ALLOWED_FLUID_TAGS.getValue().get(slot).contains(owningTag)));

                int fluidCount = 0;

                FluidListLine line = new FluidListLine();

                for (Fluid fluidInTag : FluidTags.getCollection().get(owningTag).getAllElements()) {
                    if (fluidCount > 0 && fluidCount % 8 == 0) {
                        lines.add(line);
                        line = new FluidListLine();
                    }

                    fluidCount++;

                    line.addFluid(new FluidStack(fluidInTag, FluidAttributes.BUCKET_VOLUME));
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
                lines.get(i).layoutDependantControls(true, guiLeft + xx + 3, guiTop + yy + 3);

                yy += 18;
            }
        }

        Button apply = addButton(x + 7, y + 114, 50, 20, new TranslationTextComponent("gui.refinedstorage.alternatives.apply"), lines.size() > 1, true, btn -> apply());
        addButton(x + apply.getWidth() + 7 + 4, y + 114, 50, 20, new TranslationTextComponent("gui.cancel"), true, true, btn -> close());
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
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/alternatives.png");

        blit(matrixStack, x, y, 0, 0, xSize, ySize);

        scrollbar.render(matrixStack);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());

        int x = 8;
        int y = 20;

        for (int i = 0; i < lines.size(); ++i) {
            boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + VISIBLE_ROWS;

            if (visible) {
                lines.get(i).layoutDependantControls(true, guiLeft + x + 3, guiTop + y + 3);
                lines.get(i).render(matrixStack, x, y);

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
                lines.get(i).renderTooltip(matrixStack, x, y, mouseX, mouseY);

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
        default void render(MatrixStack matrixStack, int x, int y) {
        }

        default void renderTooltip(MatrixStack matrixStack, int x, int y, int mx, int my) {
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
        public void render(MatrixStack matrixStack, int x, int y) {
            RenderSystem.color4f(1, 1, 1, 1);
            renderItem(matrixStack, x + 3, y + 2, item);
            renderString(matrixStack, x + 4 + 19, y + 7, item.getDisplayName().getString());
        }
    }

    private class FluidLine implements Line {
        private final FluidStack fluid;

        public FluidLine(FluidStack item) {
            this.fluid = item;
        }

        @Override
        public void render(MatrixStack matrixStack, int x, int y) {
            FluidRenderer.INSTANCE.render(matrixStack, x + 3, y + 2, fluid);
            renderString(matrixStack, x + 4 + 19, y + 7, fluid.getDisplayName().getString());
        }
    }

    private class TagLine implements Line {
        private final ResourceLocation tagName;
        private final CheckboxWidget widget;

        public TagLine(ResourceLocation tagName, boolean checked) {
            this.tagName = tagName;
            this.widget = addCheckBox(-100, -100, new StringTextComponent(RenderUtils.shorten(tagName.toString(), 22)), checked, btn -> {
                // NO OP
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

        public void addItem(ItemStack stack) {
            items.add(stack);
        }

        @Override
        public void render(MatrixStack matrixStack, int x, int y) {
            for (ItemStack itemInList : items) {
                renderItem(matrixStack, x + 3, y, itemInList);

                x += 17;
            }
        }

        @Override
        public void renderTooltip(MatrixStack matrixStack, int x, int y, int mx, int my) {
            for (ItemStack itemInList : items) {
                if (RenderUtils.inBounds(x + 3, y, 16, 16, mx, my)) {
                    AlternativesScreen.this.renderTooltip(matrixStack, itemInList, mx, my, RenderUtils.getTooltipFromItem(itemInList));
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
        public void render(MatrixStack matrixStack, int x, int y) {
            for (FluidStack fluidInList : fluids) {
                FluidRenderer.INSTANCE.render(matrixStack, x + 3, y, fluidInList);

                x += 17;
            }
        }

        @Override
        public void renderTooltip(MatrixStack matrixStack, int x, int y, int mx, int my) {
            for (FluidStack fluidInList : fluids) {
                if (RenderUtils.inBounds(x + 3, y, 16, 16, mx, my)) {
                    AlternativesScreen.this.renderTooltip(matrixStack, mx, my, fluidInList.getDisplayName().getString());
                }

                x += 17;
            }
        }
    }
}
