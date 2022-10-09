package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.nonswag.tnl.listener.api.packets.EntityMetadataPacket;

import javax.annotation.Nonnull;

public final class NMSEntityMetadataPacket extends EntityMetadataPacket<SynchedEntityData> {

    NMSEntityMetadataPacket(int entityId, @Nonnull SynchedEntityData dataWatcher, boolean updateAll) {
        super(entityId, dataWatcher, updateAll);
    }

    @Nonnull
    @Override
    public ClientboundSetEntityDataPacket build() {
        return new ClientboundSetEntityDataPacket(getEntityId(), getDataWatcher(), isUpdateAll());
    }
}
