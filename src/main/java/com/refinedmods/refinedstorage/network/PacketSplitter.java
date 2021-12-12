package com.refinedmods.refinedstorage.network;

import com.google.common.primitives.Bytes;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketSplitter {
    private final static int MAX_PACKET_SIZE = 943718;
    private static final Map<Integer, Map<Integer, byte[]>> PACKAGE_CACHE = new HashMap<>();

    private final ResourceLocation channelId;
    private final SimpleChannel channel;
    private final Map<Integer, ServerPlayer> messageTargets = new HashMap<>();
    private final Map<Integer, Integer> packetMaximums = new HashMap<>();
    private final Set<Class<?>> messagesToSplit = new HashSet<>();
    private final int maxNumberOfMessages;
    private int comId = 0;
    private int id;

    public PacketSplitter(int maxNumberOfMessages, SimpleChannel channel, ResourceLocation CHANNEL_ID) {
        this.maxNumberOfMessages = maxNumberOfMessages;
        this.channel = channel;
        this.channelId = CHANNEL_ID;
    }

    public boolean shouldMessageBeSplit(Class<?> clazz) {
        return messagesToSplit.contains(clazz);
    }

    public void sendToPlayer(ServerPlayer player, Object message) {
        if (id == 0) id++; // in case we wrapped around, 0 is reserved for server
        int id = this.id++;
        messageTargets.put(id, player);
        sendPacket(message, id, PacketDistributor.PLAYER.with(() -> player));
    }

    public void sendToServer(Object message) {
        messageTargets.put(0, null);
        sendPacket(message, 0, PacketDistributor.SERVER.noArg());
    }

    //@Volatile mostly copied from SimpleChannel
    private void sendPacket(Object Message, int id, PacketDistributor.PacketTarget target) {
        final FriendlyByteBuf bufIn = new FriendlyByteBuf(Unpooled.buffer());

        //write the message id to be able to figure out where the packet is supposed to go in the wrapper
        bufIn.writeInt(id);

        int index = channel.encodeMessage(Message, bufIn);
        target.send(target.getDirection().buildPacket(Pair.of(bufIn, index), channelId).getThis());
    }

    public <MSG> void registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        registerMessage(index, maxNumberOfMessages, messageType, encoder, decoder, messageConsumer);
    }

    public <MSG> void registerMessage(int index, int maxNumberOfMessages, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        packetMaximums.put(index, maxNumberOfMessages);
        messagesToSplit.add(messageType);

        BiConsumer<MSG, FriendlyByteBuf> wrappedEncoder = (msg, buffer) -> {
            int id = buffer.readInt();
            buffer.discardReadBytes();
            ServerPlayer player = messageTargets.get(id);
            messageTargets.remove(id);

            //write a zero for the number of packets in case the packet does not need to be split
            buffer.writeShort(0);
            encoder.accept(msg, buffer);
            createSplittingConsumer(player).accept(msg, buffer);
        };

        channel.registerMessage(index, messageType, wrappedEncoder, createPacketCombiner().andThen(decoder), messageConsumer);
    }

    private <MSG> BiConsumer<MSG, FriendlyByteBuf> createSplittingConsumer(ServerPlayer playerEntity) {
        return (MSG, buf) -> {

            if (buf.writerIndex() < MAX_PACKET_SIZE) {
                return;
            }

            //read packetId for this packet
            int packetId = buf.readUnsignedByte();

            //this short is written here in case we are not splitting, ignore for split packages
            buf.readShort();

            //ignore the above as it is not required for the final packet
            int currentIndex = buf.readerIndex();
            int packetIndex = 0;
            final int comId = this.comId++;

            //Data for this packet
            byte[] packetData = new byte[0];

            int maximumPackets = packetMaximums.get(packetId);
            int expectedPackets = buf.writerIndex() / MAX_PACKET_SIZE + 1;
            boolean failure = false;

            //Loop while data is available.
            while (currentIndex < buf.writerIndex()) {

                int sliceSize = Math.min(MAX_PACKET_SIZE, buf.writerIndex() - currentIndex);

                //Extract the sub data array.
                byte[] subPacketData = Arrays.copyOfRange(buf.array(), currentIndex, currentIndex + sliceSize);

                if (packetIndex == 0) { // Assign Data for first Packet to this packet.
                    packetData = subPacketData;
                    packetIndex++;
                } else {
                    //Construct the split packet.
                    SplitPacketMessage splitPacketMessage = new SplitPacketMessage(comId, packetIndex++, subPacketData);

                    if (playerEntity == null) {
                        channel.send(PacketDistributor.SERVER.noArg(), splitPacketMessage);
                    } else {
                        channel.send(PacketDistributor.PLAYER.with(() -> playerEntity), splitPacketMessage);
                    }
                }

                //Move our working index.
                currentIndex += sliceSize;

                if (packetIndex > maximumPackets) {
                    LogManager.getLogger().error("Failure Splitting Packets on Channel \"" + channelId + "\"." + " with " + MSG.getClass() + ". " +
                        " Number of Packets sent " + (packetIndex - 1) + ", expected number of Packets " + expectedPackets + ", maximum number of packets for a message of this type " + packetMaximums.get(packetId));
                    failure = true;
                    break;
                }
            }

            //start writing at the beginning
            buf.setIndex(0, 0);

            //packetId is required for forge to match the packet
            buf.writeByte(packetId);

            //number of packets the packet was split into
            buf.writeShort(failure ? expectedPackets : packetIndex);
            buf.writeInt(comId);
            buf.writeByteArray(packetData);

            //copies the written data into a new buffer discarding the old one
            buf.capacity(buf.writerIndex());
        };
    }

    private Function<FriendlyByteBuf, FriendlyByteBuf> createPacketCombiner() {
        return (buf) -> {
            int size = buf.readShort();

            //This packet was not split
            if (size < 2) return buf;

            int comId = buf.readInt();

            Map<Integer, byte[]> partsMap = PACKAGE_CACHE.get(comId);
            if (partsMap == null || partsMap.size() != size - 1) {
                int partSize = partsMap == null ? 0 : partsMap.size();
                int id = buf.readUnsignedByte();
                int max = packetMaximums.get(id) == null ? 0 : packetMaximums.get(id);
                throw new PacketSplittingException(channelId, partSize, size, max, id);
            }

            //Add data that came from this packet
            addPackagePart(comId, 0, buf.readByteArray());

            //Combine Cached Data
            final byte[] packetData = partsMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .reduce(new byte[0], Bytes::concat);

            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(packetData));

            //remove data from cache
            PACKAGE_CACHE.remove(comId);
            return buffer;
        };
    }

    public void addPackagePart(int communicationId, int packetIndex, byte[] payload) {
        //Sync on the message cache since this is still on the Netty thread.
        synchronized (PacketSplitter.PACKAGE_CACHE) {
            PacketSplitter.PACKAGE_CACHE.computeIfAbsent(communicationId, (id) -> new ConcurrentHashMap<>());
            PacketSplitter.PACKAGE_CACHE.get(communicationId).put(packetIndex, payload);
        }
    }
}

class PacketSplittingException extends RuntimeException {
    ResourceLocation channnelId;
    int actualSize;
    int expectedSize;
    int maximumSize;
    int packetId;

    public PacketSplittingException(ResourceLocation channnelId, int actualSize, int expectedSize, int maximumSize, int packetId) {
        this.channnelId = channnelId;
        this.actualSize = actualSize;
        this.expectedSize = expectedSize;
        this.maximumSize = maximumSize;
        this.packetId = packetId;
    }

    @Override
    public String getMessage() {
        return "Failure Splitting Packets on Channel \"" + channnelId.toString() + "\"." +
            " Number of Packets sent " + actualSize + ", Number of Packets expected " + expectedSize + ", maximum number of packets for a message of this type " + maximumSize;
    }

}
