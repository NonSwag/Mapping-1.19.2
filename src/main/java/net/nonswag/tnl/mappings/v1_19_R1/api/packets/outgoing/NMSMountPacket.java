package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.MountPacket;

import javax.annotation.Nonnull;

public final class NMSMountPacket extends MountPacket {

    NMSMountPacket(int holderId, int[] mounts) {
        super(holderId, mounts);
    }

    @Nonnull
    @Override
    public ClientboundSetPassengersPacket build() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeVarInt(getHolderId());
        buffer.writeVarIntArray(getMounts());
        return new ClientboundSetPassengersPacket(buffer);
    }
}
