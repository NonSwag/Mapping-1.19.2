package net.nonswag.tnl.mappings.v1_19_R1.api.player.channel;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.events.PlayerPacketEvent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class PlayerChannelHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(@Nonnull ChannelHandlerContext context, @Nonnull Object packet) {
        if (!handleInjections(packet)) return;
        Consumer<Object> read = p -> {
            try {
                super.channelRead(context, p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        PlayerPacketEvent event = new PlayerPacketEvent(getPlayer(), packet, PlayerPacketEvent.ChannelDirection.IN) {
            @Override
            public void write() {
                read.accept(getPacket());
            }
        };
        if (event.call()) read.accept(event.getPacket());
    }

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise channel) {
        if (!handleInjections(packet)) return;
        Consumer<Object> write = p -> {
            try {
                super.write(context, p, channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        PlayerPacketEvent event = new PlayerPacketEvent(getPlayer(), packet, PlayerPacketEvent.ChannelDirection.OUT) {
            @Override
            public void write() {
                write.accept(getPacket());
            }
        };
        if (event.call()) write.accept(event.getPacket());
    }

    public abstract boolean handleInjections(@Nonnull Object packet);

    @Nonnull
    public abstract TNLPlayer getPlayer();
}
