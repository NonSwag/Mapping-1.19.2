package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.nonswag.tnl.listener.api.packets.BossBarPacket;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_19_R1.boss.CraftBossBar;

import javax.annotation.Nonnull;

public final class NMSBossBarPacket extends BossBarPacket {

    NMSBossBarPacket(@Nonnull Action action, @Nonnull BossBar bossBar) {
        super(action, bossBar);
    }

    @Nonnull
    @Override
    public ClientboundBossEventPacket build() {
        return switch (getAction()) {
            case ADD -> ClientboundBossEventPacket.createAddPacket(((CraftBossBar) getBossBar()).getHandle());
            case REMOVE -> ClientboundBossEventPacket.createRemovePacket(((CraftBossBar) getBossBar()).getHandle().getId());
            case UPDATE_PCT -> ClientboundBossEventPacket.createUpdateProgressPacket(((CraftBossBar) getBossBar()).getHandle());
            case UPDATE_NAME -> ClientboundBossEventPacket.createUpdateNamePacket(((CraftBossBar) getBossBar()).getHandle());
            case UPDATE_STYLE -> ClientboundBossEventPacket.createUpdateStylePacket(((CraftBossBar) getBossBar()).getHandle());
            case UPDATE_PROPERTIES -> ClientboundBossEventPacket.createUpdatePropertiesPacket(((CraftBossBar) getBossBar()).getHandle());
        };
    }
}
