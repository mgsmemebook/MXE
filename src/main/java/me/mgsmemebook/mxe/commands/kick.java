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

public class kick implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String msg; String kickmsg;
        String name;
        String lang = MXE.getCustomConfig().getString("messages.language");
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String syntaxerror = MXE.getCustomConfig().getString("messages.custom.error.syntax");
        syntaxerror = func.colCodes(syntaxerror);
        String notfounderror = MXE.getCustomConfig().getString("messages.custom.error.target-not-found");
        notfounderror = func.colCodes(notfounderror);
        if(othererror == null || lang == null || permerror == null || syntaxerror == null || notfounderror == null) {
            func.cMSG(ChatColor.RED + "[MXE]: Error: Config misconfigured! Commands won't work!", 1);
            return false;
        }
        if(args.length < 1) {
            switch (lang) {
                case "de":
                    syntaxerror = syntaxerror.replaceAll("%s", "/kick [Spieler] [Grund]");
                    break;
                default:
                    syntaxerror = syntaxerror.replaceAll("%s", "/kick [Player] [Reason]");
            }
            sender.sendMessage(syntaxerror);
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
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE kick]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.kick")) {
                        p.sendMessage(permerror);
                        return true;
                    }
                } else {
                    LuckPerms lp = LuckPermsProvider.get();
                    Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                    if(tg == null) {
                        sender.sendMessage(othererror);
                        return true;
                    }
                    User u = lp.getUserManager().getUser(p.getUniqueId());
                    if (u == null) {
                        p.sendMessage(othererror);
                        return true;
                    }
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.ban").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                    Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                    if (pg == null) {
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
            name = MXE.getPlayerPrefix(p) + p.getDisplayName();
        } else {
            name = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Server";
        }

        if(args.length == 2) {
            String reason = args[1];
            kickmsg = MXE.getCustomConfig().getString("messages.custom.kick.reason.player");
            if(kickmsg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.kick.reason.player)", 2);
            } else {
                kickmsg = func.colCodes(kickmsg);
                kickmsg = kickmsg.replaceAll("%m", name);
                kickmsg = kickmsg.replaceAll("%r", reason);
            }
            msg = MXE.getCustomConfig().getString("messages.custom.kick.reason.staff");
            if(msg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.kick.reason.staff)", 2);
            } else {
                msg = func.colCodes(msg);
                msg = msg.replaceAll("%p", MXE.getPlayerPrefix(t) + t.getDisplayName());
                msg = msg.replaceAll("%r", reason);
            }
        } else {
            kickmsg = MXE.getCustomConfig().getString("messages.custom.kick.no-reason.player");
            if(kickmsg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.kick.reason.player)", 2);
            } else {
                kickmsg = func.colCodes(kickmsg);
                kickmsg = kickmsg.replaceAll("%m", name);
            }
            msg = MXE.getCustomConfig().getString("messages.custom.kick.no-reason.staff");
            if(msg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.kick.reason.staff)", 2);
            } else {
                msg = func.colCodes(msg);
                msg = msg.replaceAll("%p", MXE.getPlayerPrefix(t) + t.getDisplayName());
            }
        }
        if (msg != null) {
            sender.sendMessage(msg);
        }
        t.kickPlayer(kickmsg);
        return true;
    }
}
