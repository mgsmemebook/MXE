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

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ban implements CommandExecutor {
    LuckPerms lp = LuckPermsProvider.get();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error; String msg;
        Player p = Bukkit.getPlayerExact(sender.getName());
        if(p == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE ban]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
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

        String reason;
        String kickmsg;

        if(args.length < 1) {
            error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Syntax error: /ban [Spieler] [Zeit/Grund] [Grund]";
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

        if(args.length == 1) {
            //Permabann ohne Grund
            DB.banDBPlayer(t.getUniqueId(), false, null, null);

            kickmsg = ChatColor.RED+"Du wurdest " + ChatColor.BOLD + "permanent" + ChatColor.RESET + ChatColor.RED + " von " + p.getDisplayName() + ChatColor.RESET + ChatColor.RED + " gebannt!";
            msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " permanent gebannt!";
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
                String timestamp = cl.getTimeInMillis()+"";
                DB.banDBPlayer(t.getUniqueId(), true, timestamp, null);

                kickmsg = ChatColor.RED+"Du wurdest für " + ChatColor.BOLD + args[1] + ChatColor.RESET + ChatColor.RED + " von " + p.getDisplayName() + ChatColor.RESET + ChatColor.RED + " gebannt!";
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " für " + zeit + " gebannt!";
            } else {
                //Permabann mit Grund
                reason = args[1];
                DB.banDBPlayer(t.getUniqueId(), false, null, reason);

                kickmsg = ChatColor.RED+"Du wurdest " + ChatColor.BOLD + "permanent" + ChatColor.RESET + ChatColor.RED + " von " + p.getDisplayName() + ChatColor.RESET + ChatColor.RED + " gebannt! Grund: " + reason;
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " permanent gebannt! Grund: " + reason;
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
            String timestamp = cl.getTimeInMillis()+"";

            DB.banDBPlayer(t.getUniqueId(), true, timestamp, reason);

            kickmsg = ChatColor.RED+"Du wurdest für " + ChatColor.BOLD + args[1] + ChatColor.RESET + ChatColor.RED + " von " + p.getDisplayName() + ChatColor.RESET + ChatColor.RED + " gebannt! Grund: " + reason;
            msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du hast " + t.getName() + " für " + zeit + " gebannt! Grund: " + reason;
        }
        if(t.isOnline()) {
            t.kickPlayer(kickmsg);
        }
        p.sendMessage(msg);
        return true;
    }
}
