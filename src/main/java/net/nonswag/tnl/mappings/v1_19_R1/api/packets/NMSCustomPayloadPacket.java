package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.nonswag.tnl.listener.api.packets.CustomPayloadPacket;

import javax.annotation.Nonnull;

public final class NMSCustomPayloadPacket extends CustomPayloadPacket {

    NMSCustomPayloadPacket(@Nonnull String channel, @Nonnull byte[]... bytes) {
        super(channel, bytes);
    }

    @Nonnull
    @Override
    public ClientboundCustomPayloadPacket build() {
        return new ClientboundCustomPayloadPacket(new ResourceLocation(getChannel()), new FriendlyByteBuf(Unpooled.wrappedBuffer(getBytes())));
    }
}
