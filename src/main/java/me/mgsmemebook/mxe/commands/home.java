package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
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
        String error;
        String lang = MXE.getCustomConfig().getString("messages.language");
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String syntaxerror = MXE.getCustomConfig().getString("messages.custom.error.syntax");
        syntaxerror = func.colCodes(syntaxerror);
        Integer maxhomes = MXE.getCustomConfig().getInt("commands.home.max-homes.default");
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE home]: " + ChatColor.RESET + ChatColor.DARK_RED + "Error: Player is null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.home")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.home").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                    if(MXE.getCustomConfig().getInt("commands.home.max-homes."+u.getPrimaryGroup()) != 0) {
                        maxhomes = MXE.getCustomConfig().getInt("commands.home.max-homes."+u.getPrimaryGroup());
                    }
                }
            }

            ArrayList<String> homes = DB.getPlayerHomes(p.getUniqueId());
            String msg;
            switch (lang) {
                case "de":
                    if(args.length == 0) {
                        if(homes == null || homes.isEmpty()) {
                            DB.addPlayerHome(p.getUniqueId(), "home", p.getLocation());
                            msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home gespeichert!";
                            p.sendMessage(msg);
                        } else {
                            if(DB.getPlayerHome(p.getUniqueId(), "home") != null) {
                                Location loc = DB.getPlayerHome(p.getUniqueId(), "home");
                                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.home.tp-time"), loc);
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home geladen!";
                                p.sendMessage(msg);
                            } else if(DB.getPlayerHome(p.getUniqueId(), homes.get(0)) != null) {
                                Location loc = DB.getPlayerHome(p.getUniqueId(), homes.get(0));
                                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.home.tp-time"), loc);
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + homes.get(0) + "\" geladen!";
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
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "--------    " + homesize + "/"+maxhomes+" Homes    --------";
                                p.sendMessage(msg);
                            }
                        } else {
                            Location loc = DB.getPlayerHome(p.getUniqueId(), args[0]);
                            if(loc == null) {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home \"" + args[0] + "\" nicht gefunden.";
                                p.sendMessage(msg);
                            } else {
                                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.home.tp-time"), loc);
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
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home \"" + args[1] + "\" nicht löschen.";
                                p.sendMessage(msg);
                            }
                        } else if(args[0].equalsIgnoreCase("set")) {
                            if (homes != null) {
                                if(DB.getPlayerHome(p.getUniqueId(), args[1]) != null) {
                                    if(DB.changePlayerHome(p.getUniqueId(), args[1], args[1], p.getLocation())) {
                                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" gesetzt!";
                                        p.sendMessage(msg);
                                    } else {
                                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home \"" + args[1] + "\" nicht setzen.";
                                        p.sendMessage(msg);
                                    }
                                } else if(homes.size() <= maxhomes) {
                                    if(DB.addPlayerHome(p.getUniqueId(), args[1], p.getLocation())) {
                                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" gesetzt!";
                                        p.sendMessage(msg);
                                    } else {
                                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home \"" + args[1] + "\" nicht setzen.";
                                        p.sendMessage(msg);
                                    }
                                } else {
                                    msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Du darfst nicht mehr als "+maxhomes+" homes haben!";
                                    p.sendMessage(msg);
                                }
                            } else {
                                if(DB.addPlayerHome(p.getUniqueId(), args[1], p.getLocation())) {
                                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" gesetzt!";
                                    p.sendMessage(msg);
                                } else {
                                    msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home \"" + args[1] + "\" nicht setzen.";
                                    p.sendMessage(msg);
                                }
                            }
                        } else {
                            Location loc = DB.getPlayerHome(p.getUniqueId(), args[0]);
                            if(loc == null) {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home \"" + args[0] + "\" nicht gefunden.";
                                p.sendMessage(msg);
                            } else {
                                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.home.tp-time"), loc);
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \""+args[0]+"\" geladen!";
                                p.sendMessage(msg);
                            }
                        }
                    } else if(args.length == 3) {
                        if(args[0].equalsIgnoreCase("rename")) {
                            Location home = DB.getPlayerHome(p.getUniqueId(), args[1]);
                            if(home == null) {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home \"" + args[1] + "\" nicht gefunden.";
                                p.sendMessage(msg);
                            } else {
                                if(DB.changePlayerHome(p.getUniqueId(), args[1], args[2], home)) {
                                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" in \""+args[2]+"\" umbenannt.";
                                    p.sendMessage(msg);
                                } else {
                                    msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Konnte Home \"" + args[1] + "\" nicht umbenennen.";
                                    p.sendMessage(msg);
                                }
                            }
                        }
                    } else {
                        syntaxerror = syntaxerror.replaceAll("%s","/home [Name/Set/Remove/List/Rename] [Name]");
                        p.sendMessage(syntaxerror);
                    }
                    break;
                default:
                    if(args.length == 0) {
                        if(homes == null || homes.isEmpty()) {
                            DB.addPlayerHome(p.getUniqueId(), "home", p.getLocation());
                            msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home saved!";
                            p.sendMessage(msg);
                        } else {
                            if(DB.getPlayerHome(p.getUniqueId(), "home") != null) {
                                Location loc = DB.getPlayerHome(p.getUniqueId(), "home");
                                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.home.tp-time"), loc);
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home loaded!";
                                p.sendMessage(msg);
                            } else if(DB.getPlayerHome(p.getUniqueId(), homes.get(0)) != null) {
                                Location loc = DB.getPlayerHome(p.getUniqueId(), homes.get(0));
                                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.home.tp-time"), loc);
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + homes.get(0) + "\" loaded!";
                                p.sendMessage(msg);
                            } else {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Error while loading player homes.";
                                p.sendMessage(msg);
                            }
                        }
                    } else if(args.length == 1) {
                        if(args[0].equalsIgnoreCase("list")) {
                            msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "---------- Homes found ----------";
                            p.sendMessage(msg);
                            if (homes != null) {
                                for (String home : homes) {
                                    msg = ChatColor.YELLOW + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.YELLOW + home;
                                    p.sendMessage(msg);
                                }
                                int homesize = homes.size();
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "--------    " + homesize + "/"+maxhomes+" homes    --------";
                                p.sendMessage(msg);
                            }
                        } else {
                            Location loc = DB.getPlayerHome(p.getUniqueId(), args[0]);
                            if(loc == null) {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home \"" + args[0] + "\" not found.";
                                p.sendMessage(msg);
                            } else {
                                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.home.tp-time"), loc);
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[0] + "\" loaded!";
                                p.sendMessage(msg);
                            }
                        }
                    } else if(args.length == 2) {
                        if(args[0].equalsIgnoreCase("remove")) {
                            if(DB.remPlayerHome(p.getUniqueId(), args[1])) {
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \""+args[1]+"\" removed!";
                                p.sendMessage(msg);
                            } else {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Couldn't remove home \"" + args[1] + "\".";
                                p.sendMessage(msg);
                            }
                        } else if(args[0].equalsIgnoreCase("set")) {
                            if (homes != null) {
                                if(DB.getPlayerHome(p.getUniqueId(), args[1]) != null) {
                                    if(DB.changePlayerHome(p.getUniqueId(), args[1], args[1], p.getLocation())) {
                                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" set!";
                                        p.sendMessage(msg);
                                    } else {
                                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Couldn't set home \"" + args[1] + "\".";
                                        p.sendMessage(msg);
                                    }
                                } else if(homes.size() <= maxhomes) {
                                    if(DB.addPlayerHome(p.getUniqueId(), args[1], p.getLocation())) {
                                        msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" set!";
                                        p.sendMessage(msg);
                                    } else {
                                        msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Couldn't set home \"" + args[1] + "\".";
                                        p.sendMessage(msg);
                                    }
                                } else {
                                    msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "You may not have more than "+maxhomes+" homes!";
                                    p.sendMessage(msg);
                                }
                            } else {
                                if(DB.addPlayerHome(p.getUniqueId(), args[1], p.getLocation())) {
                                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \"" + args[1] + "\" set!";
                                    p.sendMessage(msg);
                                } else {
                                    msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Couldn't set home \"" + args[1] + "\".";
                                    p.sendMessage(msg);
                                }
                            }
                        } else {
                            Location loc = DB.getPlayerHome(p.getUniqueId(), args[0]);
                            if(loc == null) {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home \"" + args[0] + "\" not found.";
                                p.sendMessage(msg);
                            } else {
                                func.teleportDelay(p, MXE.getCustomConfig().getLong("commands.home.tp-time"), loc);
                                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Home \""+args[0]+"\" loaded!";
                                p.sendMessage(msg);
                            }
                        }
                    } else if(args.length == 3) {
                        if(args[0].equalsIgnoreCase("rename")) {
                            Location home = DB.getPlayerHome(p.getUniqueId(), args[1]);
                            if(home == null) {
                                msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Home \"" + args[0] + "\" not found.";
                                p.sendMessage(msg);
                            } else {
                                if(DB.changePlayerHome(p.getUniqueId(), args[1], args[2], home)) {
                                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Renamed home \"" + args[1] + "\" to \""+args[2]+"\".";
                                    p.sendMessage(msg);
                                } else {
                                    msg = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Couldn't rename home \"" + args[1] + "\".";
                                    p.sendMessage(msg);
                                }
                            }
                        }
                    } else {
                        syntaxerror = syntaxerror.replaceAll("%s","/home [Name/Set/Remove/List/Rename] [Name]");
                        p.sendMessage(syntaxerror);
                    }
            }
        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;
    }
}
