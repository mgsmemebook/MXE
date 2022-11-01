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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class unban implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String syntaxerror = MXE.getCustomConfig().getString("messages.custom.error.syntax");
        syntaxerror = func.colCodes(syntaxerror);
        String notfounderror = MXE.getCustomConfig().getString("messages.custom.error.target-not-found");
        notfounderror = func.colCodes(notfounderror);
        String lang = MXE.getCustomConfig().getString("messages.language");
        if(args.length < 1) {
            switch (lang) {
                case "de":
                    syntaxerror.replaceAll("%s", "/unban [Spieler]");
                    break;
                default:
                    syntaxerror.replaceAll("%s", "/unban [Player]");
            }
            sender.sendMessage(syntaxerror);
            return true;
        }
        String tuuid = DB.getPlayerUUID(args[0]);
        if(tuuid == null) {
            sender.sendMessage(notfounderror);
            return true;
        }
        OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(tuuid));
        if(!t.hasPlayedBefore()) {
            sender.sendMessage(notfounderror);
            return true;
        }
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tphere]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }

            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.unban")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.unban").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                    Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                    if (pg == null) {
                        p.sendMessage(othererror);
                        return true;
                    }
                }
            }
        }
        DB.unbanDBPlayer(UUID.fromString(tuuid));
        String msg;
        switch (lang) {
            case "de":
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " entbannt!";
                break;
            default:
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You unbanned " + t.getName() + "!";
        }
        sender.sendMessage(msg);
        return true;
    }
}
