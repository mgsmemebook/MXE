package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.commands.*;
import me.mgsmemebook.mxe.db.SQLite;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static me.mgsmemebook.mxe.func.cMSG;

public final class MXE extends JavaPlugin {

    private static String plDir;
    private static Plugin plugin;
    private static Scoreboard playersb;
    private File configFile;
    private static FileConfiguration config;
    public static boolean lpLoaded = false;
    @Override
    public void onEnable() {

        cMSG(ChatColor.WHITE + "[MXE] -------------------------");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + ChatColor.BOLD + " __  __  __   __  " + ChatColor.GOLD + "______      ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + ChatColor.BOLD + "|  \\/  | \\ \\ / / " + ChatColor.GOLD + "|  ____|  ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + ChatColor.BOLD + "| \\  / |  \\ V /  " + ChatColor.GOLD + "| |__      ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + ChatColor.BOLD + "| |\\/| |   > <   " + ChatColor.GOLD + "|  __|      ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + ChatColor.BOLD + "| |  | |  / . \\  " + ChatColor.GOLD + "| |____     ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + ChatColor.BOLD + "|_|  |_| /_/ \\_\\ " + ChatColor.GOLD + "|______|   ");
        cMSG(ChatColor.WHITE + "[MXE] -------------------------");
        cMSG(ChatColor.GREEN + "[MXE] Initializing MXEssentials ");
        plDir = getDataFolder().getAbsolutePath();
        plugin = this;

        //plugin files
        if (!getDataFolder().exists()) {
            if(!getDataFolder().mkdir()) {
                cMSG(ChatColor.RED + "[MXE] Couldn't create plugin folder.");
            }
        }
        File db = new File(plDir+"/db");
        if(!db.exists()) {
            if(!db.mkdir()) {
                func.cMSG(ChatColor.RED + "[MXE] Couldn't create /db");
            } else {
                func.cMSG(ChatColor.BLUE + "[MXE] Created /db");
            }
        }
        createCustomConfig();

        //EventListeners
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        //DB
        SQLite.load("users");

        //Scoreboard
        ScoreboardManager sbman = Bukkit.getScoreboardManager();
        playersb = Objects.requireNonNull(sbman).getNewScoreboard();

        if(Bukkit.getPluginManager().getPlugin("LuckPerms") != null) lpLoaded = Bukkit.getPluginManager().getPlugin("LuckPerms").isEnabled();
        if(lpLoaded) {
            LuckPerms lp = LuckPermsProvider.get();
            lp.getGroupManager().loadAllGroups();
            Set<Group> groupset = lp.getGroupManager().getLoadedGroups();
            for(Group g : groupset) {
                Team team = playersb.registerNewTeam(g.getName());
                String prefix = g.getCachedData().getMetaData().getPrefix();
                if(prefix == null) continue;
                team.setPrefix(func.colCodes(prefix));
                String color = g.getCachedData().getMetaData().getMetaValue("color");
                cMSG(ChatColor.DARK_GRAY + "[MXE] Group found: " + g.getName());
                if(color != null) {
                    ChatColor chatColor = ChatColor.getByChar(color.replaceAll("&", ""));
                    if(chatColor != null && chatColor.isColor()) {
                        cMSG(ChatColor.DARK_GRAY + "[MXE] Color found: " + chatColor.name());
                        team.setColor(chatColor);
                    }
                }
            }
            Objects.requireNonNull(getCommand("setrank")).setExecutor(new setrank());
            Objects.requireNonNull(getCommand("setrank")).setTabCompleter(new TabCompletion());
        } else {
            cMSG(ChatColor.YELLOW + "[MXE] Warn: LuckPerms not found. Please put LuckPerms inside your plugins folder to use permissions and prefixes.");
        }

        //ProtocolLib
        if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            Objects.requireNonNull(getCommand("nick")).setExecutor(new nick());
            Objects.requireNonNull(getCommand("nick")).setTabCompleter(new TabCompletion());
            Nametag.NameChanger();
        } else {
            cMSG(ChatColor.YELLOW + "[MXE] Warn: ProtocolLib not found. Please put ProtocolLib inside your plugins folder to use the /nick command.");
        }

        //Commands
        Objects.requireNonNull(getCommand("back")).setExecutor(new back());
        Objects.requireNonNull(getCommand("ban")).setExecutor(new ban());
        Objects.requireNonNull(getCommand("fly")).setExecutor(new fly());
        Objects.requireNonNull(getCommand("gm")).setExecutor(new gm());
        Objects.requireNonNull(getCommand("god")).setExecutor(new god());
        Objects.requireNonNull(getCommand("help")).setExecutor(new help());
        Objects.requireNonNull(getCommand("home")).setExecutor(new home());
        Objects.requireNonNull(getCommand("kick")).setExecutor(new kick());
        Objects.requireNonNull(getCommand("kill")).setExecutor(new kill());
        Objects.requireNonNull(getCommand("mute")).setExecutor(new mute());
        Objects.requireNonNull(getCommand("tpa")).setExecutor(new tpa());
        Objects.requireNonNull(getCommand("tpaccept")).setExecutor(new tpaccept());
        Objects.requireNonNull(getCommand("tpahere")).setExecutor(new tpahere());
        Objects.requireNonNull(getCommand("tpall")).setExecutor(new tpall());
        Objects.requireNonNull(getCommand("tpdeny")).setExecutor(new tpdeny());
        Objects.requireNonNull(getCommand("tphere")).setExecutor(new tphere());
        Objects.requireNonNull(getCommand("unban")).setExecutor(new unban());
        Objects.requireNonNull(getCommand("unmute")).setExecutor(new unmute());
        Objects.requireNonNull(getCommand("vanish")).setExecutor(new vanish());

        //TabCompletion
        Objects.requireNonNull(getCommand("back")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("ban")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("fly")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("gm")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("god")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("help")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("home")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("kick")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("kill")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("mute")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tpa")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tpaccept")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tpahere")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tpall")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tpdeny")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tphere")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("unban")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("unmute")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("vanish")).setTabCompleter(new TabCompletion());

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
        return config;
    }
    private void createCustomConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] Error: Couldn't load plugin config");
            func.cMSG(ChatColor.DARK_RED + "[MXE] Error: " + ex.getMessage());
        }
    }
}

