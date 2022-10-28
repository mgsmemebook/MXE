package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
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

import java.util.Objects;
import java.util.UUID;

public class unmute implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LuckPerms lp = LuckPermsProvider.get();
        String error;
        Player p = Bukkit.getPlayerExact(sender.getName());
        if(p == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE unmute]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
            func.cMSG(error);
            return true;
        }
        User u = lp.getUserManager().getUser(p.getUniqueId());
        if(u == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }
        if(!u.getCachedData().getPermissionData().checkPermission("mxe.unmute").asBoolean()) {
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
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Syntax error: /unmute [Spieler]";
            p.sendMessage(error);
            return true;
        }
        String tuuid = DB.getPlayerUUID(args[0]);
        if(tuuid == null) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
            p.sendMessage(error);
            return true;
        }
        Player t = (Player) Bukkit.getOfflinePlayer(UUID.fromString(tuuid));
        Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
        if(tg == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }
        if(pg.getWeight().isPresent() && tg.getWeight().isPresent() && tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
            p.sendMessage(error);
            return true;
        }

        DB.setDBPlayerMute(false, false, null, t.getUniqueId());
        String msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " entmutet!";
        String unmutemsg = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GREEN + "Du wurdest von " + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.GREEN + " entmutet!";
        p.sendMessage(msg);
        t.sendMessage(unmutemsg);
        return true;
    }
}
