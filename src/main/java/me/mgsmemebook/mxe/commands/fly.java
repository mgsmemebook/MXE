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
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE fly]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.fly")) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                        p.sendMessage(error);
                        return true;
                    }
                } else {
                    LuckPerms lp = LuckPermsProvider.get();
                    User u = lp.getUserManager().getUser(p.getUniqueId());
                    if (u == null) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                        p.sendMessage(error);
                        return true;
                    }
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.fly").asBoolean()) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                        p.sendMessage(error);
                        return true;
                    }
                }
            }
            float speed;
            if (args.length >= 1) {
                speed = Float.parseFloat(args[0]);
                if(speed > 1) speed = 1;
                if(speed > 0) {
                    if(!p.getAllowFlight()) {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                        p.setFlySpeed(speed);
                        String msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt ein Ah-64 Apache Attack Helicopter!";
                        p.sendMessage(msg);
                    } else {
                        p.setFlySpeed(speed);
                        String msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du fliegst mit einer geschwindigkeit von "+speed+"!";
                        p.sendMessage(msg);
                    }
                }
                else {
                    p.setFlying(false);
                    p.setAllowFlight(false);
                    String msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Snap back to reality";
                    p.sendMessage(msg);
                }
            } else {
                speed = (float) 0.1;
                if(!p.getAllowFlight()) {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.setFlySpeed(speed);
                    String msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt ein Ah-64 Apache Attack Helicopter!";
                    p.sendMessage(msg);
                } else {
                    p.setFlying(false);
                    p.setAllowFlight(false);
                    String msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Snap back to reality";
                    p.sendMessage(msg);
                }
            }
        } else {
            error = ChatColor.RED + "[MXE] Das kannst du nur als Spieler!";
            sender.sendMessage(error);
        }
        return true;
    }
}
