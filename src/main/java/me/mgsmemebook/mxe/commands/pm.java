package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.Nametag;
import me.mgsmemebook.mxe.db.DB;
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

public class pm implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name;
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String syntaxerror = MXE.getCustomConfig().getString("messages.custom.error.syntax");
        syntaxerror = func.colCodes(syntaxerror);
        String notfounderror = MXE.getCustomConfig().getString("messages.custom.error.target-not-found");
        notfounderror = func.colCodes(notfounderror);
        String lang = MXE.getCustomConfig().getString("messages.language");
        if(othererror == null || lang == null || permerror == null || syntaxerror == null || notfounderror == null) {
            func.cMSG(ChatColor.RED + "[MXE]: Error: Config misconfigured! Commands won't work!", 1);
            return false;
        }
        if(args.length < 2) {
            switch (lang) {
                case "de":
                    syntaxerror = syntaxerror.replaceAll("%s","/pm [Spieler] [Nachricht]");
                    break;
                default:
                    syntaxerror = syntaxerror.replaceAll("%s","/pm [Player] [Message]");
            }
            sender.sendMessage(syntaxerror);
            return true;
        }
        if(Bukkit.getPlayer(args[0]) == null && !Nametag.isFakeName(args[1])) {
            sender.sendMessage(notfounderror);
            return true;
        }
        Player t = func.getRealPlayer(args[0]);
        if(sender instanceof Player) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            if(p == null) {
                String error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE setrank]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
                func.cMSG(error, 2);
                return true;
            }
            if(!func.playerMuteCheck(p)) return true;

            name = MXE.getPlayerPrefix(p) + p.getDisplayName();
            if(!p.isOp()) {
                if(MXE.lpLoaded) {
                    LuckPerms lp = LuckPermsProvider.get();
                    User u = lp.getUserManager().getUser(p.getUniqueId());
                    if (u == null) {
                        p.sendMessage(othererror);
                        return true;
                    }
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.pm").asBoolean()) {
                        p.sendMessage(permerror);
                        return true;
                    }
                } else if(!p.hasPermission("mxe.pm")) {
                    p.sendMessage(permerror);
                    return true;
                }
            }
            DB.setLastPm(t.getUniqueId(), p.getName());
        } else {
            name = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Server";
        }
        StringBuilder msg = new StringBuilder(args[1]);
        for(int i = 2; i < args.length; i++) {
            msg.append(" ").append(args[i]);
        }
        String pm = MXE.getCustomConfig().getString("messages.custom.pm");
        if(pm == null) {
            func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.pm)", 2);
        } else {
            pm = func.colCodes(pm);
            pm = pm.replaceAll("%s", name);
            pm = pm.replaceAll("%r", MXE.getPlayerPrefix(t) + t.getDisplayName());
            pm = pm.replaceAll("%m", msg.toString());
        }

        if(pm != null) {
            t.sendMessage(pm);
            sender.sendMessage(pm);
        }
        return true;
    }
}
