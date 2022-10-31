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

import java.util.Objects;

public class tpall implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String msg;
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tpall]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.tpall")) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                        p.sendMessage(error);
                        return true;
                    }

                    for(Player t : Bukkit.getOnlinePlayers()) {
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "Du wurdest teleportiert!";
                        t.sendMessage(msg);

                        if(DB.getBackCoords(t.getUniqueId()) == null) {
                            DB.addBackCoords(t.getUniqueId());
                        } else {
                            DB.setBackCoords(t.getUniqueId());
                        }
                        t.teleport(p.getLocation());
                    }
                } else {
                    LuckPerms lp = LuckPermsProvider.get();
                    User u = lp.getUserManager().getUser(p.getUniqueId());
                    if (u == null) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                        p.sendMessage(error);
                        return true;
                    }
                    Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                    if (pg == null) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                        p.sendMessage(error);
                        return true;
                    }
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.tpall").asBoolean() || !pg.getWeight().isPresent()) {
                        error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                        p.sendMessage(error);
                        return true;
                    }

                    for(Player t : Bukkit.getOnlinePlayers()) {
                        Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                        if(tg == null) {
                            continue;
                        }
                        else if(tg.getWeight().isPresent() && tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) continue;

                        if(tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
                            msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "Du wurdest teleportiert!";
                            t.sendMessage(msg);

                            if(DB.getBackCoords(t.getUniqueId()) == null) {
                                DB.addBackCoords(t.getUniqueId());
                            } else {
                                DB.setBackCoords(t.getUniqueId());
                            }
                            t.teleport(p.getLocation());
                        }
                    }
                }
            } else {
                for(Player t : Bukkit.getOnlinePlayers()) {
                    msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "Du wurdest teleportiert!";
                    t.sendMessage(msg);

                    if(DB.getBackCoords(t.getUniqueId()) == null) {
                        DB.addBackCoords(t.getUniqueId());
                    } else {
                        DB.setBackCoords(t.getUniqueId());
                    }
                    t.teleport(p.getLocation());
                }
            }
        } else {
            error = ChatColor.RED + "[MXE] Das kannst du nur als Spieler!";
            sender.sendMessage(error);
        }
        return true;
    }
}
