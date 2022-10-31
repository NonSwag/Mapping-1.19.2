package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.nonswag.tnl.listener.api.packets.outgoing.UpdateTimePacket;

import javax.annotation.Nonnull;

public final class NMSUpdateTimePacket extends UpdateTimePacket {

    NMSUpdateTimePacket(long age, long timestamp, boolean cycle) {
        super(age, timestamp, cycle);
    }

    @Nonnull
    @Override
    public ClientboundSetTimePacket build() {
        return new ClientboundSetTimePacket(getAge(), getTimestamp(), isCycle());
    }
}
