package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.nonswag.tnl.listener.api.packets.NamedEntitySpawnPacket;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import javax.annotation.Nonnull;

public final class NMSNamedEntitySpawnPacket extends NamedEntitySpawnPacket {

    NMSNamedEntitySpawnPacket(@Nonnull HumanEntity human) {
        super(human);
    }

    @Nonnull
    @Override
    public ClientboundAddPlayerPacket build() {
        return new ClientboundAddPlayerPacket(((CraftHumanEntity) getHuman()).getHandle());
    }
}
