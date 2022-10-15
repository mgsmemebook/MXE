package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.commands.fly;
import me.mgsmemebook.mxe.commands.gm;
import me.mgsmemebook.mxe.commands.setrank;
import me.mgsmemebook.mxe.commands.tphere;
import me.mgsmemebook.mxe.eventlisteners.PlayerEvents;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static me.mgsmemebook.mxe.func.cMSG;

public final class MXE extends JavaPlugin {

    @Override
    public void onEnable() {
        cMSG(ChatColor.WHITE + "[MXE] -------------------------");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + " __  __  __   __  " + ChatColor.GOLD + "______      ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + "|  \\/  | \\ \\ / / " + ChatColor.GOLD + "|  ____|  ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + "| \\  / |  \\ V /  " + ChatColor.GOLD + "| |__      ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + "| |\\/| |   > <   " + ChatColor.GOLD + "|  __|      ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + "| |  | |  / . \\  " + ChatColor.GOLD + "| |____     ");
        cMSG(ChatColor.WHITE + "[MXE] " + ChatColor.BLUE + "|_|  |_| /_/ \\_\\ " + ChatColor.GOLD + "|______|   ");
        cMSG(ChatColor.WHITE + "[MXE] -------------------------");
        cMSG(ChatColor.GREEN + "[MXE] MXEssentials wurde gestartet! ");

        //EventListeners
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        //Commands
        Objects.requireNonNull(getCommand("tphere")).setExecutor(new tphere());
        Objects.requireNonNull(getCommand("setrank")).setExecutor(new setrank());
        Objects.requireNonNull(getCommand("gm")).setExecutor(new gm());
        Objects.requireNonNull(getCommand("fly")).setExecutor(new fly());
    }

    @Override
    public void onDisable() {
        System.out.println("[MXE] MXEssentials stoppt!");
    }

}

