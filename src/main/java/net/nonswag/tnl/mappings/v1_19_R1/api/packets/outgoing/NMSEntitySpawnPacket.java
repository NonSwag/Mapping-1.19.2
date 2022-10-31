package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.EntitySpawnPacket;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

public final class NMSEntitySpawnPacket extends EntitySpawnPacket {

    NMSEntitySpawnPacket(@Nonnull Entity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public ClientboundAddEntityPacket build() {
        return new ClientboundAddEntityPacket(((CraftEntity) getEntity()).getHandle());
    }
}
