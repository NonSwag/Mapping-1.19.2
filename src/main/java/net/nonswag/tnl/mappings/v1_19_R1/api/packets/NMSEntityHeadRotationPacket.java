package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.nonswag.tnl.listener.api.packets.EntityHeadRotationPacket;

import javax.annotation.Nonnull;

public final class NMSEntityHeadRotationPacket extends EntityHeadRotationPacket {

    NMSEntityHeadRotationPacket(int entityId, float yaw) {
        super(entityId, yaw);
    }

    @Nonnull
    @Override
    public ClientboundRotateHeadPacket build() {
        return new ClientboundRotateHeadPacket(new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(getEntityId()).writeVarInt((int) (getYaw() * 256 / 360)));
    }
}
