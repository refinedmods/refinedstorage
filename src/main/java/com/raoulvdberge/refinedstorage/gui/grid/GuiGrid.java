package com.raoulvdberge.refinedstorage.gui.grid;

import com.google.common.collect.Lists;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.IResizableDisplay;
import com.raoulvdberge.refinedstorage.gui.control.*;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.*;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.gui.grid.view.GridViewFluid;
import com.raoulvdberge.refinedstorage.gui.grid.view.GridViewItem;
import com.raoulvdberge.refinedstorage.gui.grid.view.IGridView;
import com.raoulvdberge.refinedstorage.integration.jei.IntegrationJEI;
import com.raoulvdberge.refinedstorage.integration.jei.RSJEIPlugin;
import com.raoulvdberge.refinedstorage.network.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import com.raoulvdberge.refinedstorage.util.TimeUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GuiGrid extends GuiBase implements IResizableDisplay {
    private IGridView view;

    private TextFieldSearch searchField;
    private GuiCheckBox oredictPattern;
    private GuiCheckBox processingPattern;
    private GuiCheckBox blockingPattern;
    private GuiButton tabPageLeft;
    private GuiButton tabPageRight;

    private IGrid grid;

    private boolean wasConnected;
    private boolean hadTabs = false;

    private int tabHovering = -1;

    private int slotNumber;

    public GuiGrid(ContainerGrid container, IGrid grid) {
        super(container, grid.getType() == GridType.FLUID ? 193 : 227, 0);

        List<IGridSorter> defaultSorters = new LinkedList<>();
        defaultSorters.add(new GridSorterName());
        defaultSorters.add(new GridSorterQuantity());
        defaultSorters.add(new GridSorterID());
        defaultSorters.add(new GridSorterInventoryTweaks());
        defaultSorters.add(new GridSorterLastModified());

        this.grid = grid;
        this.view = grid.getType() == GridType.FLUID ? new GridViewFluid(this, defaultSorters) : new GridViewItem(this, defaultSorters);
        this.wasConnected = this.grid.isActive();
    }

    @Override
    protected void calcHeight() {
        this.ySize = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);

        if (hadTabs) {
            this.ySize += ContainerGrid.TAB_HEIGHT;
        }

        this.screenHeight = ySize;
    }

    @Override
    public void init(int x, int y) {
        ((ContainerGrid) this.inventorySlots).initSlots();

        this.scrollbar = new Scrollbar(174, getTabHeight() + getTopHeight(), 12, (getVisibleRows() * 18) - 2);

        if (grid instanceof NetworkNodeGrid || grid instanceof TilePortableGrid) {
            addSideButton(new SideButtonRedstoneMode(this, grid instanceof NetworkNodeGrid ? TileGrid.REDSTONE_MODE : TilePortableGrid.REDSTONE_MODE));
        }

        tabPageLeft = addButton(getGuiLeft(), getGuiTop() - 22, 20, 20, "<", true, grid.getTotalTabPages() > 0);
        tabPageRight = addButton(getGuiLeft() + getXSize() - 22 - 32, getGuiTop() - 22, 20, 20, ">", true, grid.getTotalTabPages() > 0);

        int sx = x + 80 + 1;
        int sy = y + 6 + 1 + getTabHeight();

        if (searchField == null) {
            searchField = new TextFieldSearch(0, fontRenderer, sx, sy, 88 - 6);
            searchField.addListener(() -> {
                view.sort();

                updateJEI();
            });

            updateSearchFieldFocus(grid.getSearchBoxMode());
        } else {
            searchField.x = sx;
            searchField.y = sy;
        }

        if (grid.getType() == GridType.PATTERN) {
            processingPattern = addCheckBox(x + 7, y + getTabHeight() + getTopHeight() + (getVisibleRows() * 18) + 60, t("misc.refinedstorage:processing"), TileGrid.PROCESSING_PATTERN.getValue());
            oredictPattern = addCheckBox(processingPattern.x + processingPattern.width + 5, y + getTabHeight() + getTopHeight() + (getVisibleRows() * 18) + 60, t("misc.refinedstorage:oredict"), TileGrid.OREDICT_PATTERN.getValue());

            if (((NetworkNodeGrid) grid).isProcessingPattern()) {
                blockingPattern = addCheckBox(oredictPattern.x + oredictPattern.width + 5, y + getTabHeight() + getTopHeight() + (getVisibleRows() * 18) + 60, t("misc.refinedstorage:blocking"), TileGrid.BLOCKING_PATTERN.getValue());
            }
        }

        if (grid.getType() != GridType.FLUID && grid.getViewType() != -1) {
            addSideButton(new SideButtonGridViewType(this, grid));
        }

        addSideButton(new SideButtonGridSortingDirection(this, grid));
        addSideButton(new SideButtonGridSortingType(this, grid));
        addSideButton(new SideButtonGridSearchBoxMode(this));
        addSideButton(new SideButtonGridSize(this, () -> grid.getSize(), size -> grid.onSizeChanged(size)));

        view.sort();
    }

    @Override
    protected int getSideButtonYStart() {
        return super.getSideButtonYStart() + (!grid.getTabs().isEmpty() ? ContainerGrid.TAB_HEIGHT - 3 : 0);
    }

    public IGrid getGrid() {
        return grid;
    }

    public IGridView getView() {
        return view;
    }

    @Override
    public void update(int x, int y) {
        if (wasConnected != grid.isActive()) {
            wasConnected = grid.isActive();

            view.sort();
        }

        boolean hasTabs = !getGrid().getTabs().isEmpty();

        if (hadTabs != hasTabs) {
            hadTabs = hasTabs;

            initGui();
        }
    }

    @Override
    public int getTopHeight() {
        return 19;
    }

    @Override
    public int getBottomHeight() {
        if (grid.getType() == GridType.CRAFTING) {
            return 156;
        } else if (grid.getType() == GridType.PATTERN) {
            return 169;
        } else {
            return 99;
        }
    }

    @Override
    public int getYPlayerInventory() {
        int yp = getTabHeight() + getTopHeight() + (getVisibleRows() * 18);

        if (grid.getType() == GridType.NORMAL || grid.getType() == GridType.FLUID) {
            yp += 16;
        } else if (grid.getType() == GridType.CRAFTING) {
            yp += 73;
        } else if (grid.getType() == GridType.PATTERN) {
            yp += 86;
        }

        return yp;
    }

    @Override
    public int getRows() {
        return Math.max(0, (int) Math.ceil((float) view.getStacks().size() / 9F));
    }

    @Override
    public int getCurrentOffset() {
        return scrollbar.getOffset();
    }

    @Override
    public String getSearchFieldText() {
        return searchField == null ? "" : searchField.getText();
    }

    @Override
    public int getVisibleRows() {
        switch (grid.getSize()) {
            case IGrid.SIZE_STRETCH:
                int screenSpaceAvailable = height - getTopHeight() - getBottomHeight() - (hadTabs ? ContainerGrid.TAB_HEIGHT : 0);

                return Math.max(3, Math.min((screenSpaceAvailable / 18) - 3, RS.INSTANCE.config.maxRowsStretch));
            case IGrid.SIZE_SMALL:
                return 3;
            case IGrid.SIZE_MEDIUM:
                return 5;
            case IGrid.SIZE_LARGE:
                return 8;
            default:
                return 3;
        }
    }

    public boolean isOverSlotWithStack() {
        return grid.isActive() && isOverSlot() && slotNumber < view.getStacks().size();
    }

    private boolean isOverSlot() {
        return slotNumber >= 0;
    }

    public boolean isOverSlotArea(int mouseX, int mouseY) {
        return inBounds(7, 19 + getTabHeight(), 162, 18 * getVisibleRows(), mouseX, mouseY);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    private boolean isOverClear(int mouseX, int mouseY) {
        int y = getTabHeight() + getTopHeight() + (getVisibleRows() * 18) + 4;

        switch (grid.getType()) {
            case CRAFTING:
                return inBounds(82, y, 7, 7, mouseX, mouseY);
            case PATTERN:
                if (((NetworkNodeGrid) grid).isProcessingPattern()) {
                    return inBounds(154, y, 7, 7, mouseX, mouseY);
                }

                return inBounds(82, y, 7, 7, mouseX, mouseY);
            default:
                return false;
        }
    }

    private boolean isOverCreatePattern(int mouseX, int mouseY) {
        return grid.getType() == GridType.PATTERN && inBounds(172, getTabHeight() + getTopHeight() + (getVisibleRows() * 18) + 22, 16, 16, mouseX, mouseY) && ((NetworkNodeGrid) grid).canCreatePattern();
    }

    private int getTabHeight() {
        return !grid.getTabs().isEmpty() ? ContainerGrid.TAB_HEIGHT - 4 : 0;
    }

    private void drawTab(IGridTab tab, boolean foregroundLayer, int x, int y, int mouseX, int mouseY, int index, int num) {
        boolean selected = index == grid.getTabSelected();

        if ((foregroundLayer && !selected) || (!foregroundLayer && selected)) {
            return;
        }

        int tx = x + ((ContainerGrid.TAB_WIDTH + 1) * num);
        int ty = y;

        bindTexture("icons.png");

        if (!selected) {
            ty += 3;
        }

        int uvx;
        int uvy = 225;
        int tbw = ContainerGrid.TAB_WIDTH;
        int otx = tx;

        if (selected) {
            uvx = 227;

            if (num > 0) {
                uvx = 226;
                uvy = 194;
                tbw++;
                tx--;
            }
        } else {
            uvx = 199;
        }

        drawTexture(tx, ty, uvx, uvy, tbw, ContainerGrid.TAB_HEIGHT);

        RenderHelper.enableGUIStandardItemLighting();

        drawItem(otx + 6, ty + 9 - (!selected ? 3 : 0), tab.getIcon());

        if (inBounds(tx, ty, ContainerGrid.TAB_WIDTH, ContainerGrid.TAB_HEIGHT - (selected ? 2 : 7), mouseX, mouseY)) {
            tabHovering = index;
        }
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        tabHovering = -1;

        int j = 0;
        for (int i = grid.getTabPage() * IGrid.TABS_PER_PAGE; i < (grid.getTabPage() * IGrid.TABS_PER_PAGE) + IGrid.TABS_PER_PAGE; ++i) {
            if (i < grid.getTabs().size()) {
                drawTab(grid.getTabs().get(i), false, x, y, mouseX, mouseY, i, j++);
            }
        }

        if (grid.getType() == GridType.CRAFTING) {
            bindTexture("gui/crafting_grid.png");
        } else if (grid.getType() == GridType.PATTERN) {
            bindTexture("gui/pattern_grid" + (((NetworkNodeGrid) grid).isProcessingPattern() ? "_processing" : "") + ".png");
        } else if (grid instanceof IPortableGrid) {
            bindTexture("gui/portable_grid.png");
        } else {
            bindTexture("gui/grid.png");
        }

        int yy = y + getTabHeight();

        drawTexture(x, yy, 0, 0, screenWidth - (grid.getType() != GridType.FLUID ? 34 : 0), getTopHeight());

        if (grid.getType() != GridType.FLUID) {
            drawTexture(x + screenWidth - 34 + 4, y + getTabHeight(), 197, 0, 30, grid instanceof IPortableGrid ? 114 : 82);
        }

        int rows = getVisibleRows();

        for (int i = 0; i < rows; ++i) {
            yy += 18;

            drawTexture(x, yy, 0, getTopHeight() + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), screenWidth - (grid.getType() != GridType.FLUID ? 34 : 0), 18);
        }

        yy += 18;

        drawTexture(x, yy, 0, getTopHeight() + (18 * 3), screenWidth - (grid.getType() != GridType.FLUID ? 34 : 0), getBottomHeight());

        if (grid.getType() == GridType.PATTERN) {
            int ty = 0;

            if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
                ty = 1;
            }

            if (!((NetworkNodeGrid) grid).canCreatePattern()) {
                ty = 2;
            }

            drawTexture(x + 172, y + getTabHeight() + getTopHeight() + (getVisibleRows() * 18) + 22, 240, ty * 16, 16, 16);
        }

        j = 0;
        for (int i = grid.getTabPage() * IGrid.TABS_PER_PAGE; i < (grid.getTabPage() * IGrid.TABS_PER_PAGE) + IGrid.TABS_PER_PAGE; ++i) {
            if (i < grid.getTabs().size()) {
                drawTab(grid.getTabs().get(i), true, x, y, mouseX, mouseY, i, j++);
            }
        }

        if (searchField != null) {
            searchField.drawTextBox();
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7 + getTabHeight(), t(grid.getGuiTitle()));
        drawString(7, getYPlayerInventory() - 12, t("container.inventory"));

        if (grid.getTotalTabPages() > 0) {
            String text = (grid.getTabPage() + 1) + " / " + (grid.getTotalTabPages() + 1);

            drawString((int) ((193F - (float) fontRenderer.getStringWidth(text)) / 2F), -16, text, 0xFFFFFF);
        }

        int x = 8;
        int y = 19 + getTabHeight();

        this.slotNumber = -1;

        int slot = scrollbar != null ? (scrollbar.getOffset() * 9) : 0;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isActive()) {
                this.slotNumber = slot;
            }

            if (slot < view.getStacks().size()) {
                view.getStacks().get(slot).draw(this, x, y);
            }

            if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isActive()) {
                int color = grid.isActive() ? -2130706433 : 0xFF5B5B5B;

                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                zLevel = 190;
                GlStateManager.colorMask(true, true, true, false);
                drawGradientRect(x, y, x + 16, y + 16, color, color);
                zLevel = 0;
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            slot++;

            x += 18;

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            }
        }

        if (isOverSlotWithStack()) {
            drawGridTooltip(view.getStacks().get(slotNumber), mouseX, mouseY);
        }

        if (isOverClear(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:clear"));
        }

        if (isOverCreatePattern(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("gui.refinedstorage:grid.pattern_create"));
        }

        if (tabHovering >= 0 && tabHovering < grid.getTabs().size() && !grid.getTabs().get(tabHovering).getName().equalsIgnoreCase("")) {
            drawTooltip(mouseX, mouseY, grid.getTabs().get(tabHovering).getName());
        }
    }

    // Copied with some tweaks from GuiUtils#drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
    public void drawGridTooltip(IGridStack gridStack, int mouseX, int mouseY) {
        // RS BEGIN
        List<String> textLines = Lists.newArrayList(gridStack.getTooltip().split("\n"));

        if (RS.INSTANCE.config.detailedTooltip) {
            if (!(gridStack instanceof GridStackItem) || !((GridStackItem) gridStack).doesDisplayCraftText()) {
                textLines.add("");
            }

            if (gridStack.getTrackerEntry() != null) {
                textLines.add("");
            }
        }

        ItemStack stack = gridStack instanceof GridStackItem ? ((GridStackItem) gridStack).getStack() : ItemStack.EMPTY;
        // RS END

        if (!textLines.isEmpty()) {
            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, -1, fontRenderer);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            FontRenderer font = event.getFontRenderer();

            // RS BEGIN
            float textScale = font.getUnicodeFlag() ? 1F : 0.7F;
            // RS END

            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            // RS BEGIN
            if (RS.INSTANCE.config.detailedTooltip) {
                int size;

                if (!(gridStack instanceof GridStackItem) || !((GridStackItem) gridStack).doesDisplayCraftText()) {
                    size = (int) (font.getStringWidth(I18n.format("misc.refinedstorage:total", gridStack.getFormattedFullQuantity())) * textScale);

                    if (size > tooltipTextWidth) {
                        tooltipTextWidth = size;
                    }
                }

                if (gridStack.getTrackerEntry() != null) {
                    size = (int) (font.getStringWidth(TimeUtils.getAgo(gridStack.getTrackerEntry().getTime(), gridStack.getTrackerEntry().getName())) * textScale);

                    if (size > tooltipTextWidth) {
                        tooltipTextWidth = size;
                    }
                }
            }
            // RS END

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2;
                }
            }

            if (tooltipY + tooltipHeight + 6 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 6;
            }

            final int zLevel = 300;
            final int backgroundColor = 0xF0100010;
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            final int borderColorStart = 0x505000FF;
            final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            // RS BEGIN
            if (RS.INSTANCE.config.detailedTooltip) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(textScale, textScale, 1);

                if (!(gridStack instanceof GridStackItem) || !((GridStackItem) gridStack).doesDisplayCraftText()) {
                    font.drawStringWithShadow(
                        TextFormatting.GRAY + I18n.format("misc.refinedstorage:total", gridStack.getFormattedFullQuantity()),
                        RenderUtils.getOffsetOnScale(tooltipX, textScale),
                        RenderUtils.getOffsetOnScale(tooltipTop + tooltipHeight - (gridStack.getTrackerEntry() != null ? 15 : 6) - (font.getUnicodeFlag() ? 2 : 0), textScale),
                        -1
                    );
                }

                if (gridStack.getTrackerEntry() != null) {
                    font.drawStringWithShadow(
                        TextFormatting.GRAY + TimeUtils.getAgo(gridStack.getTrackerEntry().getTime(), gridStack.getTrackerEntry().getName()),
                        RenderUtils.getOffsetOnScale(tooltipX, textScale),
                        RenderUtils.getOffsetOnScale(tooltipTop + tooltipHeight - 6 - (font.getUnicodeFlag() ? 2 : 0), textScale),
                        -1
                    );
                }

                GlStateManager.popMatrix();
            }
            // RS END

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == oredictPattern) {
            TileDataManager.setParameter(TileGrid.OREDICT_PATTERN, oredictPattern.isChecked());
        } else if (button == blockingPattern) {
            TileDataManager.setParameter(TileGrid.BLOCKING_PATTERN, blockingPattern.isChecked());
        } else if (button == processingPattern) {
            // Rebuild the inventory slots before the slot change packet arrives
            TileGrid.PROCESSING_PATTERN.setValue(processingPattern.isChecked());
            ((ContainerGrid) this.inventorySlots).initSlots();

            TileDataManager.setParameter(TileGrid.PROCESSING_PATTERN, processingPattern.isChecked());
        } else if (button == tabPageLeft) {
            grid.onTabPageChanged(grid.getTabPage() - 1);
        } else if (button == tabPageRight) {
            grid.onTabPageChanged(grid.getTabPage() + 1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        if (tabHovering >= 0 && tabHovering < grid.getTabs().size()) {
            grid.onTabSelectionChanged(tabHovering);
        }

        if (searchField != null) {
            searchField.mouseClicked(mouseX, mouseY, clickedButton);
        }

        boolean clickedClear = clickedButton == 0 && isOverClear(mouseX - guiLeft, mouseY - guiTop);
        boolean clickedCreatePattern = clickedButton == 0 && isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop);

        if (clickedCreatePattern) {
            BlockPos gridPos = ((NetworkNodeGrid) grid).getPos();

            RS.INSTANCE.network.sendToServer(new MessageGridPatternCreate(gridPos.getX(), gridPos.getY(), gridPos.getZ()));
        } else if (grid.isActive()) {
            if (clickedClear) {
                RS.INSTANCE.network.sendToServer(new MessageGridClear());
            }

            ItemStack held = ((ContainerGrid) this.inventorySlots).getPlayer().inventory.getItemStack();

            if (isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && !held.isEmpty() && (clickedButton == 0 || clickedButton == 1)) {
                RS.INSTANCE.network.sendToServer(grid.getType() == GridType.FLUID ? new MessageGridFluidInsertHeld() : new MessageGridItemInsertHeld(clickedButton == 1));
            }

            if (isOverSlotWithStack()) {
                if (grid.getType() != GridType.FLUID && (held.isEmpty() || (!held.isEmpty() && clickedButton == 2))) {
                    GridStackItem stack = (GridStackItem) view.getStacks().get(slotNumber);

                    if (stack.isCraftable() && (stack.doesDisplayCraftText() || (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown())) && view.canCraft()) {
                        FMLCommonHandler.instance().showGuiScreen(new GuiCraftingStart(this, ((ContainerGrid) this.inventorySlots).getPlayer(), stack));
                    } else {
                        int flags = 0;

                        if (clickedButton == 1) {
                            flags |= IItemGridHandler.EXTRACT_HALF;
                        }

                        if (GuiScreen.isShiftKeyDown()) {
                            flags |= IItemGridHandler.EXTRACT_SHIFT;
                        }

                        if (clickedButton == 2) {
                            flags |= IItemGridHandler.EXTRACT_SINGLE;
                        }

                        RS.INSTANCE.network.sendToServer(new MessageGridItemPull(stack.getHash(), flags));
                    }
                } else if (grid.getType() == GridType.FLUID && held.isEmpty()) {
                    RS.INSTANCE.network.sendToServer(new MessageGridFluidPull(view.getStacks().get(slotNumber).getHash(), GuiScreen.isShiftKeyDown()));
                }
            }
        }

        if (clickedClear || clickedCreatePattern) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (searchField == null) {
            return;
        }

        if (checkHotbarKeys(keyCode)) {
            // NO OP
        } else if (searchField.textboxKeyTyped(character, keyCode)) {
            updateJEI();

            view.sort();

            keyHandled = true;
        } else if (keyCode == RSKeyBindings.CLEAR_GRID_CRAFTING_MATRIX.getKeyCode()) {
            RS.INSTANCE.network.sendToServer(new MessageGridClear());
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    private void updateJEI() {
        if (IntegrationJEI.isLoaded() && (grid.getSearchBoxMode() == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED || grid.getSearchBoxMode() == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)) {
            RSJEIPlugin.INSTANCE.getRuntime().getIngredientFilter().setFilterText(searchField.getText());
        }
    }

    public void updateSearchFieldFocus(int mode) {
        if (searchField != null) {
            searchField.setCanLoseFocus(!IGrid.isSearchBoxModeWithAutoselection(mode));
            searchField.setFocused(IGrid.isSearchBoxModeWithAutoselection(mode));
        }
    }

    public GuiTextField getSearchField() {
        return searchField;
    }

    public void updateOredictPattern(boolean checked) {
        if (oredictPattern != null) {
            oredictPattern.setIsChecked(checked);
        }
    }

    public void updateBlockingPattern(boolean checked) {
        if (blockingPattern != null) {
            blockingPattern.setIsChecked(checked);
        }
    }

    public GuiButton getTabPageLeft() {
        return tabPageLeft;
    }

    public GuiButton getTabPageRight() {
        return tabPageRight;
    }
}
