package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.nonswag.tnl.listener.api.packets.outgoing.EntityBodyRotationPacket;

import javax.annotation.Nonnull;

public final class NMSEntityBodyRotationPacket extends EntityBodyRotationPacket {

    NMSEntityBodyRotationPacket(int entityId, float rotation) {
        super(entityId, rotation);
    }

    @Nonnull
    @Override
    public Void build() {
        throw new UnsupportedOperationException();
    }
}
