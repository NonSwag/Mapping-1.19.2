package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.nonswag.tnl.listener.api.packets.ResourcePackPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NMSResourcePackPacket extends ResourcePackPacket {

    public NMSResourcePackPacket(@Nonnull String url, @Nullable String hash, @Nullable String prompt, boolean required) {
        super(url, hash, prompt, required);
    }

    @Nonnull
    @Override
    public ClientboundResourcePackPacket build() {
        return new ClientboundResourcePackPacket(getUrl(), String.valueOf(getHash()), isRequired(), getPrompt() != null ? Component.literal(getPrompt()) : null);
    }
}
