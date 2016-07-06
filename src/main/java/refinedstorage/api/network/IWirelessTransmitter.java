package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;

/**
 * Represents a node that can send a wireless signal.
 */
public interface IWirelessTransmitter {
    /**
     * @return The range in blocks of this transmitter, starting from {@link IWirelessTransmitter#getOrigin()}
     */
    int getRange();

    /**
     * @return The position where the wireless signal starts
     */
    BlockPos getOrigin();
}
