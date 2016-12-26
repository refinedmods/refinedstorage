package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerProcessingPatternEncoder;
import com.raoulvdberge.refinedstorage.network.MessageGridPatternCreate;
import com.raoulvdberge.refinedstorage.network.MessageProcessingPatternEncoderClear;
import com.raoulvdberge.refinedstorage.tile.TileProcessingPatternEncoder;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;

public class GuiProcessingPatternEncoder extends GuiBase {
    private TileProcessingPatternEncoder encoder;

    private GuiCheckBox oredictPattern;
    private GuiCheckBox blockingPattern;

    public GuiProcessingPatternEncoder(ContainerProcessingPatternEncoder container, TileProcessingPatternEncoder encoder) {
        super(container, 176, 183);

        this.encoder = encoder;
    }

    @Override
    public void init(int x, int y) {
        oredictPattern = addCheckBox(x + 7, y + 76, I18n.format("misc.refinedstorage:oredict"), TileProcessingPatternEncoder.OREDICT_PATTERN.getValue());
        blockingPattern = addCheckBox(x + 60, y + 76, I18n.format("misc.refinedstorage:blocking"), TileProcessingPatternEncoder.BLOCKING_TASK_PATTERN.getValue());
    }

    @Override
    public void update(int x, int y) {
    }

    private boolean isOverCreatePattern(int mouseX, int mouseY) {
        return inBounds(152, 38, 16, 16, mouseX, mouseY) && encoder.canCreatePattern();
    }

    private boolean isOverClear(int mouseX, int mouseY) {
        return inBounds(80, 19, 7, 7, mouseX, mouseY);
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/processing_pattern_encoder.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);

        int ty = 0;

        if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
            ty = 1;
        }

        if (!encoder.canCreatePattern()) {
            ty = 2;
        }

        drawTexture(x + 152, y + 38, 178, ty * 16, 16, 16);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:processing_pattern_encoder"));
        drawString(7, 90, t("container.inventory"));

        if (isOverCreatePattern(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("gui.refinedstorage:processing_pattern_encoder.pattern_create"));
        }

        if (isOverClear(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:clear"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == oredictPattern) {
            TileDataManager.setParameter(TileProcessingPatternEncoder.OREDICT_PATTERN, oredictPattern.isChecked());
        } else if (button == blockingPattern) {
            TileDataManager.setParameter(TileProcessingPatternEncoder.BLOCKING_TASK_PATTERN, blockingPattern.isChecked());
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
            RS.INSTANCE.network.sendToServer(new MessageGridPatternCreate(encoder.getPos().getX(), encoder.getPos().getY(), encoder.getPos().getZ()));

            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        } else if (isOverClear(mouseX - guiLeft, mouseY - guiTop)) {
            RS.INSTANCE.network.sendToServer(new MessageProcessingPatternEncoderClear(encoder));

            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
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
}