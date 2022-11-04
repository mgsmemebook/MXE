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
            p.performCommand("pm "+lastpm);
        }
        return true;
    }
}
