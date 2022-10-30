package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.commands.*;
import me.mgsmemebook.mxe.db.SQLite;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.Objects;
import java.util.Set;

import static me.mgsmemebook.mxe.func.cMSG;

public final class MXE extends JavaPlugin {

    private static String plDir;
    private static Plugin plugin;
    private static Scoreboard playersb;
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
        cMSG(ChatColor.GREEN + "[MXE] initializing MXEssentials ");
        plDir = getDataFolder().getAbsolutePath();
        plugin = this;

        //plugin files
        if (!getDataFolder().exists()) {
            if(!getDataFolder().mkdir()) {
                cMSG(ChatColor.RED + "[MXE] Couldn't create plugin folder.");
            }
        }
        File lib = new File(plDir+"/lib");
        if(!lib.exists()) {
            if(!lib.mkdir()) {
                func.cMSG(ChatColor.RED + "[MXE] Couldn't create /lib");
            } else {
                func.cMSG(ChatColor.BLUE + "[MXE] Created /lib");
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

        //ProtocolLib
        Nametag.NameChanger();

        //EventListeners
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        //DB
        SQLite.load("users");

        //Scoreboard
        LuckPerms lp = LuckPermsProvider.get();

        ScoreboardManager sbman = Bukkit.getScoreboardManager();
        playersb = Objects.requireNonNull(sbman).getNewScoreboard();
        lp.getGroupManager().loadAllGroups();
        Set<Group> groupset = lp.getGroupManager().getLoadedGroups();
        for(Group g:groupset) {
            Team team = playersb.registerNewTeam(g.getName());
            String prefix = g.getCachedData().getMetaData().getPrefix();
            if(prefix == null) continue;
            team.setPrefix(func.colCodes(prefix));
            String color = g.getCachedData().getMetaData().getMetaValue("color");
            cMSG(ChatColor.DARK_GRAY + "[MXE] Group found: " + g.getName());
            if (color != null) {
                ChatColor chatColor = ChatColor.getByChar(color.replaceAll("&", ""));
                if(chatColor.isColor()) {
                    cMSG(ChatColor.DARK_GRAY + "[MXE] Color found: " + chatColor.name());
                    team.setColor(chatColor);
                }
            }
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
        Objects.requireNonNull(getCommand("nick")).setExecutor(new nick());
        Objects.requireNonNull(getCommand("setrank")).setExecutor(new setrank());
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
        Objects.requireNonNull(getCommand("nick")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("setrank")).setTabCompleter(new TabCompletion());
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
        for(Player t:Bukkit.getOnlinePlayers()) {
            User tu = lp.getUserManager().getUser(t.getUniqueId());
            if(tu == null) continue;
            Group tg = lp.getGroupManager().getGroup(tu.getPrimaryGroup());
            if(tg == null) continue;
            func.updateUser(t, tg);
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
    public static Scoreboard getPlayerSB() {
        return playersb;
    }
    public static String getPlayerPrefix(Player p) {
        Team team = playersb.getEntryTeam(p.getDisplayName());
        if(team == null) {
            return "";
        }
        return team.getPrefix();
    }
    public static void updatePlayerSB() {
        for(Player t:Bukkit.getOnlinePlayers()) {
            t.setScoreboard(playersb);
        }
    }
}

