package net.nonswag.tnl.mappings.v1_19_R1.api.plugin;

import net.nonswag.tnl.listener.api.plugin.PluginHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;

import javax.annotation.Nonnull;

public class NMSPluginHelper extends PluginHelper {

    @Nonnull
    @Override
    public SimpleCommandMap getCommandMap() {
        return ((CraftServer) Bukkit.getServer()).getServer().server.getCommandMap();
    }
}
