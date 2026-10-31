package com.royalsmp.tab;

import com.royalsmp.tab.util.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabCommand implements CommandExecutor, TabCompleter {

    private final RoyalTab plugin;

    public TabCommand(RoyalTab plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("royaltab.admin")) {
            String noPerm = plugin.getConfig().getString("messages.no-permission", "<red>You do not have permission.</red>");
            sender.sendMessage(ColorUtils.parse(noPerm));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getTabManager().loadConfig();
            plugin.getTabManager().startTask();
            plugin.getTabManager().updateTablist();

            String prefix = plugin.getConfig().getString("messages.prefix", "[RoyalTab] ");
            String reloaded = plugin.getConfig().getString("messages.reloaded", "<green>Configuration reloaded!</green>");
            sender.sendMessage(ColorUtils.parse(prefix + reloaded));
            return true;
        }

        // Show help / info
        String prefix = plugin.getConfig().getString("messages.prefix", "[RoyalTab] ");
        sender.sendMessage(ColorUtils.parse(prefix + "<gray>RoyalTab v" + plugin.getDescription().getVersion() + " by Royal SMP"));
        sender.sendMessage(ColorUtils.parse("<yellow>Usage: <gray>/" + label + " reload"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> sub = new ArrayList<>();
            if ("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("royaltab.admin")) {
                sub.add("reload");
            }
            return sub;
        }
        return Collections.emptyList();
    }
}
