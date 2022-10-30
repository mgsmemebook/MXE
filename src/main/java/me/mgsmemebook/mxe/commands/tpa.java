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
        LuckPerms lp = LuckPermsProvider.get();
        String error;
        if (sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if (p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE tpa]: " + ChatColor.RESET + ChatColor.DARK_RED + "p = null (" + sender.getName() + ")";
                func.cMSG(error);
                return true;
            }

            if (args.length == 0) {
                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Syntax error: /tpa [Spieler]";
                p.sendMessage(error);
                return true;
            }

            User u = lp.getUserManager().getUser(p.getUniqueId());
            if (u == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Ein interner Fehler ist aufgetreten.";
                p.sendMessage(error);
                return true;
            }
            if (!u.getCachedData().getPermissionData().checkPermission("mxe.tpa").asBoolean()) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.DARK_RED + "Dafür hast du keine Rechte!";
                p.sendMessage(error);
                return true;
            }

            Player t = Bukkit.getPlayer(args[0]);
            if (t == null) {
                error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Spieler nicht gefunden!";
                p.sendMessage(error);
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
                        error = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Du hast diesem Spieler schon eine ausstehende Anfrage gestellt!";
                        p.sendMessage(error);
                        return true;
                    }
                }
            }
            t.sendMessage(" ");
            String tmsg = ChatColor.GOLD + "--------------------------------------------";
            t.sendMessage(tmsg);
            t.sendMessage(" ");
            tmsg = MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.GOLD + " will sich zu dir teleportieren.";
            t.sendMessage(tmsg);
            t.sendMessage(" ");

            TextComponent bas = new TextComponent("  ");
            TextComponent bet = new TextComponent("  ");
            TextComponent atc = new TextComponent("[ Akzeptieren ]");
            atc.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            atc.setBold(true);
            TextComponent dtc = new TextComponent("[  Ablehnen  ]");
            dtc.setColor(net.md_5.bungee.api.ChatColor.RED);
            dtc.setBold(true);
            atc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
            dtc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
            t.spigot().sendMessage(bas, atc, bet, dtc);

            t.sendMessage(" ");
            tmsg = ChatColor.GOLD + "--------------------------------------------";
            t.sendMessage(tmsg);
            t.sendMessage(" ");

            DB.setPlayerTpa(p.getName(), t.getName(), false);
            String msg = ChatColor.GOLD +""+ ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Anfrage gesendet!";
            p.sendMessage(msg);
        } else {
            error = ChatColor.RED + "[MXE] Das kannst du nur als Spieler!";
            sender.sendMessage(error);
        }
        return true;
    }
}
