package refinedstorage.api.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;

/**
 * Represents a crafting monitor element.
 */
public interface ICraftingMonitorElement<T> {
    /**
     * @param gui the gui
     * @param x   position on the x axis to render
     * @param y   position on the y axis to render
     */
    void draw(T gui, int x, int y);

    /**
     * Returns the position where the corresponding task is in the crafting task list.
     * Used for cancelling tasks.
     *
     * @return the id, or -1 if no task is associated with this element
     */
    int getTaskId();

    /**
     * Returns the id of this element, used for serialization and deserialization over the network.
     *
     * @return the id
     */
    String getId();

    /**
     * Writes the data to the network.
     *
     * @param buf the buffer
     */
    void write(ByteBuf buf);
}
