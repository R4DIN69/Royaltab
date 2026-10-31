package com.royalsmp.tab;

import com.royalsmp.tab.hook.LuckPermsHook;
import com.royalsmp.tab.hook.PlaceholderHook;
import com.royalsmp.tab.util.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TabManager {

    private final RoyalTab plugin;
    private final PlaceholderHook placeholderHook;
    private final LuckPermsHook luckPermsHook;

    private BukkitTask updateTask;
    private List<String> headerLines;
    private List<String> footerLines;
    private String logoStyle;
    private List<String> activeLogoLines;

    private boolean enableSorting;
    private boolean pingEnabled;
    private int pingGoodThreshold;
    private int pingMediumThreshold;
    private String pingGoodIcon;
    private String pingMediumIcon;
    private String pingBadIcon;

    private final Map<String, RankInfo> ranks = new LinkedHashMap<>();

    public static class RankInfo {
        final String name;
        final int priority;
        final String badge;
        final String nameColor;
        final String permission;

        public RankInfo(String name, int priority, String badge, String nameColor, String permission) {
            this.name = name;
            this.priority = priority;
            this.badge = badge;
            this.nameColor = nameColor;
            this.permission = permission;
        }
    }

    public TabManager(RoyalTab plugin) {
        this.plugin = plugin;
        this.placeholderHook = new PlaceholderHook();
        this.luckPermsHook = new LuckPermsHook();
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        this.headerLines = config.getStringList("header");
        this.footerLines = config.getStringList("footer");
        this.logoStyle = config.getString("logo-style", "3D_BLOCK");

        // Load active logo lines based on selected logo-style (100% native Minecraft characters)
        List<String> loadedLogo = config.getStringList("logos." + logoStyle);
        if (loadedLogo.isEmpty()) {
            loadedLogo = config.getStringList("logos.3D_BLOCK");
        }
        this.activeLogoLines = loadedLogo;

        this.enableSorting = config.getBoolean("player-list.enable-sorting", true);
        this.pingEnabled = config.getBoolean("player-list.ping.enabled", true);
        this.pingGoodThreshold = config.getInt("player-list.ping.good-threshold", 80);
        this.pingMediumThreshold = config.getInt("player-list.ping.medium-threshold", 160);
        this.pingGoodIcon = config.getString("player-list.ping.good-icon", "<color:#55FF55>📶</color>");
        this.pingMediumIcon = config.getString("player-list.ping.medium-icon", "<color:#FFFF55>📶</color>");
        this.pingBadIcon = config.getString("player-list.ping.bad-icon", "<color:#FF5555>📶</color>");

        // Load ranks
        ranks.clear();
        ConfigurationSection rankSec = config.getConfigurationSection("player-list.ranks");
        if (rankSec != null) {
            for (String key : rankSec.getKeys(false)) {
                ConfigurationSection s = rankSec.getConfigurationSection(key);
                if (s != null) {
                    ranks.put(key.toLowerCase(), new RankInfo(
                            key,
                            s.getInt("priority", 99),
                            s.getString("badge", ""),
                            s.getString("name-color", "<color:#FFFFFF>"),
                            s.getString("permission", "")
                    ));
                }
            }
        }
    }

    public void startTask() {
        stopTask();
        int interval = plugin.getConfig().getInt("update-interval", 20);
        this.updateTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateTablist, 0L, Math.max(1L, interval));
    }

    public void stopTask() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    public void updateTablist() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) return;

        for (Player player : onlinePlayers) {
            updatePlayerHeaderFooter(player);
            updatePlayerNameAndTeam(player);
        }
    }

    private void updatePlayerHeaderFooter(Player player) {
        // Build Header (replaces %logo% with native Minecraft block/gradient text)
        List<String> processedHeader = new ArrayList<>();
        for (String line : headerLines) {
            if (line.contains("%logo%")) {
                for (String logoLine : activeLogoLines) {
                    processedHeader.add(line.replace("%logo%", logoLine));
                }
            } else {
                processedHeader.add(line);
            }
        }

        Component headerComponent = buildComponentFromLines(player, processedHeader);
        Component footerComponent = buildComponentFromLines(player, footerLines);

        player.sendPlayerListHeaderAndFooter(headerComponent, footerComponent);
    }

    private Component buildComponentFromLines(Player player, List<String> lines) {
        Component root = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            String parsed = placeholderHook.setPlaceholders(player, lines.get(i));
            Component lineComp = ColorUtils.parse(parsed);
            root = root.append(lineComp);
            if (i < lines.size() - 1) {
                root = root.append(Component.newline());
            }
        }
        return root;
    }

    private void updatePlayerNameAndTeam(Player player) {
        RankInfo rank = resolveRank(player);

        // Dynamic ping indicator
        String pingIcon = "";
        if (pingEnabled) {
            int ping = player.getPing();
            if (ping <= pingGoodThreshold) {
                pingIcon = " " + pingGoodIcon;
            } else if (ping <= pingMediumThreshold) {
                pingIcon = " " + pingMediumIcon;
            } else {
                pingIcon = " " + pingBadIcon;
            }
        }

        // Formatted tab display name: [RANK] PlayerName 📶
        String formattedString = rank.badge + rank.nameColor + player.getName() + pingIcon;
        String parsed = placeholderHook.setPlaceholders(player, formattedString);
        player.playerListName(ColorUtils.parse(parsed));

        // Scoreboard team sorting (Founders and Staff sorted on top)
        if (enableSorting) {
            Scoreboard board = player.getScoreboard();
            if (board == null || board.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                board = Bukkit.getScoreboardManager().getMainScoreboard();
            }

            String teamName = String.format("%02d_%s", rank.priority, rank.name);
            if (teamName.length() > 16) {
                teamName = teamName.substring(0, 16);
            }

            Team team = board.getTeam(teamName);
            if (team == null) {
                team = board.registerNewTeam(teamName);
            }

            if (!team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }
        }
    }

    private RankInfo resolveRank(Player player) {
        // 1. Check LuckPerms primary group
        if (luckPermsHook.isLuckPermsPresent()) {
            String group = luckPermsHook.getPrimaryGroup(player).toLowerCase();
            if (ranks.containsKey(group)) {
                return ranks.get(group);
            }
        }

        // 2. Check custom permissions in order of priority (lower priority number = higher rank)
        RankInfo bestRank = null;
        for (RankInfo rank : ranks.values()) {
            if (rank.permission != null && !rank.permission.isEmpty() && player.hasPermission(rank.permission)) {
                if (bestRank == null || rank.priority < bestRank.priority) {
                    bestRank = rank;
                }
            }
        }

        if (bestRank != null) {
            return bestRank;
        }

        // 3. Fallback to 'default' or first rank
        return ranks.getOrDefault("default", ranks.values().stream().findFirst().orElse(
                new RankInfo("default", 99, "<gray>[MEMBER] </gray>", "<white>", "")
        ));
    }
}
