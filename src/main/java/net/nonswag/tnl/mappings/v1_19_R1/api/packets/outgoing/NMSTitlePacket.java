package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.nonswag.tnl.listener.api.packets.outgoing.TitlePacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NMSTitlePacket extends TitlePacket {

    NMSTitlePacket(@Nonnull Action action, @Nullable String text, int timeIn, int timeStay, int timeOut) {
        super(action, text, timeIn, timeStay, timeOut);
    }

    @Nonnull
    @Override
    public Packet<ClientGamePacketListener> build() {
        return switch (getAction()) {
            case CLEAR -> new ClientboundClearTitlesPacket(false);
            case RESET -> new ClientboundClearTitlesPacket(true);
            case TITLE -> new ClientboundSetTitleTextPacket(Component.nullToEmpty(getText()));
            case SUBTITLE -> new ClientboundSetSubtitleTextPacket(Component.nullToEmpty(getText()));
            case ACTIONBAR -> new ClientboundSetActionBarTextPacket(Component.nullToEmpty(getText()));
            case TIMES -> new ClientboundSetTitlesAnimationPacket(getTimeIn(), getTimeStay(), getTimeOut());
        };
    }
}
