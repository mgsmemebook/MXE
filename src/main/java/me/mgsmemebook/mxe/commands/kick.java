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

public class kick implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String msg; String kickmsg;
        String name;
        if(args.length < 1) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Syntax error: /kick [Spieler] [Grund]";
            sender.sendMessage(error);
            return true;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if(t == null) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
            sender.sendMessage(error);
            return true;
        }
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE kick]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.kick")) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                        p.sendMessage(error);
                        return true;
                    }
                } else {
                    LuckPerms lp = LuckPermsProvider.get();
                    Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                    if(tg == null) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                        sender.sendMessage(error);
                        return true;
                    }
                    User u = lp.getUserManager().getUser(p.getUniqueId());
                    if (u == null) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                        p.sendMessage(error);
                        return true;
                    }
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.ban").asBoolean()) {
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
            name = MXE.getPlayerPrefix(p) + p.getDisplayName();
        } else {
            name = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Konsole";
        }

        if(args.length == 2) {
            String reason = args[1];
            kickmsg = ChatColor.RED+"Du wurdest von " + name + ChatColor.RESET + ChatColor.RED + " gekickt! Grund: " + reason;
            msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " gekickkt! Grund: " + reason;
        } else {
            kickmsg = ChatColor.RED+"Du wurdest von " + name + ChatColor.RESET + ChatColor.RED + " gekickt!";
            msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " gekickkt!";
        }
        sender.sendMessage(msg);
        t.kickPlayer(kickmsg);
        return true;
    }
}
