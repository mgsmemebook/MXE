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

public class fly implements CommandExecutor {
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
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE fly]: " + ChatColor.RESET + ChatColor.DARK_RED + "Error: Player is null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.fly")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.fly").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                }
            }
            float speed;
            if (args.length >= 1) {
                speed = Float.parseFloat(args[0]);
                if (speed > 1) speed = 1;
            } else {
                speed = (float) 0.1;
            }
            String onmsg; String offmsg; String spdmsg;
            switch (lang) {
                case "de":
                    onmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt ein Ah-64 Apache Attack Helicopter!";
                    offmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Snap back to reality";
                    spdmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du fliegst mit einer geschwindigkeit von "+speed+"!";
                    break;
                default:
                    onmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You can now identify as an Ah-64 Apache Attack Helicopter!";
                    offmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Snap back to reality";
                    spdmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You are now flying with a speed of "+speed+"!";
            }
            if (args.length >= 1) {
                if(speed > 0) {
                    if(!p.getAllowFlight()) {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                        p.setFlySpeed(speed);
                        p.sendMessage(onmsg);
                    } else {
                        p.setFlySpeed(speed);
                        p.sendMessage(spdmsg);
                    }
                }
                else {
                    p.setFlying(false);
                    p.setAllowFlight(false);
                    p.sendMessage(offmsg);
                }
            } else {
                speed = (float) 0.1;
                if(!p.getAllowFlight()) {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.setFlySpeed(speed);
                    String msg = onmsg;
                    p.sendMessage(msg);
                } else {
                    p.setFlying(false);
                    p.setAllowFlight(false);
                    String msg = offmsg;
                    p.sendMessage(msg);
                }
            }
        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;
    }
}
