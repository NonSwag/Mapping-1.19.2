package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.nonswag.tnl.listener.api.packets.EntityAnimationPacket;

import javax.annotation.Nonnull;

public final class NMSEntityAnimationPacket extends EntityAnimationPacket {

    NMSEntityAnimationPacket(int entityId, @Nonnull EntityAnimationPacket.Animation animation) {
        super(entityId, animation);
    }

    @Nonnull
    @Override
    public ClientboundAnimatePacket build() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeVarInt(getEntityId());
        buffer.writeByte(getAnimation().getId());
        return new ClientboundAnimatePacket(buffer);
    }
}
