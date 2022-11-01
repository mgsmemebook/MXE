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
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String syntaxerror = MXE.getCustomConfig().getString("messages.custom.error.syntax");
        String lang = MXE.getCustomConfig().getString("messages.language");
        switch (lang) {
            case "de":
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "Du wurdest teleportiert!";
                break;
            default:
                msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_AQUA + "You have been teleported!";
        }
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tpall]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.tpall")) {
                        p.sendMessage(permerror);
                        return true;
                    }
                    for(Player t : Bukkit.getOnlinePlayers()) {
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
                        p.sendMessage(othererror);
                        return true;
                    }
                    Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                    if (pg == null) {
                        p.sendMessage(othererror);
                        return true;
                    }
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.tpall").asBoolean() || !pg.getWeight().isPresent()) {
                        p.sendMessage(permerror);
                        return true;
                    }

                    for(Player t : Bukkit.getOnlinePlayers()) {
                        Group tg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(t.getUniqueId())).getPrimaryGroup());
                        if(tg == null) {
                            continue;
                        }
                        else if(tg.getWeight().isPresent() && tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) continue;

                        if(tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
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
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;
    }
}
