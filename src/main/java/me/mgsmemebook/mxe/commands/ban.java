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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ban implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error; String msg;
        String reason; String kickmsg;
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
        if(args.length < 1) {
            switch (lang) {
                case "de":
                    syntaxerror = syntaxerror.replaceAll("%s", "/ban [Spieler] [Zeit/Grund] [Grund]");
                    break;
                default:
                    syntaxerror = syntaxerror.replaceAll("%s", "/ban [Player] [Time/Reason] [Reason]");
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
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE ban]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if (!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.ban")) {
                        p.sendMessage(permerror);
                        return true;
                    }
                } else {
                    LuckPerms lp = LuckPermsProvider.get();
                    CompletableFuture<User> userFuture = lp.getUserManager().loadUser(t.getUniqueId(), t.getName());
                    User tu = null;
                    try {
                        tu = userFuture.get();
                    } catch (InterruptedException ex) {
                        func.cMSG(ChatColor.GOLD + "[MXE ban] Error while getting offline User", 2);
                        func.cMSG(ChatColor.GOLD + "[MXE ban] " + ex.getMessage(), 2);
                    } catch (ExecutionException ex) {
                        func.cMSG(ChatColor.GOLD + "[MXE ban] Error while getting offline User", 2);
                        func.cMSG(ChatColor.GOLD + "[MXE ban] " + ex.getMessage(), 2);
                    }
                    if(tu == null) {
                        sender.sendMessage(notfounderror);
                        return true;
                    }
                    lp.getGroupManager().loadAllGroups();
                    Group tg = lp.getGroupManager().getGroup(tu.getPrimaryGroup());
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

        if(args.length == 1) {
            //Permabann ohne Grund
            DB.banDBPlayer(t.getUniqueId(), false, null, null);

            kickmsg = MXE.getCustomConfig().getString("messages.custom.ban.banned.permanent.no-reason.player");
            kickmsg = func.colCodes(kickmsg);
            kickmsg = kickmsg.replaceAll("%m", name);
            msg = MXE.getCustomConfig().getString("messages.custom.ban.banned.permanent.no-reason.staff");
            msg = func.colCodes(msg);
            msg = msg.replaceAll("%p", t.getName());
        } else if(args.length == 2) {
            //Tempban ohne Grund oder Permabann mit Grund
            String unit = args[1].substring(args[1].length() - 1);
            String scale = args[1].substring(0, args[1].length() - 1);
            String zeit = scale;

            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());

            if(func.isNumeric(unit)) {
                //Tempbann ohne Grund
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
                DB.banDBPlayer(t.getUniqueId(), true, timestamp, null);

                kickmsg = MXE.getCustomConfig().getString("messages.custom.ban.banned.temporary.no-reason.player");
                kickmsg = func.colCodes(kickmsg);
                kickmsg = kickmsg.replaceAll("%t", zeit);
                kickmsg = kickmsg.replaceAll("%m", name);
                msg = MXE.getCustomConfig().getString("messages.custom.ban.banned.temporary.no-reason.staff");
                msg = func.colCodes(msg);
                msg = msg.replaceAll("%t", zeit);
                msg = msg.replaceAll("%p", t.getName());
            } else {
                //Permabann mit Grund
                reason = args[1];
                DB.banDBPlayer(t.getUniqueId(), false, null, reason);

                kickmsg = MXE.getCustomConfig().getString("messages.custom.ban.banned.permanent.reason.player");
                kickmsg = func.colCodes(kickmsg);
                kickmsg = kickmsg.replaceAll("%r", reason);
                kickmsg = kickmsg.replaceAll("%m", name);
                msg = MXE.getCustomConfig().getString("messages.custom.ban.banned.permanent.reason.staff");
                msg = func.colCodes(msg);
                msg = msg.replaceAll("%r", reason);
                msg = msg.replaceAll("%p", t.getName());
            }
        } else {
            //Tempbann mit Grund
            reason = args[2];

            String unit = args[1].substring(args[1].length() - 1);
            String scale = args[1].substring(0, args[1].length() - 1);
            String zeit = scale;

            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());

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

            DB.banDBPlayer(t.getUniqueId(), true, timestamp, reason);

            kickmsg = MXE.getCustomConfig().getString("messages.custom.ban.banned.temporary.reason.player");
            kickmsg = func.colCodes(kickmsg);
            kickmsg = kickmsg.replaceAll("%t", zeit);
            kickmsg = kickmsg.replaceAll("%m", name);
            kickmsg = kickmsg.replaceAll("%r", reason);
            msg = MXE.getCustomConfig().getString("messages.custom.ban.banned.temporary.reason.staff");
            msg = func.colCodes(msg);
            msg = msg.replaceAll("%t", zeit);
            msg = msg.replaceAll("%p", t.getName());
            msg = msg.replaceAll("%r", reason);
        }
        if(t.isOnline()) {
            Player tplayer = Bukkit.getPlayer(t.getUniqueId());
            if(tplayer != null) {
                tplayer.kickPlayer(kickmsg);
            }
        }
        sender.sendMessage(msg);
        return true;
    }
}
