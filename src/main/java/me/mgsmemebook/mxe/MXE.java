package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.commands.*;
import me.mgsmemebook.mxe.db.SQLite;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import static me.mgsmemebook.mxe.func.cMSG;

public final class MXE extends JavaPlugin {

    private static String plDir;

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
        cMSG(ChatColor.GREEN + "[MXE] MXEssentials started! ");

        //plugin files
        if (!getDataFolder().exists()) {
            if(!getDataFolder().mkdir()) {
                cMSG(ChatColor.RED + "[MXE] Couldn't create plugin folder.");
            }
        }

        //EventListeners
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        //DB
        plDir = getDataFolder().getAbsolutePath();
        SQLite.load();

        //Commands
        Objects.requireNonNull(getCommand("ban")).setExecutor(new ban());
        Objects.requireNonNull(getCommand("fly")).setExecutor(new fly());
        Objects.requireNonNull(getCommand("gm")).setExecutor(new gm());
        Objects.requireNonNull(getCommand("kick")).setExecutor(new kick());
        Objects.requireNonNull(getCommand("kill")).setExecutor(new kill());
        Objects.requireNonNull(getCommand("setrank")).setExecutor(new setrank());
        Objects.requireNonNull(getCommand("tpall")).setExecutor(new tpall());
        Objects.requireNonNull(getCommand("tphere")).setExecutor(new tphere());
        Objects.requireNonNull(getCommand("unban")).setExecutor(new unban());

        //TabCompletion
        Objects.requireNonNull(getCommand("ban")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("fly")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("gm")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("kick")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("kill")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("setrank")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tpall")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tphere")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("unban")).setTabCompleter(new TabCompletion());
    }

    @Override
    public void onDisable() {
        System.out.println("[MXE] MXEssentials stopping!");
    }

    public static String getPlDir() {
        return plDir;
    }
}

