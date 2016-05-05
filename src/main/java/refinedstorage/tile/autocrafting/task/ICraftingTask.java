package refinedstorage.tile.autocrafting.task;

import refinedstorage.tile.TileController;
import refinedstorage.tile.autocrafting.CraftingPattern;

public interface ICraftingTask {
    CraftingPattern getPattern();

    boolean update(TileController controller);

    void onDone(TileController controller);

    void onCancelled(TileController controller);

    String getInfo();
}
