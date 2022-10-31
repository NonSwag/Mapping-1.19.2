package net.nonswag.tnl.mappings.v1_19_R1.api.player.channel;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.nonswag.tnl.listener.api.mapper.Mapping;
import net.nonswag.tnl.listener.api.packets.listener.PacketEvent;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.api.plugin.CombinedPlugin;

import javax.annotation.Nonnull;

public abstract class PlayerChannelHandler extends ChannelDuplexHandler {

    @Override
    @SuppressWarnings("rawtypes")
    public void channelRead(@Nonnull ChannelHandlerContext context, @Nonnull Object packet) {
        try {
            if (!handleInjections(packet)) return;
            var incoming = Mapping.get().packetManager().incoming().map(packet);
            var event = new PacketEvent<>(getPlayer(), incoming);
            Mapping.get().pluginHelper().getPlugins().forEach(plugin -> {
                if (!(plugin instanceof CombinedPlugin combinedPlugin) || !plugin.isEnabled()) return;
                combinedPlugin.getEventManager().getReaders().forEach((reader, clazz) -> tryCatch(() -> {
                    if (clazz.isInstance(incoming)) reader.read((PacketEvent) event);
                }));
            });
            if (!event.isCancelled()) super.channelRead(context, event.getPacket().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise channel) {
        try {
            if (!handleInjections(packet)) return;
            var outgoing = Mapping.get().packetManager().outgoing().map(packet);
            var event = new PacketEvent<>(getPlayer(), outgoing);
            Mapping.get().pluginHelper().getPlugins().forEach(plugin -> {
                if (!(plugin instanceof CombinedPlugin combinedPlugin) || !plugin.isEnabled()) return;
                combinedPlugin.getEventManager().getWriters().forEach((writer, clazz) -> tryCatch(() -> {
                    if (clazz.isInstance(outgoing)) writer.write((PacketEvent) event);
                }));
            });
            if (!event.isCancelled()) super.write(context, event.getPacket().build(), channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryCatch(@Nonnull Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract boolean handleInjections(@Nonnull Object packet);

    @Nonnull
    public abstract TNLPlayer getPlayer();
}
