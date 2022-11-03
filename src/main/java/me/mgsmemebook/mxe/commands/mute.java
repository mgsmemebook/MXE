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

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class mute  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error; String msg; String mutemsg;
        String name;
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
                    syntaxerror = syntaxerror.replaceAll("%s", "/mute [Spieler] [Zeit]");
                    break;
                default:
                    syntaxerror = syntaxerror.replaceAll("%s", "/mute [Player] [Time]");
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
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE mute]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.mute")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.mute").asBoolean()) {
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
            name = MXE.getPlayerPrefix(p) + p.getDisplayName();
        } else {
            name = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Server";
        }

        if(args.length == 1) {
            //Permamute
            DB.setDBPlayerMute(true, false, null, t.getUniqueId());

            mutemsg = MXE.getCustomConfig().getString("messages.custom.mute.muted.permanent.player");
            if(mutemsg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.mute.muted.permanent.player)", 2);
            } else {
                mutemsg = func.colCodes(mutemsg);
                mutemsg = mutemsg.replaceAll("%m", name);
            }
            msg = MXE.getCustomConfig().getString("messages.custom.mute.muted.permanent.staff");
            if(msg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.mute.muted.permanent.player)", 2);
            } else {
                msg = func.colCodes(msg);
                msg = msg.replaceAll("%p", MXE.getPlayerPrefix(t) + t.getDisplayName());
            }
        } else {
            //Tempmute
            String unit = args[1].substring(args[1].length() - 1);
            String scale = args[1].substring(0, args[1].length() - 1);
            String zeit = scale;

            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());

            if(func.isNumeric(scale)) {
                //Tempbann
                int time = Integer.parseInt(scale);
                switch (lang) {
                    case "de":
                        switch(unit) {
                            case "s":
                                cl.add(Calendar.SECOND, time);
                                zeit = zeit + " Sekunde";
                                if(time > 1) zeit = zeit + "n";
                                break;
                            case "m":
                                cl.add(Calendar.MINUTE, time);
                                zeit = zeit + " Minute";
                                if(time > 1) zeit = zeit + "n";
                                break;
                            case "h":
                                cl.add(Calendar.HOUR, time);
                                zeit = zeit + " Stunde";
                                if(time > 1) zeit = zeit + "n";
                                break;
                            case "D":
                                cl.add(Calendar.DAY_OF_YEAR, time);
                                zeit = zeit + " Tag";
                                if(time > 1) zeit = zeit + "e";
                                break;
                            case "M":
                                cl.add(Calendar.MONTH, time);
                                zeit = zeit + " Monat";
                                if(time > 1) zeit = zeit + "e";
                                break;
                            case "Y":
                                cl.add(Calendar.YEAR, time);
                                zeit = zeit + " Jahr";
                                if(time > 1) zeit = zeit + "e";
                                break;
                        }
                        break;
                    default:
                        switch(unit) {
                            case "s":
                                cl.add(Calendar.SECOND, time);
                                zeit = zeit + " second";
                                if(time > 1) zeit = zeit + "s";
                                break;
                            case "m":
                                cl.add(Calendar.MINUTE, time);
                                zeit = zeit + " minute";
                                if(time > 1) zeit = zeit + "s";
                                break;
                            case "h":
                                cl.add(Calendar.HOUR, time);
                                zeit = zeit + " hour";
                                if(time > 1) zeit = zeit + "s";
                                break;
                            case "D":
                                cl.add(Calendar.DAY_OF_YEAR, time);
                                zeit = zeit + " day";
                                if(time > 1) zeit = zeit + "s";
                                break;
                            case "M":
                                cl.add(Calendar.MONTH, time);
                                zeit = zeit + " month";
                                if(time > 1) zeit = zeit + "s";
                                break;
                            case "Y":
                                cl.add(Calendar.YEAR, time);
                                zeit = zeit + " year";
                                if(time > 1) zeit = zeit + "s";
                                break;
                        }
                }
                String timestamp = cl.getTimeInMillis()+"";
                DB.setDBPlayerMute(true, true, timestamp, t.getUniqueId());

                mutemsg = MXE.getCustomConfig().getString("messages.custom.mute.muted.temporary.player");
                if(mutemsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.mute.muted.temporary.player)", 2);
                } else {
                    mutemsg = func.colCodes(mutemsg);
                    mutemsg = mutemsg.replaceAll("%m", name);
                    mutemsg = mutemsg.replaceAll("%t", zeit);
                }
                msg = MXE.getCustomConfig().getString("messages.custom.mute.muted.temporary.staff");
                if(msg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.mute.muted.temporary.staff)", 2);
                } else {
                    msg = func.colCodes(msg);
                    msg = msg.replaceAll("%p", MXE.getPlayerPrefix(t) + t.getDisplayName());
                    msg = msg.replaceAll("%t", zeit);
                }
            } else {
                switch (lang) {
                    case "de":
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Invalide Zeit!";
                        break;
                    default:
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Invalid time!";
                }
                sender.sendMessage(error);
                return true;
            }
        }
        sender.sendMessage(msg);
        t.sendMessage(mutemsg);
        return true;
    }
}
