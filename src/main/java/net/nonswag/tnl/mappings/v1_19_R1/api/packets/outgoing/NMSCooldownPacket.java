package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.CooldownPacket;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;

import javax.annotation.Nonnull;

public final class NMSCooldownPacket extends CooldownPacket {

    NMSCooldownPacket(@Nonnull Material item, int cooldown) {
        super(item, cooldown);
    }

    @Nonnull
    @Override
    public ClientboundCooldownPacket build() {
        return new ClientboundCooldownPacket(CraftMagicNumbers.getItem(getItem()), getCooldown());
    }
}
