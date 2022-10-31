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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class vanish implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error; String msg;
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String syntaxerror = MXE.getCustomConfig().getString("messages.custom.error.syntax");
        syntaxerror = func.colCodes(syntaxerror);
        String notfounderror = MXE.getCustomConfig().getString("messages.custom.error.target-not-found");
        notfounderror = func.colCodes(notfounderror);
        String lang = MXE.getCustomConfig().getString("messages.language");
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE vanish]: " + ChatColor.RESET + ChatColor.DARK_RED + "Error: Player is null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            boolean vanish = DB.getVanish(p.getUniqueId());
            if(MXE.lpLoaded) {
                LuckPerms lp = LuckPermsProvider.get();
                User u = lp.getUserManager().getUser(p.getUniqueId());
                if (u == null) {
                    p.sendMessage(othererror);
                    return true;
                }
                if (!u.getCachedData().getPermissionData().checkPermission("mxe.vanish").asBoolean()) {
                    p.sendMessage(permerror);
                    return true;
                }
                Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                if (pg == null) {
                    p.sendMessage(othererror);
                    return true;
                }
                for (Player t : Bukkit.getOnlinePlayers()) {
                    Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                    if (tg == null) {
                        continue;
                    } else if (!pg.getWeight().isPresent()) {
                        continue;
                    }
                    if (!tg.getWeight().isPresent() || tg.getWeight().getAsInt() < pg.getWeight().getAsInt()) {
                        if (vanish) {
                            t.showPlayer(MXE.getPlugin(), p);
                        } else {
                            t.hidePlayer(MXE.getPlugin(), p);
                        }
                    }
                }
            } else {
                for (Player t : Bukkit.getOnlinePlayers()) {
                    if (vanish) {
                        t.showPlayer(MXE.getPlugin(), p);
                    } else {
                        t.hidePlayer(MXE.getPlugin(), p);
                    }
                }
            }
            if(vanish) {
                switch (lang) {
                    case "de":
                        DB.setVanish(p.getUniqueId(), false);
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "Du bist jetzt nicht mehr vanished!";
                        p.sendMessage(msg);
                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave]: " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.AQUA + " ist beigetreten!");
                        break;
                    default:
                        DB.setVanish(p.getUniqueId(), false);
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "You are not vanished anymore!";
                        p.sendMessage(msg);
                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave]: " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.AQUA + " joined the server!");
                }
            } else {
                switch (lang) {
                    case "de":
                        DB.setVanish(p.getUniqueId(), true);
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "Du bist jetzt vanished!";
                        p.sendMessage(msg);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999999, 2, true, true));
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.AQUA + " ist Milch holen gegangen!");
                        break;
                    default:
                        DB.setVanish(p.getUniqueId(), true);
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "You are now vanished!";
                        p.sendMessage(msg);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999999, 2, true, true));
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.AQUA + " left the server!");
                }
            }
        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;

    }
}
