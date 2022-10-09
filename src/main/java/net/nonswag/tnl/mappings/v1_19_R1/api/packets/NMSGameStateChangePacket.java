package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.nonswag.tnl.listener.api.packets.GameStateChangePacket;

import javax.annotation.Nonnull;

public final class NMSGameStateChangePacket extends GameStateChangePacket {

    NMSGameStateChangePacket(@Nonnull Identifier identifier, float state) {
        super(identifier, state);
    }

    @Nonnull
    @Override
    public ClientboundGameEventPacket build() {
        return new ClientboundGameEventPacket(identifier(), getState());
    }

    @Nonnull
    private ClientboundGameEventPacket.Type identifier() {
        return switch (getIdentifier().getId()) {
            case 0 -> ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE;
            case 1 -> ClientboundGameEventPacket.START_RAINING;
            case 2 -> ClientboundGameEventPacket.STOP_RAINING;
            case 3 -> ClientboundGameEventPacket.CHANGE_GAME_MODE;
            case 4 -> ClientboundGameEventPacket.WIN_GAME;
            case 5 -> ClientboundGameEventPacket.DEMO_EVENT;
            case 6 -> ClientboundGameEventPacket.ARROW_HIT_PLAYER;
            case 7 -> ClientboundGameEventPacket.RAIN_LEVEL_CHANGE;
            case 8 -> ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE;
            case 9 -> ClientboundGameEventPacket.PUFFER_FISH_STING;
            case 10 -> ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT;
            case 11 -> ClientboundGameEventPacket.IMMEDIATE_RESPAWN;
            default -> throw new IllegalStateException("Unexpected value: " + getIdentifier().getId());
        };
    }
}
