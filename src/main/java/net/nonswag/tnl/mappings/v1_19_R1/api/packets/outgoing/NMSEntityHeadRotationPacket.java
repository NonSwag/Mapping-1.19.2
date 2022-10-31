package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.EntityHeadRotationPacket;

import javax.annotation.Nonnull;

public final class NMSEntityHeadRotationPacket extends EntityHeadRotationPacket {

    NMSEntityHeadRotationPacket(int entityId, float yaw) {
        super(entityId, yaw);
    }

    @Nonnull
    @Override
    public ClientboundRotateHeadPacket build() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeVarInt(getEntityId());
        buffer.writeByte((int) (getYaw() * 256 / 360));
        return new ClientboundRotateHeadPacket(buffer);
    }
}
