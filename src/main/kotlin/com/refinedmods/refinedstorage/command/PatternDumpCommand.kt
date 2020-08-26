package com.refinedmods.refinedstorage.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.refinedmods.refinedstorage.item.PatternItem
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fluids.FluidInstance


class PatternDumpCommand : Command<CommandSource> {
    @Throws(CommandSyntaxException::class)
    override fun run(context: CommandContext<CommandSource>): Int {
        val stack: ItemStack = context.getSource().asPlayer().inventory.getCurrentItem()
        if (stack.item is PatternItem) {
            val processing = PatternItem.isProcessing(stack)
            val exact = PatternItem.isExact(stack)
            val allowedTagList = PatternItem.getAllowedTags(stack)
            val pattern = PatternItem.fromCache(context.getSource().getWorld(), stack)
            context.getSource().sendFeedback(StringTextComponent("Crafting task factory ID: ").setStyle(Styles.YELLOW).append(StringTextComponent(pattern.getCraftingTaskFactoryId().toString()).setStyle(Styles.WHITE)), false)
            if (!pattern.isValid()) {
                context.getSource().sendFeedback(StringTextComponent("Pattern is invalid! Reason: ").append(pattern.getErrorMessage()).setStyle(Styles.RED), false)
            } else {
                context.getSource().sendFeedback(StringTextComponent("Processing: ").setStyle(Styles.YELLOW).append(StringTextComponent(processing.toString()).setStyle(Styles.WHITE)), false)
                context.getSource().sendFeedback(StringTextComponent("Exact: ").setStyle(Styles.YELLOW).append(StringTextComponent(exact.toString()).setStyle(Styles.WHITE)), false)
                context.getSource().sendFeedback(StringTextComponent("Has allowed tag list: ").setStyle(Styles.YELLOW).append(StringTextComponent((allowedTagList != null).toString()).setStyle(Styles.WHITE)), false)
                if (pattern.isProcessing()) {
                    for (i in pattern.getInputs().indices) {
                        if (!pattern.getInputs()[i].isEmpty()) {
                            context.getSource().sendFeedback(StringTextComponent("Item inputs in slot $i:").setStyle(Styles.YELLOW), false)
                            for (j in 0 until pattern.getInputs()[i].size()) {
                                context.getSource().sendFeedback(StringTextComponent("- Possibility #" + j + ": " + pattern.getInputs()[i].get(j).getCount() + "x ").append(pattern.getInputs()[i].get(j).getDisplayName()), false)
                            }
                        }
                        if (allowedTagList != null) {
                            for (allowed in allowedTagList.getAllowedItemTags()[i]) {
                                context.getSource().sendFeedback(StringTextComponent("- Allowed item tag: " + allowed.toString()), false)
                            }
                        }
                    }
                    for (i in pattern.getFluidInputs().indices) {
                        if (!pattern.getFluidInputs()[i].isEmpty()) {
                            context.getSource().sendFeedback(StringTextComponent("Fluid inputs in slot $i:").setStyle(Styles.YELLOW), false)
                            for (j in 0 until pattern.getFluidInputs()[i].size()) {
                                context.getSource().sendFeedback(StringTextComponent("- Possibility #" + j + ": " + pattern.getFluidInputs()[i].get(j).getAmount() + " mB ").append(pattern.getFluidInputs()[i].get(j).getDisplayName()), false)
                            }
                        }
                        if (allowedTagList != null) {
                            for (allowed in allowedTagList.getAllowedFluidTags()[i]) {
                                context.getSource().sendFeedback(StringTextComponent("- Allowed fluid tag: " + allowed.toString()), false)
                            }
                        }
                    }
                    context.getSource().sendFeedback(StringTextComponent("Outputs").setStyle(Styles.YELLOW), false)
                    for (output in pattern.getOutputs()) {
                        context.getSource().sendFeedback(StringTextComponent("- " + output.count + "x ").append(output.getDisplayName()), false)
                    }
                    context.getSource().sendFeedback(StringTextComponent("Fluid outputs").setStyle(Styles.YELLOW), false)
                    for (output in pattern.getFluidOutputs()) {
                        context.getSource().sendFeedback(StringTextComponent("- " + output.getAmount().toString() + " mB ").append(output.getDisplayName()), false)
                    }
                } else {
                    for (i in pattern.getInputs().indices) {
                        if (!pattern.getInputs()[i].isEmpty()) {
                            context.getSource().sendFeedback(StringTextComponent("Inputs in slot $i:").setStyle(Styles.YELLOW), false)
                            for (j in 0 until pattern.getInputs()[i].size()) {
                                context.getSource().sendFeedback(StringTextComponent("- Possibility #" + j + ": " + pattern.getInputs()[i].get(j).getCount() + "x ").append(pattern.getInputs()[i].get(j).getDisplayName()), false)
                            }
                        }
                    }
                    context.getSource().sendFeedback(StringTextComponent("Outputs").setStyle(Styles.YELLOW), false)
                    for (output in pattern.getOutputs()) {
                        context.getSource().sendFeedback(StringTextComponent("- " + output.count + "x ").append(output.getDisplayName()), false)
                    }
                    var anyByproducts = false
                    for (byproduct in pattern.getByproducts()) {
                        if (!byproduct.isEmpty) {
                            if (!anyByproducts) {
                                context.getSource().sendFeedback(StringTextComponent("Byproducts").setStyle(Styles.YELLOW), false)
                                anyByproducts = true
                            }
                            context.getSource().sendFeedback(StringTextComponent("- " + byproduct.count + "x ").append(byproduct.getDisplayName()), false)
                        }
                    }
                }
            }
        } else {
            context.getSource().sendFeedback(StringTextComponent("You need to be holding a pattern in your hand.").setStyle(Styles.RED), false)
        }
        return 0
    }

    companion object {
        @JvmStatic
        fun register(dispatcher: CommandDispatcher<CommandSource?>?): ArgumentBuilder<CommandSource, *> {
            return Commands.literal("patterndump")
                    .requires({ cs -> cs.hasPermissionLevel(0) })
                    .executes(PatternDumpCommand())
        }
    }
}