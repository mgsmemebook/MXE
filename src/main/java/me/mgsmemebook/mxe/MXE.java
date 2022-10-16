package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.commands.*;
import me.mgsmemebook.mxe.db.DB;
import me.mgsmemebook.mxe.db.SQLite;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import static me.mgsmemebook.mxe.func.cMSG;

public final class MXE extends JavaPlugin {

    private static String plDir;
    private DB db;

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
        cMSG(ChatColor.GREEN + "[MXE] MXEssentials wurde gestartet! ");

        //plugin files
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        //EventListeners
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        //DB
        plDir = getDataFolder().getAbsolutePath();
        this.db = new SQLite(this);
        this.db.load();


        //Commands
        Objects.requireNonNull(getCommand("tphere")).setExecutor(new tphere());
        Objects.requireNonNull(getCommand("setrank")).setExecutor(new setrank());
        Objects.requireNonNull(getCommand("gm")).setExecutor(new gm());
        Objects.requireNonNull(getCommand("fly")).setExecutor(new fly());
        Objects.requireNonNull(getCommand("tpall")).setExecutor(new tpall());
        Objects.requireNonNull(getCommand("kill")).setExecutor(new kill());

        //TabCompletion
        Objects.requireNonNull(getCommand("tphere")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("setrank")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("gm")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("fly")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("tpall")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("kill")).setTabCompleter(new TabCompletion());
    }

    @Override
    public void onDisable() {
        System.out.println("[MXE] MXEssentials stoppt!");
    }

    public static String getPlDir() {
        return plDir;
    }
    public DB getRDatabase() {
        return this.db;
    }
}

