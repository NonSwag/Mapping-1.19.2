package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.nonswag.tnl.listener.api.packets.EntityBodyRotationPacket;

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
