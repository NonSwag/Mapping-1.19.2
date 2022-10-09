package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.nonswag.tnl.listener.api.packets.LivingEntitySpawnPacket;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public final class NMSLivingEntitySpawnPacket extends LivingEntitySpawnPacket {

    NMSLivingEntitySpawnPacket(@Nonnull LivingEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public ClientboundAddEntityPacket build() {
        return new ClientboundAddEntityPacket(((CraftLivingEntity) getEntity()).getHandle());
    }
}
