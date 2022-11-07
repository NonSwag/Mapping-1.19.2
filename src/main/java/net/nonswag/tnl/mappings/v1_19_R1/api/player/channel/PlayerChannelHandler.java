package net.nonswag.tnl.mappings.v1_19_R1.api.player.channel;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.nonswag.core.api.annotation.MethodsReturnNonnullByDefault;
import net.nonswag.tnl.listener.api.event.EventManager;
import net.nonswag.tnl.listener.api.mapper.Mapping;
import net.nonswag.tnl.listener.api.packets.listener.PacketReader;
import net.nonswag.tnl.listener.api.packets.listener.PacketWriter;
import net.nonswag.tnl.listener.api.player.TNLPlayer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicBoolean;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class PlayerChannelHandler extends ChannelDuplexHandler {

    @Override
    @SuppressWarnings("rawtypes")
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        try {
            if (!handleInjections(packet)) return;
            var readers = EventManager.getAllReaders();
            if (!readers.isEmpty()) {
                AtomicBoolean cancelled = new AtomicBoolean();
                var incoming = Mapping.get().packetManager().incoming().map(packet);
                readers.forEach((reader, clazz) -> tryCatch(() -> {
                    if (clazz.isInstance(incoming)) ((PacketReader) reader).read(getPlayer(), incoming, cancelled);
                }));
                if (!cancelled.get()) super.channelRead(context, incoming.build());
            } else super.channelRead(context, packet);
        } catch (Exception e) {
            super.channelRead(context, packet);
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise channel) throws Exception {
        try {
            if (!handleInjections(packet)) return;
            var writers = EventManager.getAllWriters();
            if (!writers.isEmpty()) {
                AtomicBoolean cancelled = new AtomicBoolean();
                var outgoing = Mapping.get().packetManager().outgoing().map(packet);
                writers.forEach((writer, clazz) -> tryCatch(() -> {
                    if (clazz.isInstance(outgoing)) ((PacketWriter) writer).write(getPlayer(), outgoing, cancelled);
                }));
                if (!cancelled.get()) super.write(context, outgoing.build(), channel);
            } else super.write(context, packet, channel);
        } catch (Exception e) {
            super.write(context, packet, channel);
            e.printStackTrace();
        }
    }

    private void tryCatch(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract boolean handleInjections(Object packet);

    public abstract TNLPlayer getPlayer();
}
