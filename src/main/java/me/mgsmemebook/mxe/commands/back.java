package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.db.DB;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class back implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);

        String lang = MXE.getCustomConfig().getString("messages.language");
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE back]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.back")) {
                        p.sendMessage(permerror);
                        return true;
                    }
                } else {
                    LuckPerms lp = LuckPermsProvider.get();
                    User u = lp.getUserManager().getUser(p.getUniqueId());
                    if (u == null) {
                        p.sendMessage(othererror);
                        return true;
                    }
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.back").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                }
            }

            Location loc = DB.getBackCoords(p.getUniqueId());
            if(loc == null) return true;

            func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.back.tp-time"), loc);
            DB.delBackCoords(p.getUniqueId());

            String msg;
            switch (lang) {
                case "de":
                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Nimm revanche!";
                    break;
                default:
                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Take revenge!";
            }
            p.sendMessage(msg);

        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;
    }
}

