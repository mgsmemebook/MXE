package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.Nametag;
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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String msg;
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String notfounderror = MXE.getCustomConfig().getString("messages.custom.error.target-not-found");
        notfounderror = func.colCodes(notfounderror);
        String lang = MXE.getCustomConfig().getString("messages.language");
        if(othererror == null || lang == null || permerror == null || notfounderror == null) {
            func.cMSG(ChatColor.RED + "[MXE]: Error: Config misconfigured! Commands won't work!", 1);
            return false;
        }

        String notfound, p0msg, p1msg, p2msg, p3msg;
        switch (lang) {
            case "de":
                p0msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Survival-Mode!";
                p1msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Creative-Mode!";
                p2msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Adventure-Mode!";
                p3msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt im Spectator-Mode!";
                notfound = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Gamemode nicht gefunden!";
                break;
            default:
                p0msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You are now in survival-mode!";
                p1msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You are now in creative-mode!";
                p2msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You are now in adventure-mode!";
                p3msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You are now in spectator-mode!";
                notfound = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Gamemode not found!";
        }

        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            Group pg = null;
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE gm]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.nick")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.gm").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                    pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                    if (pg == null) {
                        p.sendMessage(othererror);
                        return true;
                    }
                }
            }

            if(args.length >= 2) {
                if(Bukkit.getPlayer(args[1]) == null && !Nametag.isFakeName(args[1])) {
                    sender.sendMessage(notfounderror);
                    return true;
                }
                Player t = func.getRealPlayer(args[1]);

                if(!p.isOp()) {
                    if (!MXE.lpLoaded) {
                        if (!p.hasPermission("mxe.gm")) {
                            p.sendMessage(permerror);
                            return true;
                        }
                    } else {
                        LuckPerms lp = LuckPermsProvider.get();
                        User tu = lp.getUserManager().getUser(t.getUniqueId());
                        if (tu == null) {
                            sender.sendMessage(othererror);
                            return true;
                        }
                        Group tg = lp.getGroupManager().getGroup(tu.getPrimaryGroup());
                        if (tg == null) {
                            sender.sendMessage(othererror);
                            return true;
                        }
                        if (pg.getWeight().isPresent() && tg.getWeight().isPresent() && tg.getWeight().getAsInt() >= pg.getWeight().getAsInt()) {
                            p.sendMessage(permerror);
                            return true;
                        }
                    }
                }
                String t1msg, t0msg, t2msg, t3msg;
                switch (lang) {
                    case "de":
                        t0msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " ist jetzt im Survival-Mode!";
                        t1msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " ist jetzt im Creative-Mode!";
                        t2msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " ist jetzt im Adventure-Mode!";
                        t3msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " ist jetzt im Spectator-Mode!";
                        break;
                    default:
                        t0msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " is now in survival mode!";
                        t1msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " is now in creative mode!";
                        t2msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " is now in adventure mode!";
                        t3msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + MXE.getPlayerPrefix(t) + t.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + " is now in spectator mode!";
                }
                if(args[0].equalsIgnoreCase("creative") || args[0].equals("1")) {
                    t.setGameMode(GameMode.CREATIVE);
                    p.sendMessage(t1msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("survival") || args[0].equals("0")) {
                    t.setGameMode(GameMode.SURVIVAL);
                    p.sendMessage(t0msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("adventure") || args[0].equals("2")) {
                    t.setGameMode(GameMode.ADVENTURE);
                    p.sendMessage(t2msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("spectator") || args[0].equals("3")) {
                    t.setGameMode(GameMode.SPECTATOR);
                    p.sendMessage(t3msg);
                    return true;
                } else {
                    t.sendMessage(notfound);
                    return true;
                }
            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("creative") || args[0].equals("1")) {
                    p.setGameMode(GameMode.CREATIVE);
                    p.sendMessage(p1msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("survival") || args[0].equals("0")) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.sendMessage(p0msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("adventure") || args[0].equals("2")) {
                    p.setGameMode(GameMode.ADVENTURE);
                    p.sendMessage(p2msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("spectator") || args[0].equals("3")) {
                    p.setGameMode(GameMode.SPECTATOR);
                    p.sendMessage(p3msg);
                    return true;
                } else {
                    p.sendMessage(notfound);
                    return true;
                }
            } else {
                if (p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.setGameMode(GameMode.SURVIVAL);
                    msg = p0msg;
                } else {
                    p.setGameMode(GameMode.CREATIVE);
                    msg = p1msg;
                }
                p.sendMessage(msg);
            }
        } else {
            if(args.length < 2) {
                return true;
            } else {
                Player t = Bukkit.getPlayer(args[1]);
                if(t == null) {
                    error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
                    sender.sendMessage(error);
                    return true;
                }
                if(args[0].equalsIgnoreCase("creative") || args[0].equals("1")) {
                    t.setGameMode(GameMode.CREATIVE);
                    sender.sendMessage(p1msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("survival") || args[0].equals("0")) {
                    t.setGameMode(GameMode.SURVIVAL);
                    sender.sendMessage(p0msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("adventure") || args[0].equals("2")) {
                    t.setGameMode(GameMode.ADVENTURE);
                    sender.sendMessage(p2msg);
                    return true;
                } else if(args[0].equalsIgnoreCase("spectator") || args[0].equals("3")) {
                    t.setGameMode(GameMode.SPECTATOR);
                    sender.sendMessage(p3msg);
                    return true;
                } else {
                    t.sendMessage(notfound);
                    return true;
                }
            }
        }

        return true;
    }
}
