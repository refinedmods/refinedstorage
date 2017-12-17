package com.raoulvdberge.refinedstorage.apiimpl.solderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;

public class SoldererRecipeLoader {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void load() {
        JsonContext context = new JsonContext(RS.ID);

        CraftingHelper.findFiles(Loader.instance().activeModContainer(), "assets/" + RS.ID + "/solderer_recipes", root -> {
            // @todo: Load the constants into to the context.

            return true;
        }, (root, file) -> {
            String relative = root.relativize(file).toString();

            if (!"json".equals(FilenameUtils.getExtension(file.toString()))) {
                return true;
            }

            String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
            ResourceLocation key = new ResourceLocation(RS.ID, name);

            BufferedReader reader = null;

            try {
                reader = Files.newBufferedReader(file);

                API.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFactory(key, JsonUtils.fromJson(GSON, reader, JsonObject.class)).create(context));
            } catch (JsonParseException e) {
                FMLLog.log.error("Parsing error while reading JSON solderer recipe {}", key, e);

                return false;
            } catch (IOException e) {
                FMLLog.log.error("Couldn't read JSON solderer recipe {} from {}", key, file, e);

                return false;
            } finally {
                IOUtils.closeQuietly(reader);
            }

            return true;
        }, false, false);
    }
}
