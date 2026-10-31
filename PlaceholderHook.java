package com.royalsmp.tab.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderHook {

    private final boolean papiPresent;

    public PlaceholderHook() {
        this.papiPresent = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public boolean isPapiPresent() {
        return papiPresent;
    }

    public String setPlaceholders(Player player, String text) {
        if (text == null) {
            return "";
        }

        // Internal default replacements
        String result = text
                .replace("%player%", player.getName())
                .replace("%player_name%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()))
                .replace("%ping%", String.valueOf(player.getPing()));

        // If PlaceholderAPI is present, pass through PAPI
        if (papiPresent) {
            try {
                result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, result);
            } catch (Throwable ignored) {
            }
        }

        return result;
    }
}
