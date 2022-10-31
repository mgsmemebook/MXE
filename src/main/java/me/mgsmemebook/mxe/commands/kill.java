package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class kill implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        Player t;
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE kill]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }

            if (args.length == 0) {
                p.setHealth(0);
                return true;
            }
            t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
                sender.sendMessage(error);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.kill")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.kill").asBoolean()) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                        p.sendMessage(error);
                        return true;
                    }
                    Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                    if (pg == null) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                        p.sendMessage(error);
                        return true;
                    }
                    Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                    if (tg == null) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                        sender.sendMessage(error);
                        return true;
                    }
                    if (!pg.getWeight().isPresent()) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                        p.sendMessage(error);
                        return true;
                    }
                    if (tg.getWeight().isPresent() && tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                        p.sendMessage(error);
                        return true;
                    }
                }
            }
        } else {
            if(args.length < 1) {
                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
                sender.sendMessage(error);
                return true;
            } else {
                t = Bukkit.getPlayer(args[0]);
                if(t == null) {
                    error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
                    sender.sendMessage(error);
                    return true;
                }
            }
        }
        t.setHealth(0);
        return true;
    }
}
