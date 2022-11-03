package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.Nametag;
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

public class unmute implements CommandExecutor {
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
        if(othererror == null || lang == null || permerror == null || syntaxerror == null || notfounderror == null) {
            func.cMSG(ChatColor.RED + "[MXE]: Error: Config misconfigured! Commands won't work!", 1);
            return false;
        }
        if(args.length < 1) {
            switch (lang) {
                case "de":
                    syntaxerror = syntaxerror.replaceAll("%s", "/unmute [Spieler]");
                    break;
                default:
                    syntaxerror = syntaxerror.replaceAll("%s", "/unmute [Player]");
            }
            sender.sendMessage(syntaxerror);
            return true;
        }
        String tuuid = DB.getPlayerUUID(args[0]);
        if(tuuid == null) {
            sender.sendMessage(notfounderror);
            return true;
        }
        if(Bukkit.getPlayer(args[0]) == null && !Nametag.isFakeName(args[0])) {
            sender.sendMessage(notfounderror);
            return true;
        }
        Player t = func.getRealPlayer(args[0]);
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tphere]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(!p.isOp()) {
                if(!MXE.lpLoaded) {
                    if(!p.hasPermission("mxe.unmute")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.unmute").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                    Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                    if (pg == null) {
                        p.sendMessage(othererror);
                        return true;
                    }
                    Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                    if (tg == null) {
                        p.sendMessage(othererror);
                        return true;
                    }
                    if (!pg.getWeight().isPresent()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                    if (tg.getWeight().isPresent() && tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                }
            }
            DB.setDBPlayerMute(false, false, null, t.getUniqueId());
            String msg, unmutemsg;
            switch (lang) {
                case "de":
                    msg = ChatColor.YELLOW + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.YELLOW + "Du hast " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " entmutet!";
                    unmutemsg = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_GREEN + "Du wurdest von " + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.DARK_GREEN + " entmutet!";
                    break;
                default:
                    msg = ChatColor.YELLOW + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You unmuted " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + "!";
                    unmutemsg = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_GREEN + "You have been unmuted by " + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.DARK_GREEN + "!";
            }
            p.sendMessage(msg);
            t.sendMessage(unmutemsg);
        } else {
            DB.setDBPlayerMute(false, false, null, t.getUniqueId());
            String msg, unmutemsg;
            switch (lang) {
                case "de":
                    msg = ChatColor.YELLOW + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.YELLOW + "Du hast " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " entmutet!";
                    unmutemsg = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_GREEN + "Du wurdest von " + ChatColor.DARK_RED + "Server" + ChatColor.RESET + ChatColor.DARK_GREEN + " entmutet!";
                    break;
                default:
                    msg = ChatColor.YELLOW + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You unmuted " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + "!";
                    unmutemsg = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_GREEN + "You have been unmuted by " + ChatColor.DARK_RED + "Server" + ChatColor.RESET + ChatColor.DARK_GREEN + "!";
            }
            sender.sendMessage(msg);
            t.sendMessage(unmutemsg);
        }
        return true;
    }
}
