package net.nonswag.tnl.mappings.v1_19_R1.api.player.channel;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.nonswag.tnl.core.api.logger.Logger;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.events.PlayerPacketEvent;

import javax.annotation.Nonnull;

public abstract class PlayerChannelHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(@Nonnull ChannelHandlerContext context, @Nonnull Object packet) {
        try {
            if (!handleInjections(packet)) return;
            PlayerPacketEvent event = new PlayerPacketEvent(getPlayer(), packet, PlayerPacketEvent.ChannelDirection.IN);
            if (event.call()) super.channelRead(context, event.getPacket());
        } catch (Exception e) {
            Logger.error.println(e);
        }
    }

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise channel) {
        try {
            if (!handleInjections(packet)) return;
            PlayerPacketEvent event = new PlayerPacketEvent(getPlayer(), packet, PlayerPacketEvent.ChannelDirection.OUT);
            if (event.call()) super.write(context, event.getPacket(), channel);
        } catch (Exception e) {
            Logger.error.println(e);
        }
    }

    public abstract boolean handleInjections(@Nonnull Object packet);

    @Nonnull
    public abstract TNLPlayer getPlayer();
}
