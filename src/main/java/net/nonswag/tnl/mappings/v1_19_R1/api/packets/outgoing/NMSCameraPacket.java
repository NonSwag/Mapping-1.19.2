package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.CameraPacket;

import javax.annotation.Nonnull;

public final class NMSCameraPacket extends CameraPacket {

    NMSCameraPacket(int targetId) {
        super(targetId);
    }

    @Nonnull
    @Override
    public ClientboundSetCameraPacket build() {
        return new ClientboundSetCameraPacket(new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(getTargetId()));
    }
}
