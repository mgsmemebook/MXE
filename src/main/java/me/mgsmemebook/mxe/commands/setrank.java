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

public class setrank implements CommandExecutor {
    LuckPerms lp = LuckPermsProvider.get();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!MXE.lpLoaded) return true;
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
        if(args.length < 2) {
            switch (lang) {
                case "de":
                    syntaxerror = syntaxerror.replaceAll("%s", "/setrank [Spieler] [Rang]");
                    break;
                default:
                    syntaxerror = syntaxerror.replaceAll("%s", "/setrank [Player] [Rank]");
            }
            sender.sendMessage(syntaxerror);
            return true;
        }
        if(Bukkit.getPlayer(args[0]) == null && !Nametag.isFakeName(args[0])) {
            sender.sendMessage(notfounderror);
            return true;
        }
        Player t = func.getRealPlayer(args[0]);
        User tu = lp.getUserManager().getUser(t.getUniqueId());
        if(tu == null) {
            sender.sendMessage(othererror);
            return true;
        }
        Group ng = lp.getGroupManager().getGroup(args[1]);
        if(ng == null) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Rang nicht gefunden!";
            sender.sendMessage(error);
            return true;
        }
        Group tg = lp.getGroupManager().getGroup(tu.getPrimaryGroup());
        if(tg == null) {
            sender.sendMessage(othererror);
            return true;
        }
        String name;
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE setrank]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            name = MXE.getPlayerPrefix(p) + p.getDisplayName();
            User u = lp.getUserManager().getUser(p.getUniqueId());
            if(u == null) {
                p.sendMessage(othererror);
                return true;
            }
            if(!u.getCachedData().getPermissionData().checkPermission("mxe.setrank").asBoolean()) {
                p.sendMessage(permerror);
                return true;
            }
            Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
            if(pg == null) {
                p.sendMessage(othererror);
                return true;
            }
            if(!pg.getWeight().isPresent()) {
                p.sendMessage(permerror);
                return true;
            }
            if(ng.getWeight().isPresent() && ng.getWeight().getAsInt() > pg.getWeight().getAsInt()) {
                p.sendMessage(permerror);
                return true;
            } else if(tg.getWeight().isPresent() && tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
                p.sendMessage(permerror);
                return true;
            }

            if(tg.getWeight().getAsInt() >= ng.getWeight().getAsInt()) {
                String pmsg = MXE.getCustomConfig().getString("messages.custom.setrank.demote.staff");
                if(pmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.demote.staff)", 2);
                } else {
                    pmsg = func.colCodes(pmsg);
                    pmsg = pmsg.replaceAll("%p", t.getDisplayName());
                    pmsg = pmsg.replaceAll("%r", args[1]);
                    sender.sendMessage(pmsg);
                }
                String tmsg = MXE.getCustomConfig().getString("messages.custom.setrank.demote.player");
                if(tmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.demote.player)", 2);
                } else {
                    tmsg = func.colCodes(tmsg);
                    tmsg = tmsg.replaceAll("%m", name);
                    t.sendMessage(tmsg);
                }
            } else {
                String pmsg = MXE.getCustomConfig().getString("messages.custom.setrank.promote.staff");
                if(pmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.promote.staff)", 2);
                } else {
                    pmsg = func.colCodes(pmsg);
                    pmsg = pmsg.replaceAll("%p", t.getDisplayName());
                    pmsg = pmsg.replaceAll("%r", args[1]);
                    sender.sendMessage(pmsg);
                }
                String tmsg = MXE.getCustomConfig().getString("messages.custom.setrank.promote.player");
                if(tmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.promote.player)", 2);
                } else {
                    tmsg = func.colCodes(tmsg);
                    tmsg = tmsg.replaceAll("%m", name);
                    t.sendMessage(tmsg);
                }
            }
        } else {
            name = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Server";
            if(!tg.getWeight().isPresent()) {
                String pmsg = MXE.getCustomConfig().getString("messages.custom.setrank.promote.staff");
                if(pmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.promote.staff)", 2);
                } else {
                    pmsg = func.colCodes(pmsg);
                    pmsg = pmsg.replaceAll("%p", t.getDisplayName());
                    pmsg = pmsg.replaceAll("%r", args[1]);
                    sender.sendMessage(pmsg);
                }
                String tmsg = MXE.getCustomConfig().getString("messages.custom.setrank.promote.player");
                if(tmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.promote.player)", 2);
                } else {
                    tmsg = func.colCodes(tmsg);
                    tmsg = tmsg.replaceAll("%m", name);
                    t.sendMessage(tmsg);
                }
            } else if(!ng.getWeight().isPresent()) {
                String pmsg = MXE.getCustomConfig().getString("messages.custom.setrank.demote.staff");
                if(pmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.demote.staff)", 2);
                } else {
                    pmsg = func.colCodes(pmsg);
                    pmsg = pmsg.replaceAll("%p", t.getDisplayName());
                    pmsg = pmsg.replaceAll("%r", args[1]);
                    sender.sendMessage(pmsg);
                }
                String tmsg = MXE.getCustomConfig().getString("messages.custom.setrank.demote.player");
                if(tmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.demote.player)", 2);
                } else {
                    tmsg = func.colCodes(tmsg);
                    tmsg = tmsg.replaceAll("%m", name);
                    t.sendMessage(tmsg);
                }
            } else {
                if (tg.getWeight().getAsInt() >= ng.getWeight().getAsInt()) {
                    String pmsg = MXE.getCustomConfig().getString("messages.custom.setrank.demote.staff");
                    if(pmsg == null) {
                        func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.demote.staff)", 2);
                    } else {
                        pmsg = func.colCodes(pmsg);
                        pmsg = pmsg.replaceAll("%p", t.getDisplayName());
                        pmsg = pmsg.replaceAll("%r", args[1]);
                        sender.sendMessage(pmsg);
                    }
                    String tmsg = MXE.getCustomConfig().getString("messages.custom.setrank.demote.player");
                    if(tmsg == null) {
                        func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.demote.player)", 2);
                    } else {
                        tmsg = func.colCodes(tmsg);
                        tmsg = tmsg.replaceAll("%m", name);
                        t.sendMessage(tmsg);
                    }
                } else {
                    String pmsg = MXE.getCustomConfig().getString("messages.custom.setrank.promote.staff");
                    if(pmsg == null) {
                        func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.promote.staff)", 2);
                    } else {
                        pmsg = func.colCodes(pmsg);
                        pmsg = pmsg.replaceAll("%p", t.getDisplayName());
                        pmsg = pmsg.replaceAll("%r", args[1]);
                        sender.sendMessage(pmsg);
                    }
                    String tmsg = MXE.getCustomConfig().getString("messages.custom.setrank.promote.player");
                    if(tmsg == null) {
                        func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.setrank.promote.player)", 2);
                    } else {
                        tmsg = func.colCodes(tmsg);
                        tmsg = tmsg.replaceAll("%m", name);
                        t.sendMessage(tmsg);
                    }
                }
            }
        }
        func.switchGroup(tu, ng.getName(), tg.getName());
        func.updateUser(t, ng);
        return true;
    }
}
