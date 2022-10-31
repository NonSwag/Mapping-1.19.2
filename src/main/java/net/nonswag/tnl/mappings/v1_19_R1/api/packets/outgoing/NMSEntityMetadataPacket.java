package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.nonswag.tnl.listener.api.packets.outgoing.EntityMetadataPacket;

import javax.annotation.Nonnull;

public final class NMSEntityMetadataPacket extends EntityMetadataPacket<SynchedEntityData> {

    NMSEntityMetadataPacket(int entityId, @Nonnull SynchedEntityData metadata, boolean updateAll) {
        super(entityId, metadata, updateAll);
    }

    @Nonnull
    @Override
    public ClientboundSetEntityDataPacket build() {
        return new ClientboundSetEntityDataPacket(getEntityId(), getMetadata(), isUpdateAll());
    }
}
