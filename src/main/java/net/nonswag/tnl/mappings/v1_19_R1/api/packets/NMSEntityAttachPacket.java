package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.nonswag.tnl.listener.api.packets.EntityAttachPacket;

import javax.annotation.Nonnull;

public final class NMSEntityAttachPacket extends EntityAttachPacket {

    NMSEntityAttachPacket(int holderId, int leashedId) {
        super(holderId, leashedId);
    }

    @Nonnull
    @Override
    public ClientboundSetEntityLinkPacket build() {
        return new ClientboundSetEntityLinkPacket(new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(getLeashedId()).writeVarInt(getHolderId()));
    }
}
