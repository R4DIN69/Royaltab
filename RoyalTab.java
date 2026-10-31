package com.royalsmp.tab;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class RoyalTab extends JavaPlugin implements Listener {

    private TabManager tabManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize TabManager
        this.tabManager = new TabManager(this);
        this.tabManager.loadConfig();
        this.tabManager.startTask();

        // Register Command
        PluginCommand cmd = getCommand("royaltab");
        if (cmd != null) {
            TabCommand tabCommand = new TabCommand(this);
            cmd.setExecutor(tabCommand);
            cmd.setTabCompleter(tabCommand);
        }

        // Register Events
        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info("RoyalTab v" + getDescription().getVersion() + " has been successfully enabled for Paper 1.21!");
    }

    @Override
    public void onDisable() {
        if (tabManager != null) {
            tabManager.stopTask();
        }
        getLogger().info("RoyalTab has been disabled.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Run update on join with a 1 tick delay to ensure player state is initialized
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (event.getPlayer().isOnline()) {
                tabManager.updateTablist();
            }
        }, 1L);
    }

    public TabManager getTabManager() {
        return tabManager;
    }
}
