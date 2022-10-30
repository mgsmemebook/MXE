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
    LuckPerms lp = LuckPermsProvider.get();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error; String msg;
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tpall]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            User u = lp.getUserManager().getUser(p.getUniqueId());
            if(u == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                p.sendMessage(error);
                return true;
            }
            if(!u.getCachedData().getPermissionData().checkPermission("mxe.vanish").asBoolean()) {
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
            boolean vanish = DB.getVanish(p.getUniqueId());
            for(Player t : Bukkit.getOnlinePlayers()){
                Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                if(tg == null) {
                    continue;
                } else if(!pg.getWeight().isPresent()) {
                    continue;
                }
                if(!tg.getWeight().isPresent() || tg.getWeight().getAsInt() < pg.getWeight().getAsInt()) {
                    if(vanish) {
                        t.showPlayer(MXE.getPlugin(), p);
                    } else {
                        t.hidePlayer(MXE.getPlugin(), p);
                    }
                }
            }
            if(vanish) {
                DB.setVanish(p.getUniqueId(), false);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "Du bist jetzt nicht mehr vanished!";
                p.sendMessage(msg);
                p.removePotionEffect(PotionEffectType.INVISIBILITY);
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave]: " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.AQUA + " ist uns beigetreten!");
            } else {
                DB.setVanish(p.getUniqueId(), true);
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "Du bist jetzt vanished!";
                p.sendMessage(msg);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999999, 2, true, true));
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[join/leave] " + ChatColor.RESET + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.AQUA + " ist Milch holen gegangen!");
            }
        } else {
            error = ChatColor.RED + "[MXE] Das kannst du nur als Spieler!";
            sender.sendMessage(error);
        }
        return true;

    }
}
