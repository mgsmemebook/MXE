package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.db.DB;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class reply implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) return true;
            String lastpm = DB.getLastPm(p.getUniqueId());
            if(lastpm == null) return true;
            if(args.length > 1) {
                StringBuilder msg = new StringBuilder(args[0]);
                for (int i = 1; i < args.length; i++) {
                    msg.append(" ").append(args[i]);
                }
                p.performCommand("pm " + lastpm + " " + msg);
            }
        }
        return true;
    }
}
