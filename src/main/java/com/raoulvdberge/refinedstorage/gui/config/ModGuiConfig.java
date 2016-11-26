package com.raoulvdberge.refinedstorage.gui.config;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModGuiConfig extends GuiConfig {
    public ModGuiConfig(GuiScreen guiScreen) {
        super(
            guiScreen,
            RS.INSTANCE.config.getConfigElements(),
            RS.ID,
            false,
            false,
            GuiConfig.getAbridgedConfigPath(RS.INSTANCE.config.getConfig().toString())
        );
    }
}
