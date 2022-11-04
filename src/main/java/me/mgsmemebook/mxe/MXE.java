package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.commands.*;
import me.mgsmemebook.mxe.db.SQLite;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static me.mgsmemebook.mxe.func.cMSG;

public final class MXE extends JavaPlugin {

    private static String plDir;
    private static Plugin plugin;
    private static Scoreboard playersb;
    public static boolean lpLoaded = false;
    @Override
    public void onEnable() {
        plDir = getDataFolder().getAbsolutePath();
        plugin = this;
        cMSG(ChatColor.WHITE + "[MXE] -------------------------", 0);
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.AQUA + ChatColor.BOLD + " __  __  __   __  " + ChatColor.GOLD + "______      ", 0);
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.AQUA + ChatColor.BOLD + "|  \\/  | \\ \\ / / " + ChatColor.GOLD + "|  ____|  ", 0);
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.AQUA + ChatColor.BOLD + "| \\  / |  \\ V /  " + ChatColor.GOLD + "| |__      ", 0);
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.AQUA + ChatColor.BOLD + "| |\\/| |   > <   " + ChatColor.GOLD + "|  __|      ", 0);
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.AQUA + ChatColor.BOLD + "| |  | |  / . \\  " + ChatColor.GOLD + "| |____     ", 0);
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.AQUA + ChatColor.BOLD + "|_|  |_| /_/ \\_\\ " + ChatColor.GOLD + "|______|   ", 0);
        cMSG(ChatColor.WHITE + "[MXE] -------------------------", 0);
        cMSG(ChatColor.GREEN + "[MXE] Initializing MXEssentials ", 0);

        //plugin files
        if (!getDataFolder().exists()) {
            if(!getDataFolder().mkdir()) {
                cMSG(ChatColor.RED + "[MXE] Couldn't create plugin folder.", 1);
            }
        }
        File db = new File(plDir+"/db");
        if(!db.exists()) {
            if(!db.mkdir()) {
                func.cMSG(ChatColor.RED + "[MXE] Couldn't create /db", 1);
            } else {
                func.cMSG(ChatColor.BLUE + "[MXE] Created /db", 3);
            }
        }
        saveDefaultConfig();
        func.checkAllSections();

        //DB
        SQLite.load("users");

        Plugin lpPlugin = Bukkit.getPluginManager().getPlugin("LuckPerms");
        if(lpPlugin != null) lpLoaded = lpPlugin.isEnabled();
        if(lpLoaded) {
            //Scoreboard
            ScoreboardManager sbman = Bukkit.getScoreboardManager();
            if(sbman == null) {
                cMSG(ChatColor.YELLOW + "[MXE]: Warn: ScoreBoardManager is null! Plugin probably won't work correctly.", 2);
            } else {
                playersb = sbman.getNewScoreboard();
            }

            LuckPerms lp = LuckPermsProvider.get();
            lp.getGroupManager().loadAllGroups();
            Set<Group> groupset = lp.getGroupManager().getLoadedGroups();
            for(Group g : groupset) {
                Team team = playersb.registerNewTeam(g.getName());
                String prefix = g.getCachedData().getMetaData().getPrefix();
                if(prefix == null) continue;
                team.setPrefix(func.colCodes(prefix));
                String color = g.getCachedData().getMetaData().getMetaValue("color");
                cMSG(ChatColor.DARK_GRAY + "[MXE] Group found: " + g.getName(), 3);
                if(color != null) {
                    ChatColor chatColor = ChatColor.getByChar(color.replaceAll("&", ""));
                    if(chatColor != null && chatColor.isColor()) {
                        cMSG(ChatColor.DARK_GRAY + "[MXE] Color found: " + chatColor.name(), 3);
                        team.setColor(chatColor);
                    }
                }
            }
            Objects.requireNonNull(getCommand("setrank")).setExecutor(new setrank());
            Objects.requireNonNull(getCommand("setrank")).setTabCompleter(new TabCompletion());
        } else {
            cMSG(ChatColor.YELLOW + "[MXE] Warn: LuckPerms not found. Please put LuckPerms inside your plugins folder to use permissions and prefixes.", 2);
        }

        //ProtocolLib
        if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            Objects.requireNonNull(getCommand("nick")).setExecutor(new nick());
            Objects.requireNonNull(getCommand("nick")).setTabCompleter(new TabCompletion());
            Nametag.NameChanger();
        } else {
            cMSG(ChatColor.YELLOW + "[MXE] Warn: ProtocolLib not found. Please put ProtocolLib inside your plugins folder to use the /nick command.", 2);
        }

        //EventListeners
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        //Commands
        List<String> disabledCommands;
        disabledCommands = (List<String>) getCustomConfig().getList("commands.disabled");

        if(disabledCommands == null || !disabledCommands.contains("help")) {
            if (plugin.getConfig().getBoolean("commands.help.replace-vanilla")) {
                Objects.requireNonNull(getCommand("help")).setExecutor(new help());
                Objects.requireNonNull(getCommand("help")).setTabCompleter(new TabCompletion());
            } else {
                Objects.requireNonNull(getCommand("mxe")).setExecutor(new help());
                Objects.requireNonNull(getCommand("mxe")).setTabCompleter(new TabCompletion());
            }
        }if(disabledCommands == null || !disabledCommands.contains("back")) {
            Objects.requireNonNull(getCommand("back")).setExecutor(new back());
            Objects.requireNonNull(getCommand("back")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("ban")) {
            Objects.requireNonNull(getCommand("ban")).setExecutor(new ban());
            Objects.requireNonNull(getCommand("ban")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("fly")) {
            Objects.requireNonNull(getCommand("fly")).setExecutor(new fly());
            Objects.requireNonNull(getCommand("fly")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("back")) {
            Objects.requireNonNull(getCommand("gm")).setExecutor(new gm());
            Objects.requireNonNull(getCommand("gm")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("god")) {
            Objects.requireNonNull(getCommand("god")).setExecutor(new god());
            Objects.requireNonNull(getCommand("god")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("home")) {
            Objects.requireNonNull(getCommand("home")).setExecutor(new home());
            Objects.requireNonNull(getCommand("home")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("kick")) {
            Objects.requireNonNull(getCommand("kick")).setExecutor(new kick());
            Objects.requireNonNull(getCommand("kick")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("kill")) {
            Objects.requireNonNull(getCommand("kill")).setExecutor(new kill());
            Objects.requireNonNull(getCommand("kill")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("mute")) {
            Objects.requireNonNull(getCommand("mute")).setExecutor(new mute());
            Objects.requireNonNull(getCommand("mute")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("pm")) {
            Objects.requireNonNull(getCommand("pm")).setExecutor(new pm());
            Objects.requireNonNull(getCommand("pm")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("reply")) {
            Objects.requireNonNull(getCommand("reply")).setExecutor(new reply());
            Objects.requireNonNull(getCommand("reply")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("tpa")) {
            Objects.requireNonNull(getCommand("tpa")).setExecutor(new tpa());
            Objects.requireNonNull(getCommand("tpa")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("tpaccept")) {
            Objects.requireNonNull(getCommand("tpaccept")).setExecutor(new tpaccept());
            Objects.requireNonNull(getCommand("tpaccept")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("tpahere")) {
            Objects.requireNonNull(getCommand("tpahere")).setExecutor(new tpahere());
            Objects.requireNonNull(getCommand("tpahere")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("tpall")) {
            Objects.requireNonNull(getCommand("tpall")).setExecutor(new tpall());
            Objects.requireNonNull(getCommand("tpall")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("tpdeny")) {
            Objects.requireNonNull(getCommand("tpdeny")).setExecutor(new tpdeny());
            Objects.requireNonNull(getCommand("tpdeny")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("tphere")) {
            Objects.requireNonNull(getCommand("tphere")).setExecutor(new tphere());
            Objects.requireNonNull(getCommand("tphere")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("unban")) {
            Objects.requireNonNull(getCommand("unban")).setExecutor(new unban());
            Objects.requireNonNull(getCommand("unban")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("unmute")) {
            Objects.requireNonNull(getCommand("unmute")).setExecutor(new unmute());
            Objects.requireNonNull(getCommand("unmute")).setTabCompleter(new TabCompletion());
        } if(disabledCommands == null || !disabledCommands.contains("vanish")) {
            Objects.requireNonNull(getCommand("vanish")).setExecutor(new vanish());
            Objects.requireNonNull(getCommand("vanish")).setTabCompleter(new TabCompletion());
        }

        //In case of reload
        if(lpLoaded) {
            LuckPerms lp = LuckPermsProvider.get();
            for (Player t : Bukkit.getOnlinePlayers()) {
                User tu = lp.getUserManager().getUser(t.getUniqueId());
                if (tu == null) continue;
                Group tg = lp.getGroupManager().getGroup(tu.getPrimaryGroup());
                if (tg == null) continue;
                func.updateUser(t, tg);
            }
        }
    }

    @Override
    public void onDisable() {
        System.out.println("[MXE] MXEssentials stopping!");
    }

    public static String getPlDir() {
        return plDir;
    }
    public static Plugin getPlugin() {
        return plugin;
    }

    public static String getPlayerPrefix(Player p) {
        Team team = playersb.getEntryTeam(p.getDisplayName());
        if(team == null) {
            return "";
        }
        return team.getPrefix();
    }

    public static Scoreboard getPlayerSB() {
        return playersb;
    }
    public static void updatePlayerSB() {
        for(Player t:Bukkit.getOnlinePlayers()) {
            t.setScoreboard(playersb);
        }
    }

    public static FileConfiguration getCustomConfig() {
        return plugin.getConfig();
    }
}

