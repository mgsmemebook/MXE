package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.Nametag;
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

public class nick implements CommandExecutor {
    LuckPerms lp = LuckPermsProvider.get();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error; String msg;
        Player p = Bukkit.getPlayerExact(sender.getName());
        if(sender instanceof Player) {
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE nick]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            User u = lp.getUserManager().getUser(p.getUniqueId());
            if(u == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                p.sendMessage(error);
                return true;
            }
            if(!u.getCachedData().getPermissionData().checkPermission("mxe.nick").asBoolean()) {
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
            if(args.length == 0) {
                if(MXE.getPlayerSB().getEntryTeam(p.getDisplayName()) != null) MXE.getPlayerSB().getEntryTeam(p.getDisplayName()).removeEntry(p.getDisplayName());
                func.updateUser(p,pg);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist wieder \""+ p.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + "\"!";
                p.sendMessage(msg);
            } else {
                if(args[0].equals(p.getName())) {
                    p.performCommand("nick");
                    return true;
                }
                if(MXE.getPlayerSB().getEntryTeam(p.getDisplayName()) != null) MXE.getPlayerSB().getEntryTeam(p.getDisplayName()).removeEntry(p.getDisplayName());
                Nametag.setName(p, args[0]);
                MXE.getPlayerSB().getTeam("default").addEntry(args[0]);
                p.setDisplayName(args[0]);
                String prefix = Objects.requireNonNull(lp.getGroupManager().getGroup("default")).getCachedData().getMetaData().getPrefix();
                p.setPlayerListName(func.colCodes(prefix) + p.getDisplayName());

                MXE.updatePlayerSB();
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt \""+ p.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + "\"!";
                p.sendMessage(msg);
            }
        } else {
            error = ChatColor.RED + "[MXE] Das kannst du nur als Spieler!";
            sender.sendMessage(error);
        }
        return true;
    }
}
