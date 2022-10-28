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

public class setrank implements CommandExecutor {
    LuckPerms lp = LuckPermsProvider.get();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        Player p = Bukkit.getPlayerExact(sender.getName());
        if(p == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE setrank]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
            func.cMSG(error);
            return true;
        }
        User u = lp.getUserManager().getUser(p.getUniqueId());
        if(u == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }
        if(!u.getCachedData().getPermissionData().checkPermission("mxe.setrank").asBoolean()) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
            p.sendMessage(error);
            return true;
        }

        if(args.length < 2) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Syntax error: /setrank [Spieler] [Rang]";
            p.sendMessage(error);
            return true;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if(t == null) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
            p.sendMessage(error);
            return true;
        }
        User tu = lp.getUserManager().getUser(t.getUniqueId());
        if(tu == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }
        Group ng = lp.getGroupManager().getGroup(args[1]);
        if(ng == null) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Rang nicht gefunden!";
            p.sendMessage(error);
            return true;
        }
        Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
        if(pg == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }
        Group tg = lp.getGroupManager().getGroup(tu.getPrimaryGroup());
        if(tg == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }

        if(pg.getWeight().isPresent() && ng.getWeight().isPresent() && ng.getWeight().getAsInt() > pg.getWeight().getAsInt()) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
            p.sendMessage(error);
            return true;
        } else if(pg.getWeight().isPresent() && tg.getWeight().isPresent() && tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
            p.sendMessage(error);
            return true;
        }

        func.switchGroup(tu, args[1], tg.getName());
        func.updateUser(t, tg);

        if(tg.getWeight().getAsInt() >= ng.getWeight().getAsInt()) {
            String pmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " zum " + args[1] + " degradiert!";
            String tmsg = ChatColor.BLUE + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.BLUE + " hat dich zum " + args[1] + " degradiert!";
            p.sendMessage(pmsg);
            t.sendMessage(tmsg);
        } else {
            String pmsg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " zum " + args[1] + " befördert!";
            String tmsg = ChatColor.BLUE + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.BLUE + " hat dich zum " + args[1] + " befördert!";
            p.sendMessage(pmsg);
            t.sendMessage(tmsg);
        }
        return true;
    }
}
