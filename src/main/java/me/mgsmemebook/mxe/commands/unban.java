package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.db.DB;
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

public class unban implements CommandExecutor {
    LuckPerms lp = LuckPermsProvider.get();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        Player p = Bukkit.getPlayerExact(sender.getName());
        if(p == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[tphere]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
            func.cMSG(error);
            return true;
        }
        User u = lp.getUserManager().getUser(p.getUniqueId());
        if(u == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }
        if(!u.getCachedData().getPermissionData().checkPermission("mxe.ban").asBoolean()) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
            p.sendMessage(error);
            return true;
        }
        Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
        if(pg == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }

        if(args.length < 1) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Syntax error: /ban [Spieler] [Zeit/Grund] [Grund]";
            p.sendMessage(error);
            return true;
        }
        Player t = Bukkit.getPlayerExact(args[0]);
        if(t == null) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
            p.sendMessage(error);
            return true;
        }
        Group tg = lp.getGroupManager().getGroup(lp.getUserManager().getUser(t.getUniqueId()).getPrimaryGroup());
        if(tg == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }
        /*if(tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
            p.sendMessage(error);
            return true;
        }*/

        DB.unbanDBPlayer(t.getUniqueId());
        String msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " entbannt!";
        p.sendMessage(msg);
        return true;
    }
}
