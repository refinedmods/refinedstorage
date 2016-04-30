package refinedstorage.tile.autocrafting;

import refinedstorage.tile.TileController;

public interface ICraftingTask {
    CraftingPattern getPattern();

    boolean update(TileController controller);
}
