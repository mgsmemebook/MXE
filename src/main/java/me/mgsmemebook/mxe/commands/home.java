package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.db.DB;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public class home implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LuckPerms lp = LuckPermsProvider.get();
        String error;
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE home]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            User u = lp.getUserManager().getUser(p.getUniqueId());
            if(u == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                p.sendMessage(error);
                return true;
            }
            if(!u.getCachedData().getPermissionData().checkPermission("mxe.home").asBoolean()) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                p.sendMessage(error);
                return true;
            }

            ArrayList<String> homes = DB.getPlayerHomes(p.getUniqueId());
            String msg;
            if(args.length == 0) {
                if(homes.isEmpty()) {
                    DB.addPlayerHome(p.getUniqueId(), "home", p.getLocation());
                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home gespeichert!";
                    p.sendMessage(msg);
                } else {
                    if(DB.getPlayerHome(p.getUniqueId(), "home") != null) {
                        Location loc = DB.getPlayerHome(p.getUniqueId(), "home");
                        p.teleport(Objects.requireNonNull(loc));
                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home geladen!";
                        p.sendMessage(msg);
                    } else if(DB.getPlayerHome(p.getUniqueId(), homes.get(0)) != null) {
                        Location loc = DB.getPlayerHome(p.getUniqueId(), homes.get(0));
                        p.teleport(Objects.requireNonNull(loc));
                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home " + homes.get(0) + " geladen!";
                        p.sendMessage(msg);
                    } else {
                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Fehler beim Laden deiner Homes.";
                        p.sendMessage(msg);
                    }
                }
            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "-------- Gefundene Homes --------";
                    p.sendMessage(msg);
                    if (homes != null) {
                        for (String home : homes) {
                            msg = ChatColor.YELLOW + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.YELLOW + home;
                            p.sendMessage(msg);
                        }
                        int homesize = homes.size();
                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "--------    " + homesize + "/10 Homes    --------";
                        p.sendMessage(msg);
                    }
                } else {
                    Location loc = DB.getPlayerHome(p.getUniqueId(), args[0]);
                    if(loc == null) {
                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home " + args[0] + " nicht gefunden.";
                        p.sendMessage(msg);
                    } else {
                        p.teleport(loc);
                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[0] + "\" geladen!";
                        p.sendMessage(msg);
                    }
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("remove")) {
                    if(DB.remPlayerHome(p.getUniqueId(), args[1])) {
                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \""+args[1]+"\" gelöscht!";
                        p.sendMessage(msg);
                    } else {
                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home " + args[1] + " nicht löschen.";
                        p.sendMessage(msg);
                    }
                } else if(args[0].equalsIgnoreCase("set")) {
                    if (homes != null) {
                        if(DB.getPlayerHome(p.getUniqueId(), args[1]) != null) {
                            if(DB.changePlayerHome(p.getUniqueId(), args[1], args[1], p.getLocation())) {
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" gesetzt!";
                                p.sendMessage(msg);
                            } else {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home " + args[1] + " nicht setzen.";
                                p.sendMessage(msg);
                            }
                        } else if(homes.size() <= 10) {
                            if(DB.addPlayerHome(p.getUniqueId(), args[1], p.getLocation())) {
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" gesetzt!";
                                p.sendMessage(msg);
                            } else {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home " + args[1] + " nicht setzen.";
                                p.sendMessage(msg);
                            }
                        } else {
                            msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Du darfst nicht mehr als 10 homes haben!";
                            p.sendMessage(msg);
                        }
                    } else {
                        if(DB.addPlayerHome(p.getUniqueId(), args[1], p.getLocation())) {
                            msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" gesetzt!";
                            p.sendMessage(msg);
                        } else {
                            msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home " + args[1] + " nicht setzen.";
                            p.sendMessage(msg);
                        }
                    }
                } else {
                    Location loc = DB.getPlayerHome(p.getUniqueId(), args[0]);
                    if(loc == null) {
                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home " + args[0] + " nicht gefunden.";
                        p.sendMessage(msg);
                    } else {
                        p.teleport(loc);
                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \""+args[0]+"\" geladen!";
                        p.sendMessage(msg);
                    }
                }
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("rename")) {
                    Location home = DB.getPlayerHome(p.getUniqueId(), args[1]);
                    if(home == null) {
                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home " + args[1] + " nicht gefunden.";
                        p.sendMessage(msg);
                    } else {
                        if(DB.changePlayerHome(p.getUniqueId(), args[1], args[2], home)) {
                            msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" in \""+args[2]+"\" umbenannt.";
                            p.sendMessage(msg);
                        } else {
                            msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home " + args[1] + " nicht umbenennen.";
                            p.sendMessage(msg);
                        }
                    }
                }
            } else {
                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Syntax error: /home [name/set/remove/list/rename] [name]";
                p.sendMessage(error);
            }
        } else {
            error = ChatColor.RED + "[MXE] Das kannst du nur als Spieler!";
            sender.sendMessage(error);
        }
        return true;
    }
}
