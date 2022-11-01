package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
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
        String notfounderror = MXE.getCustomConfig().getString("messages.custom.error.target-not-found");
        notfounderror = func.colCodes(notfounderror);
        String lang = MXE.getCustomConfig().getString("messages.language");
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if (p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tpaccept]: " + ChatColor.RESET + ChatColor.DARK_RED + "Error: Player is null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }

            ArrayList<Integer> tpas = DB.getPlayerTpas(p.getName());
            ArrayList<String> res = new ArrayList<>();
            if (tpas != null) {
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
            }

            if(res == null || res.isEmpty()) {
                switch (lang) {
                    case "de":
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Anfrage abgelaufen!";
                        break;
                    default:
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Request expired!";
                }
                p.sendMessage(error);
                return true;
            }

            DB.remPlayerTpa(Integer.parseInt(res.get(4)));

            Player t = Bukkit.getPlayerExact(res.get(0));
            if(t == null) {
                func.cMSG(notfounderror);
                return true;
            }
            switch (lang) {
                case "de":
                    t.sendMessage(ChatColor.GOLD + "[Server]: " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.GOLD + " hat deine Anfrage " + ChatColor.DARK_GREEN + "angenommen" + ChatColor.RESET + ChatColor.GOLD + "!");
                    break;
                default:
                    t.sendMessage(ChatColor.GOLD + "[Server]: " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.DARK_GREEN + "accepted" + ChatColor.RESET + ChatColor.GOLD + " your request!");
            }

            if(Boolean.parseBoolean(res.get(3))) {
                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.tpaccept.tp-time"), t.getLocation());
            } else {
                func.teleportDelay(t, MXE.getCustomConfig().getLong("commands.tpaccept.tp-time"), p.getLocation());
            }
        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;
    }
}
