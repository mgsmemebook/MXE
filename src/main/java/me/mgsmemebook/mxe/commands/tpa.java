package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.db.DB;
import me.mgsmemebook.mxe.func;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class tpa implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error;
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String syntaxerror = MXE.getCustomConfig().getString("messages.custom.error.syntax");
        syntaxerror = func.colCodes(syntaxerror);
        String notfounderror = MXE.getCustomConfig().getString("messages.custom.error.target-not-found");
        notfounderror = func.colCodes(notfounderror);
        String lang = MXE.getCustomConfig().getString("messages.language");
        if (sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if (p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tpa]: " + ChatColor.RESET + ChatColor.DARK_RED + "Error: Player is null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }

            if (args.length == 0) {
                switch (lang) {
                    case "de":
                        syntaxerror.replaceAll("%s", "/tpa [Spieler]");
                        break;
                    default:
                        syntaxerror.replaceAll("%s", "/tpa [Player]");
                }
                p.sendMessage(syntaxerror);
                return true;
            }

            if(!p.isOp()) {
                if (!MXE.lpLoaded) {
                    if (!p.hasPermission("mxe.tpa")) {
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.tpa").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                }
            }

            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                p.sendMessage(notfounderror);
                return true;
            }

            ArrayList<Integer> tpas = DB.getPlayerTpas(p.getName());
            ArrayList<String> res;

            if (tpas != null) {
                for (int i : tpas) {
                    res = DB.getPlayerTpa(i);
                    if (res == null) continue;

                    Calendar cl = Calendar.getInstance();
                    cl.setTime(new Date());
                    long now = cl.getTimeInMillis();
                    long then = Long.parseLong(Objects.requireNonNull(res).get(2));
                    if (now - then > 600000) {
                        DB.remPlayerTpa(i);
                    } else if (res.get(0).equals(p.getName()) && res.get(1).equals(t.getName())) {
                        switch (lang) {
                            case "de":
                                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Du hast diesem Spieler schon eine ausstehende Anfrage gestellt!";
                                break;
                            default:
                                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "You already sent a request to this player!";
                        }
                        p.sendMessage(error);
                        return true;
                    }
                }
            }
            t.sendMessage(" ");
            String tmsg = ChatColor.GOLD + "--------------------------------------------";
            t.sendMessage(tmsg);
            t.sendMessage(" ");
            TextComponent bas;
            TextComponent bet;
            TextComponent atc;
            TextComponent dtc;
            String msg;
            switch (lang) {
                case "de":
                    tmsg = MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.GOLD + " will sich zu dir teleportieren.";
                    t.sendMessage(tmsg);
                    t.sendMessage(" ");

                    bas = new TextComponent("  ");
                    bet = new TextComponent("  ");
                    atc = new TextComponent("[ Akzeptieren ]");
                    atc.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    atc.setBold(true);
                    dtc = new TextComponent("[  Ablehnen  ]");
                    dtc.setColor(net.md_5.bungee.api.ChatColor.RED);
                    dtc.setBold(true);
                    atc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
                    dtc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
                    t.spigot().sendMessage(bas, atc, bet, dtc);

                    t.sendMessage(" ");
                    tmsg = ChatColor.GOLD + "--------------------------------------------";
                    t.sendMessage(tmsg);
                    t.sendMessage(" ");
                    msg = ChatColor.GOLD +""+ ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Anfrage gesendet!";
                    break;
                default:
                    tmsg = MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.GOLD + " wants to teleport to you.";
                    t.sendMessage(tmsg);
                    t.sendMessage(" ");

                    bas = new TextComponent("  ");
                    bet = new TextComponent("  ");
                    atc = new TextComponent("[ Accept ]");
                    atc.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    atc.setBold(true);
                    dtc = new TextComponent("[  Deny  ]");
                    dtc.setColor(net.md_5.bungee.api.ChatColor.RED);
                    dtc.setBold(true);
                    atc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
                    dtc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
                    t.spigot().sendMessage(bas, atc, bet, dtc);

                    t.sendMessage(" ");
                    tmsg = ChatColor.GOLD + "--------------------------------------------";
                    t.sendMessage(tmsg);
                    t.sendMessage(" ");
                    msg = ChatColor.GOLD +""+ ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Request sent!";
            }

            DB.setPlayerTpa(p.getName(), t.getName(), false);
            p.sendMessage(msg);
        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;
    }
}
