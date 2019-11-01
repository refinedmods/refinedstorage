package com.raoulvdberge.refinedstorage.screen.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.InputConfigurationContainer;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.widget.CheckBoxWidget;
import com.raoulvdberge.refinedstorage.screen.widget.ScrollbarWidget;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InputConfigurationScreen extends BaseScreen {
    private final Screen parent;
    private final List<Line> lines = new ArrayList<>();
    private final ScrollbarWidget scrollbar;

    private final ItemStack stack;

    public InputConfigurationScreen(Screen parent, PlayerEntity player, ITextComponent title, ItemStack stack) {
        super(new InputConfigurationContainer(player), 175, 143, null, title);

        this.parent = parent;

        this.scrollbar = new ScrollbarWidget(this, 155, 20, 12, 89);

        this.stack = stack;
    }

    @Override
    public void onPostInit(int x, int y) {
        Button apply = addButton(x + 7, y + 114, 50, 20, I18n.format("gui.refinedstorage.input_configuration.apply"), true, true, btn -> close());
        addButton(x + apply.getWidth() + 7 + 4, y + 114, 50, 20, I18n.format("gui.cancel"), true, true, btn -> close());

        lines.clear();

        lines.add(new ItemLine(stack));

        for (ResourceLocation owningTag : ItemTags.getCollection().getOwningTags(stack.getItem())) {
            lines.add(new ItemTagLine(owningTag));

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

    private class ItemTagLine implements Line {
        private final CheckBoxWidget widget;

        public ItemTagLine(ResourceLocation tagName) {
            widget = addCheckBox(-100, -100, RenderUtils.shorten(tagName.toString(), 22), true, (btn) -> {

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
}
