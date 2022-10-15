package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gm implements CommandExecutor {
    LuckPerms lp = LuckPermsProvider.get();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        Player p = Bukkit.getPlayerExact(sender.getName());
        if(p == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[gm]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
            func.cMSG(error);
            return true;
        }
        User u = lp.getUserManager().getUser(p.getUniqueId());
        if(u == null) {
            error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
            p.sendMessage(error);
            return true;
        }
        if(!u.getCachedData().getPermissionData().checkPermission("mxe.gm").asBoolean()) {
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

        String msg;
        if(args.length >= 2) {
            Player t = Bukkit.getPlayer(args[1]);
            if(t == null) {
                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
                p.sendMessage(error);
                return true;
            }
            User tu = lp.getUserManager().getUser(t.getUniqueId());
            if(tu == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                p.sendMessage(error);
                return true;
            }
            Group tg = lp.getGroupManager().getGroup(tu.getPrimaryGroup());
            if(tg == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                p.sendMessage(error);
                return true;
            }

            if(tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                p.sendMessage(error);
                return true;
            }
            if(args[0].equalsIgnoreCase("creative") || args[0].equals("1")) {
                t.setGameMode(GameMode.CREATIVE);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " ist bist jetzt im Creative-Mode!";
                p.sendMessage(msg);
                return true;
            } else if(args[0].equalsIgnoreCase("survival") || args[0].equals("0")) {
                t.setGameMode(GameMode.SURVIVAL);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " ist bist jetzt im Survival-Mode!";
                p.sendMessage(msg);
                return true;
            } else if(args[0].equalsIgnoreCase("adventure") || args[0].equals("2")) {
                t.setGameMode(GameMode.ADVENTURE);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " ist jetzt im Adventure-Mode!";
                p.sendMessage(msg);
                return true;
            } else if(args[0].equalsIgnoreCase("spectator") || args[0].equals("3")) {
                t.setGameMode(GameMode.SPECTATOR);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " ist jetzt im Spectator-Mode!";
                p.sendMessage(msg);
                return true;
            } else {
                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Gamemode nicht gefunden!";
                t.sendMessage(error);
                return true;
            }
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("creative") || args[0].equals("1")) {
                p.setGameMode(GameMode.CREATIVE);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Creative-Mode!";
                p.sendMessage(msg);
                return true;
            } else if(args[0].equalsIgnoreCase("survival") || args[0].equals("0")) {
                p.setGameMode(GameMode.SURVIVAL);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Survival-Mode!";
                p.sendMessage(msg);
                return true;
            } else if(args[0].equalsIgnoreCase("adventure") || args[0].equals("2")) {
                p.setGameMode(GameMode.ADVENTURE);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Adventure-Mode!";
                p.sendMessage(msg);
                return true;
            } else if(args[0].equalsIgnoreCase("spectator") || args[0].equals("3")) {
                p.setGameMode(GameMode.SPECTATOR);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Spectator-Mode!";
                p.sendMessage(msg);
                return true;
            } else {
                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Gamemode nicht gefunden!";
                p.sendMessage(error);
                return true;
            }
        } else {
            if (p.getGameMode().equals(GameMode.CREATIVE)) {
                p.setGameMode(GameMode.SURVIVAL);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Survival-Mode!";
            } else {
                p.setGameMode(GameMode.CREATIVE);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Creative-Mode!";
            }
            p.sendMessage(msg);
            return true;
        }
    }
}
