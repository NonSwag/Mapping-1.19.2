package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.EntityStatusPacket;

import javax.annotation.Nonnull;

public final class NMSEntityStatusPacket extends EntityStatusPacket {

    NMSEntityStatusPacket(int entityId, @Nonnull Status status) {
        super(entityId, status);
    }

    @Nonnull
    @Override
    public ClientboundEntityEventPacket build() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(getEntityId());
        buffer.writeByte(getStatus().getId());
        return new ClientboundEntityEventPacket(buffer);
    }
}
