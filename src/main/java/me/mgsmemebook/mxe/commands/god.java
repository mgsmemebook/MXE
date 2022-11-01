package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class god implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String lang = MXE.getCustomConfig().getString("messages.language");
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE god]: " + ChatColor.RESET + ChatColor.DARK_RED + "Error: Warn is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.god")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.god").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                }
            }

            String offmsg; String onmsg; String msg;
            switch (lang) {
                case "de":
                    offmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Godmode aus.";
                    onmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Godmode an.";
                    break;
                default:
                    offmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Godmode off.";
                    onmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Godmode on.";
            }
            if(args.length == 0) {
                if(p.isInvulnerable()) {
                    p.setInvulnerable(false);
                    msg = offmsg;
                } else {
                    p.setInvulnerable(true);
                    msg = onmsg;
                }
            } else {
                if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("aus")) {
                    p.setInvulnerable(false);
                    msg = offmsg;
                } else {
                    p.setInvulnerable(true);
                    msg = onmsg;
                }
            }
            p.sendMessage(msg);
        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;
    }
}
