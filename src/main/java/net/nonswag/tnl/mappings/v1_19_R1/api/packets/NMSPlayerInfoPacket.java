package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.nonswag.tnl.listener.api.packets.PlayerInfoPacket;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class NMSPlayerInfoPacket extends PlayerInfoPacket {

    NMSPlayerInfoPacket(@Nonnull Player player, @Nonnull Action action) {
        super(player, action);
    }

    @Nonnull
    @Override
    public ClientboundPlayerInfoPacket build() {
        return new ClientboundPlayerInfoPacket(action(), ((CraftPlayer) getPlayer()).getHandle());
    }

    @Nonnull
    private ClientboundPlayerInfoPacket.Action action() {
        return switch (getAction()) {
            case ADD_PLAYER -> ClientboundPlayerInfoPacket.Action.ADD_PLAYER;
            case REMOVE_PLAYER -> ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER;
            case UPDATE_LATENCY -> ClientboundPlayerInfoPacket.Action.UPDATE_LATENCY;
            case UPDATE_GAME_MODE -> ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE;
            case UPDATE_DISPLAY_NAME -> ClientboundPlayerInfoPacket.Action.UPDATE_DISPLAY_NAME;
        };
    }
}
