package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.nonswag.tnl.listener.api.location.Position;
import net.nonswag.tnl.listener.api.packets.EntityTeleportPacket;

import javax.annotation.Nonnull;

public final class NMSEntityTeleportPacket extends EntityTeleportPacket {

    NMSEntityTeleportPacket(int entityId, @Nonnull Position position) {
        super(entityId, position);
    }

    @Nonnull
    @Override
    public ClientboundTeleportEntityPacket build() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeVarInt(getEntityId());
        buffer.writeDouble(getPosition().getX());
        buffer.writeDouble(getPosition().getY());
        buffer.writeDouble(getPosition().getZ());
        buffer.writeByte((byte) ((int) (getPosition().getYaw() * 256.0F / 360.0F)));
        buffer.writeByte((byte) ((int) (getPosition().getPitch() * 256.0F / 360.0F)));
        buffer.writeBoolean(false);
        return new ClientboundTeleportEntityPacket(buffer);
    }
}
