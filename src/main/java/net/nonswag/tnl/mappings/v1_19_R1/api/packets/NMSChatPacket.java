package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.nonswag.tnl.listener.api.packets.ChatPacket;

import javax.annotation.Nonnull;
import java.util.UUID;

public final class NMSChatPacket extends ChatPacket {

    NMSChatPacket(@Nonnull String message, @Nonnull Type type, @Nonnull UUID sender) {
        super(message, type, sender);
    }

    @Nonnull
    @Override
    public Packet<ClientGamePacketListener> build() {
        /*
        return switch (getType()) {
            case CHAT -> new ClientboundPlayerChatPacket();
            case SYSTEM, GAME_INFO -> new ClientboundSystemChatPacket(Component.nullToEmpty(getMessage()), false);
        };
         */
        return new ClientboundSystemChatPacket(Component.nullToEmpty(getMessage()), false);
    }
}
