package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class help implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String cmd = "";
        String lang = MXE.getCustomConfig().getString("messages.language");
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        if(othererror == null || lang == null) {
            func.cMSG(ChatColor.RED + "[MXE]: Error: Config misconfigured! Commands won't work!", 1);
            return false;
        }
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE gm]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(args.length >= 1) {
                cmd = args[0].toLowerCase();
                if(!p.isOp()) {
                    if (!MXE.lpLoaded) {
                        if (!p.hasPermission("mxe."+cmd) && !cmd.equalsIgnoreCase("admin")) {
                            p.performCommand("help");
                            return true;
                        }
                        if(cmd.equals("admin")) {
                            p.performCommand("help");
                        }
                    } else {
                        LuckPerms lp = LuckPermsProvider.get();
                        User u = lp.getUserManager().getUser(p.getUniqueId());
                        if (u == null) {
                            p.sendMessage(othererror);
                            return true;
                        }
                        if (!u.getCachedData().getPermissionData().checkPermission("mxe." + cmd).asBoolean() && !cmd.equalsIgnoreCase("admin")) {
                            p.performCommand("help");
                            return true;
                        }
                        if(cmd.equals("admin")) {
                            if (!lp.getGroupManager().getGroup(u.getPrimaryGroup()).getWeight().isPresent()) {
                                p.performCommand("help");
                                return true;
                            } else if (lp.getGroupManager().getGroup(u.getPrimaryGroup()).getWeight().getAsInt() < 5) {
                                p.performCommand("help");
                                return true;
                            }
                        }
                    }
                }
            }
        } else {
            if(args.length >= 1) {
                cmd = args[0].toLowerCase();
            }
        }
        sender.sendMessage("");
        sender.sendMessage("");
        String msg = ChatColor.DARK_AQUA + "---------------- " + ChatColor.BOLD + "MXE-V."+MXE.getPlugin().getDescription().getVersion()+" /help" + ChatColor.RESET + ChatColor.DARK_AQUA + " ----------------";
        sender.sendMessage(msg);
        sender.sendMessage("");
        switch (lang) {
            case "de":
                switch (cmd) {
                    case "admin":
                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("2")) {
                                msg = ChatColor.GREEN + "MXEssentials Admin Befehle Seite 2/4 (/help admin [Seite])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/kick | Kicke einen Spieler vom Server.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/kill | Töte einen Spieler.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/mute | Stumme einen Spieler.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/nick | Ändere deine Identität.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Befehl] für weitere Informationen";
                            } else if (args[1].equalsIgnoreCase("3")) {
                                msg = ChatColor.GREEN + "MXEssentials Admin Befehle Seite 3/4 (/help admin [Seite])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/setrank | Setze den Rang eines Spielers.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpall | Teleportiere alle Spieler zu dir.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tphere | Teleportiere einen Spieler zu dir.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/unban | Vergebe einem Spieler.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Befehl] für weitere Informationen";
                            } else if (args[1].equalsIgnoreCase("4")) {
                                msg = ChatColor.GREEN + "MXEssentials Admin Befehle Seite 4/4 (/help admin [Seite])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/unmute | Entmute einen Spieler.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/van(ish) | Tauche unter.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Befehl] für weitere Informationen";
                            } else {
                                msg = ChatColor.GREEN + "MXEssentials Admin Befehle Seite 1/4 (/help admin [Seite])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/ban | Banne einen Spieler.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/fly | Werde zu einem AH-64 Apache Attack Helicopter.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/gm | Wechsle deinen Gamemode.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/god | Werde zum Gott.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Befehl] für weitere Informationen";
                            }
                        } else {
                            msg = ChatColor.GREEN + "MXEssentials Admin Befehle Seite 1/4 (/help admin [Seite])";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/ban | Banne einen Spieler.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/fly | Werde zu einem AH-64 Apache Attack Helicopter.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/gm | Wechsle deinen Gamemode.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/god | Werde zum Gott.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Befehl] für weitere Informationen";
                        }
                        break;
                    case "back":
                        msg = ChatColor.GREEN + "/back:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Kehre an deinen Todespunkt zurück.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /back";
                        break;
                    case "ban":
                        msg = ChatColor.GREEN + "/ban:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Banne einen Spieler.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /ban [Spieler]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /ban [Spieler] [Grund]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /ban [Spieler] [Zeit (in s/m/h/D/M/Y)]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /ban [Spieler] [Zeit (in s/m/h/D/M/Y)] [Grund]";
                        break;
                    case "fly":
                        msg = ChatColor.GREEN + "/fly:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Werde zu einem AH-64 Apache Attack Helicopter.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /fly";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /fly [Geschwindigkeit]";
                        break;
                    case "gm":
                        msg = ChatColor.GREEN + "/gm:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Wechsle deinen Gamemode.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /gm";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /gm [Modus]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /gm [Modus] [Spieler]";
                        break;
                    case "god":
                        msg = ChatColor.GREEN + "/god:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Werde zum Gott.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /god";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /god [An/Aus]";
                        break;
                    case "help":
                        msg = ChatColor.GREEN + "/help:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Dieser Befehl.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /help";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /help mxe";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /help [Befehl]";
                        break;
                    case "home":
                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("2")) {
                                msg = ChatColor.GREEN + "/home (Seite 2/2):";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Verwalte deine Homes.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home remove [Home]";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home rename [Home] [Neuer Name]";
                            } else {
                                msg = ChatColor.GREEN + "/home (Seite 1/2):";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Verwalte deine Homes.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home [Home]";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home list";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home set [Home]";
                            }
                        } else {
                            msg = ChatColor.GREEN + "/home (Seite 1/2):";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Verwalte deine Homes.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home [Home]";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home list";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home set [Home]";
                        }
                        break;
                    case "kick":
                        msg = ChatColor.GREEN + "/kick:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Kicke einen Spieler vom Server.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /kick [Spieler]";
                        break;
                    case "kill":
                        msg = ChatColor.GREEN + "/kill:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Töte einen Spieler.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /kill [Spieler]";
                        break;
                    case "mute":
                        msg = ChatColor.GREEN + "/mute:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Stumme einen Spieler.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /mute [Spieler]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /mute [Spieler] [Zeit (in s/m/h/D/M/Y)]";
                        break;
                    case "nick":
                        msg = ChatColor.GREEN + "/nick:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Ändere deine Identität.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /nick [Name]";
                        break;
                    case "pm":
                        msg = ChatColor.GREEN + "/pm:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Sende eine Privatnachricht an einen Spieler.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /pm [Spieler]";
                        break;
                    case "reply":
                        msg = ChatColor.GREEN + "/reply:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Sende eine Privatnachricht an einen Spieler.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /reply [Spieler]";
                        break;
                    case "setrank":
                        msg = ChatColor.GREEN + "/setrank:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Setze den Rang eines Spielers.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /setrank [Spieler] [Rang]";
                        break;
                    case "tpa":
                        msg = ChatColor.GREEN + "/tpa:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Teleportiere dich zu deinen Freunden.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpa [Spieler]";
                        break;
                    case "tpaccept":
                        msg = ChatColor.GREEN + "/tpaccept:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Akzeptiere die letzte tp-Anfrage.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpaccept";
                        sender.sendMessage(msg);
                        break;
                    case "tpahere":
                        msg = ChatColor.GREEN + "/tpahere:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Teleportiere einen Freund zu dir.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpahere [Spieler]";
                        break;
                    case "tpall":
                        msg = ChatColor.GREEN + "/tpall:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Teleportiere alle Spieler zu dir.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpall";
                        break;
                    case "tpdeny":
                        msg = ChatColor.GREEN + "/tpdeny:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Lehne die letzte tp-Anfrage ab.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpdeny";
                        break;
                    case "tphere":
                        msg = ChatColor.GREEN + "/tphere:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Teleportiere einen Spieler zu dir.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpahere [Spieler]";
                        break;
                    case "unban":
                        msg = ChatColor.GREEN + "/unban:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Vergebe einem Spieler.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /unban [Spieler]";
                        break;
                    case "unmute":
                        msg = ChatColor.GREEN + "/unmute:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Entmute einen Spieler.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /unmute [Spieler]";
                        break;
                    case "vanish":
                        msg = ChatColor.GREEN + "/vanish:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Tauche unter.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /van(ish)";
                        break;
                    default:
                        if (args.length >= 1) {
                            if (args[0].equalsIgnoreCase("2")) {
                                msg = ChatColor.GREEN + "MXEssentials Befehle Seite 2/2 (/help [Seite])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpa | Teleportiere dich zu deinen Freunden.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpaccept | Akzeptiere die letzte tp-Anfrage.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpahere | Teleportiere einen Freund zu dir.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpdeny | Lehne die letzte tp-Anfrage ab.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Befehl] für weitere Informationen";
                            } else {
                                msg = ChatColor.GREEN + "MXEssentials Befehle Seite 1/2 (/help [Seite])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/back | Kehre an deinen Todespunkt zurück.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help | Dieser Befehl.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/home | Verwalte deine Homes.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/pm | Sende eine Privatnachricht an einen Spieler.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Befehl] für weitere Informationen";
                            }
                        } else {
                            msg = ChatColor.GREEN + "MXEssentials Befehle Seite 1/2 (/help [Seite])";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/back | Kehre an deinen Todespunkt zurück.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/help | Dieser Befehl.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/home | Verwalte deine Homes.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/pm | Sende eine Privatnachricht an einen Spieler.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Befehl] für weitere Informationen";
                        }
                        break;
                }
                break;
            default:
                switch (cmd) {
                    case "admin":
                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("2")) {
                                msg = ChatColor.GREEN + "MXEssentials admin commands page 2/4 (/help admin [Page])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/kick | Kick a player from the server.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/kill | Kill a player.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/mute | Mute a player.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/nick | Disguise yourself.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Command] for further information";
                            } else if (args[1].equalsIgnoreCase("3")) {
                                msg = ChatColor.GREEN + "MXEssentials admin commands page 3/4 (/help admin [Page])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/setrank | Set a player's rank.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpall | Teleport all players to you.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tphere | Teleport a player to you.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/unban | Forgive a player.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Command] for further information";
                            } else if (args[1].equalsIgnoreCase("4")) {
                                msg = ChatColor.GREEN + "MXEssentials admin commands page 4/4 (/help admin [Seite])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/unmute | Unmute a player.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/van(ish) | Disappear.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Command] for further information";
                            } else {
                                msg = ChatColor.GREEN + "MXEssentials admin commands page 1/4 (/help admin [Page])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/ban | Ban a player.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/fly | Become an AH-64 Apache Attack Helicopter.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/gm | Change your gamemode.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/god | Become a god.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Command] for further information";
                            }
                        } else {
                            msg = ChatColor.GREEN + "MXEssentials admin commands page 1/4 (/help admin [Page])";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/ban | Ban a player.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/fly | Become an AH-64 Apache Attack Helicopter.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/gm | Change your gamemode.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/god | Become a god.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Command] for further information";
                        }
                        break;
                    case "back":
                        msg = ChatColor.GREEN + "/back:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Return to your death location.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /back";
                        break;
                    case "ban":
                        msg = ChatColor.GREEN + "/ban:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Ban a player.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /ban [Player]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /ban [Player] [Reason]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /ban [Player] [Time (in s/m/h/D/M/Y)]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /ban [Player] [Time (in s/m/h/D/M/Y)] [Reason]";
                        break;
                    case "fly":
                        msg = ChatColor.GREEN + "/fly:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Become an AH-64 Apache Attack Helicopter.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /fly";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /fly [Speed]";
                        break;
                    case "gm":
                        msg = ChatColor.GREEN + "/gm:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Change your gamemode.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /gm";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /gm [Mode]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /gm [Mode] [Player]";
                        break;
                    case "god":
                        msg = ChatColor.GREEN + "/god:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Become a god.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /god";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /god [On/Off]";
                        break;
                    case "help":
                        msg = ChatColor.GREEN + "/help:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "This command.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /help";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /help mxe";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /help [Command]";
                        break;
                    case "home":
                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("2")) {
                                msg = ChatColor.GREEN + "/home (Page 2/2):";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Configure your homes.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home remove [Home]";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home rename [Home] [New name]";
                            } else {
                                msg = ChatColor.GREEN + "/home (Page 1/2):";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Configure your homes.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home [Home]";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home list";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home set [Home]";
                            }
                        } else {
                            msg = ChatColor.GREEN + "/home (Page 1/2):";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Configure your homes.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home [Home]";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home list";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /home set [Home]";
                        }
                        break;
                    case "kick":
                        msg = ChatColor.GREEN + "/kick:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Kick a player.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /kick [Player]";
                        break;
                    case "kill":
                        msg = ChatColor.GREEN + "/kill:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Kill a player.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /kill [Player]";
                        break;
                    case "mute":
                        msg = ChatColor.GREEN + "/mute:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Mute a player.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /mute [Player]";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /mute [Player] [Time (in s/m/h/D/M/Y)]";
                        break;
                    case "nick":
                        msg = ChatColor.GREEN + "/nick:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Disguise yourself.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /nick [Name]";
                        break;
                    case "pm":
                        msg = ChatColor.GREEN + "/pm:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Send a private message to a player.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /pm [Player]";
                        break;
                    case "reply":
                        msg = ChatColor.GREEN + "/reply:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Send a private message to a player.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /reply [Player]";
                        break;
                    case "setrank":
                        msg = ChatColor.GREEN + "/setrank:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Set a player's rank.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /setrank [Player] [Rank]";
                        break;
                    case "tpa":
                        msg = ChatColor.GREEN + "/tpa:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Teleport yourself to your friends.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpa [Player]";
                        break;
                    case "tpaccept":
                        msg = ChatColor.GREEN + "/tpaccept:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Accept the last tp-request.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpaccept";
                        sender.sendMessage(msg);
                        break;
                    case "tpahere":
                        msg = ChatColor.GREEN + "/tpahere:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Teleport a friend to you.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpahere [Player]";
                        break;
                    case "tpall":
                        msg = ChatColor.GREEN + "/tpall:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Teleport every player to you.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpall";
                        break;
                    case "tpdeny":
                        msg = ChatColor.GREEN + "/tpdeny:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Reject the last tp-request.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpdeny";
                        break;
                    case "tphere":
                        msg = ChatColor.GREEN + "/tphere:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Teleport a player to you.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /tpahere [Player]";
                        break;
                    case "unban":
                        msg = ChatColor.GREEN + "/unban:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Forgive a player.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /unban [Player]";
                        break;
                    case "unmute":
                        msg = ChatColor.GREEN + "/unmute:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Unmute a player.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /unmute [Player]";
                        break;
                    case "vanish":
                        msg = ChatColor.GREEN + "/vanish:";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Disappear.";
                        msg = msg + "\n" + ChatColor.DARK_GREEN + "Syntax: /van(ish)";
                        break;
                    default:
                        if (args.length >= 1) {
                            if (args[0].equalsIgnoreCase("2")) {
                                msg = ChatColor.GREEN + "MXEssentials commands page 2/2 (/help [Page])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpa | Teleport to your friends.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpaccept | Accept the last tp-request.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpahere | Teleport a friend to you.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/tpdeny | Reject the last tp-request.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Command] for further information";
                            } else {
                                msg = ChatColor.GREEN + "MXEssentials commands page 1/2 (/help [Page])";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/back | Return to your death location.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help | This command.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/home | Configure your homes.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/pm | Send a private message to a player.";
                                msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Command] for further information";
                            }
                        } else {
                            msg = ChatColor.GREEN + "MXEssentials commands page 1/2 (/help [Page])";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/back | Return to your death location.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/help | This command.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/home | Configure your homes.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/pm | Send a private message to a player.";
                            msg = msg + "\n" + ChatColor.DARK_GREEN + "/help [Command] for further information";
                        }
                        break;
                }
        }
        sender.sendMessage(msg);
        sender.sendMessage("");
        msg = ChatColor.DARK_AQUA + "------------------- " + ChatColor.BOLD + "MXE-V."+MXE.getPlugin().getDescription().getVersion() + ChatColor.RESET + ChatColor.DARK_AQUA + " -------------------";
        sender.sendMessage(msg);

        return true;
    }
}
