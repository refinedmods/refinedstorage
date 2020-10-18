package com.refinedmods.refinedstorage.render;

import net.minecraft.util.text.*;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExperimentalLightingPipelineNagger {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        if (Boolean.FALSE.equals(ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get())) {
            IFormattableTextComponent message = new StringTextComponent("[Refined Storage] ").setStyle(Styles.AQUA.setBold(true));
            message.append(new StringTextComponent("Please set ").setStyle(Styles.WHITE.setBold(false)));
            message.append(new StringTextComponent("experimentalForgeLightPipelineEnabled").setStyle(Styles.GRAY.setItalic(true)));
            message.append(new StringTextComponent(" to ").setStyle(Styles.WHITE.setBold(false)));
            message.append(new StringTextComponent("true").setStyle(Style.EMPTY.setColor(Color.func_240744_a_(TextFormatting.GREEN)).setBold(true).setItalic(true)));
            message.append(new StringTextComponent(" in the Forge client config to ensure correct rendering of Refined Storage blocks.").setStyle(Styles.WHITE.setBold(false)));

            e.getPlayer().sendStatusMessage(message, false);
        }
    }
}
