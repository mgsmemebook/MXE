package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.db.DB;
import me.mgsmemebook.mxe.func;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class tpaccept implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        Player p = Bukkit.getPlayerExact(sender.getName());
        if (p == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tpaccept]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
            func.cMSG(error);
            return true;
        }

        ArrayList<Integer> tpas = DB.getPlayerTpas(p.getName());
        ArrayList<String> res = new ArrayList<>();

        for(int i:tpas) {
            res = DB.getPlayerTpa(i);
            if(res == null) continue;

            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());
            long now = cl.getTimeInMillis();
            long then = Long.parseLong(Objects.requireNonNull(res).get(2));
            if(now - then > 600000) {
                DB.remPlayerTpa(i);
            } else {
                break;
            }
        }

        if(res.isEmpty()) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Anfrage abgelaufen!";
            p.sendMessage(error);
            return true;
        }

        DB.remPlayerTpa(Integer.parseInt(res.get(4)));

        Player t = Bukkit.getPlayerExact(res.get(0));
        if(t == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE accept]: " + ChatColor.RESET + ChatColor.DARK_RED + "t = null (" + res.get(0) + ")";
            func.cMSG(error);
            return true;
        }
        t.sendMessage(ChatColor.GOLD + "[Server]: " + ChatColor.RESET + p.getDisplayName() + ChatColor.RESET + ChatColor.GOLD + " hat deine Anfrage " + ChatColor.DARK_GREEN + "angenommen" + ChatColor.RESET + ChatColor.BOLD + "!");

        if(Boolean.parseBoolean(res.get(3))) {
            t.teleport(p.getLocation());
        } else {
            p.teleport(t.getLocation());
        }
        return true;
    }
}
