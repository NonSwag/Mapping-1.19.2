package net.nonswag.tnl.mappings.v1_19_R1;

import net.nonswag.tnl.listener.api.bossbar.TNLBossBar;
import net.nonswag.tnl.listener.api.enchantment.Enchant;
import net.nonswag.tnl.listener.api.entity.TNLArmorStand;
import net.nonswag.tnl.listener.api.entity.TNLEntityPlayer;
import net.nonswag.tnl.listener.api.entity.TNLFallingBlock;
import net.nonswag.tnl.listener.api.item.ItemHelper;
import net.nonswag.tnl.listener.api.item.TNLItem;
import net.nonswag.tnl.listener.api.mapper.Mapping;
import net.nonswag.tnl.listener.api.player.GameProfile;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.api.plugin.PluginHelper;
import net.nonswag.tnl.listener.api.plugin.PluginUpdate;
import net.nonswag.tnl.listener.api.settings.Settings;
import net.nonswag.tnl.listener.api.version.Version;
import net.nonswag.tnl.mappings.v1_19_R1.api.bossbar.NMSBossBar;
import net.nonswag.tnl.mappings.v1_19_R1.api.enchantments.EnchantmentWrapper;
import net.nonswag.tnl.mappings.v1_19_R1.api.entity.NMSArmorStand;
import net.nonswag.tnl.mappings.v1_19_R1.api.entity.NMSEntityPlayer;
import net.nonswag.tnl.mappings.v1_19_R1.api.entity.NMSFallingBlock;
import net.nonswag.tnl.mappings.v1_19_R1.api.item.NMSItem;
import net.nonswag.tnl.mappings.v1_19_R1.api.item.NMSItemHelper;
import net.nonswag.tnl.mappings.v1_19_R1.api.packets.PacketManager;
import net.nonswag.tnl.mappings.v1_19_R1.api.player.NMSPlayer;
import net.nonswag.tnl.mappings.v1_19_R1.api.plugin.NMSPluginHelper;
import net.nonswag.tnl.mappings.v1_19_R1.listeners.PacketListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

@Mapping.Info(id = "Mapping-1.19.2", name = "Origin 1.19.2", authors = "NonSwag", description = "An official TNL-Production")
public class Mappings extends Mapping {

    public Mappings(@Nonnull File file) {
        super(file);
    }

    @Override
    public void enable() {
        getEventManager().registerListener(new PacketListener());
        async(() -> {
            if (Settings.AUTO_UPDATER.getValue()) new PluginUpdate(this).downloadUpdate();
        });
    }

    @Nonnull
    @Override
    public Version getVersion() {
        return Version.v1_19_1;
    }

    @Nonnull
    @Override
    public String overrideLogger() {
        return "TerminalConsole";
    }

    @Nonnull
    @Override
    public TNLPlayer createPlayer(@Nonnull Player player) {
        return new NMSPlayer(player);
    }

    @Nonnull
    @Override
    public TNLItem createItem(@Nonnull ItemStack itemStack) {
        return new NMSItem(itemStack);
    }

    @Nonnull
    @Override
    public TNLBossBar createBossBar(@Nonnull String id, @Nonnull String text, @Nonnull BarColor color, @Nonnull BarStyle style, double progress, @Nonnull BarFlag... flags) {
        return new NMSBossBar(id, text, color, style, progress, flags);
    }

    @Nonnull
    @Override
    public TNLFallingBlock createFallingBlock(@Nonnull Location location, @Nonnull Material material) {
        return new NMSFallingBlock(location, material);
    }

    @Nonnull
    @Override
    public TNLArmorStand createArmorStand(@Nonnull World world, double x, double y, double z, float yaw, float pitch) {
        return new NMSArmorStand(world, x, y, z, yaw, pitch);
    }

    @Nonnull
    @Override
    public TNLEntityPlayer createEntityPlayer(@Nonnull World world, double x, double y, double z, float yaw, float pitch, @Nonnull GameProfile gameProfile) {
        return new NMSEntityPlayer(world, x, y, z, yaw, pitch, gameProfile);
    }

    @Nonnull
    @Override
    public Enchant createEnchant(@Nonnull NamespacedKey key, @Nonnull String name, @Nonnull EnchantmentTarget target) {
        return new EnchantmentWrapper(key, name, target);
    }

    @Nonnull
    @Override
    public ItemHelper itemHelper() {
        return itemHelper == null ? itemHelper = new NMSItemHelper() : itemHelper;
    }

    @Nonnull
    @Override
    public PluginHelper pluginHelper() {
        return pluginHelper == null ? pluginHelper = new NMSPluginHelper() : pluginHelper;
    }

    @Nonnull
    @Override
    public Packets packets() {
        return packets == null ? packets = new PacketManager() : packets;
    }

    @Nullable
    @Override
    public BiomeProvider getDefaultBiomeProvider(@Nonnull String name, @Nullable String id) {
        return null;
    }
}
