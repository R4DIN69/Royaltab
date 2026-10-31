package com.royalsmp.tab.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LuckPermsHook {

    private final boolean luckPermsPresent;

    public LuckPermsHook() {
        this.luckPermsPresent = Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
    }

    public boolean isLuckPermsPresent() {
        return luckPermsPresent;
    }

    public String getPrimaryGroup(Player player) {
        if (!luckPermsPresent) {
            return "default";
        }
        try {
            net.luckperms.api.LuckPerms api = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = api.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                return user.getPrimaryGroup();
            }
        } catch (Throwable ignored) {
        }
        return "default";
    }
}
