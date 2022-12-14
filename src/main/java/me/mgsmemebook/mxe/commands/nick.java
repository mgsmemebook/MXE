package me.mgsmemebook.mxe.commands;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.Nametag;
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

public class nick implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String error; String msg;
        Player p = Bukkit.getPlayerExact(sender.getName());
        Group pg = null;
        String othererror = MXE.getCustomConfig().getString("messages.custom.error.other");
        othererror = func.colCodes(othererror);
        String permerror = MXE.getCustomConfig().getString("messages.custom.error.unsufficient-permissions");
        permerror = func.colCodes(permerror);
        String lang = MXE.getCustomConfig().getString("messages.language");
        if(othererror == null || lang == null || permerror == null) {
            func.cMSG(ChatColor.RED + "[MXE]: Error: Config misconfigured! Commands won't work!", 1);
            return false;
        }
        if(sender instanceof Player) {
            if(p == null) {
                error = ChatColor.DARK_RED + "" + ChatColor.BOLD + "[MXE nick]: " + ChatColor.RESET + ChatColor.DARK_RED + "Warn: Player is null (" + sender.getName() + ")";
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
                    if (!u.getCachedData().getPermissionData().checkPermission("mxe.nick").asBoolean()) {
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
            String quitmsg;
            if(args.length == 0) {
                quitmsg = MXE.getCustomConfig().getString("messages.custom.quit");
                if(quitmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.quit)", 2);
                } else {
                    quitmsg = func.colCodes(quitmsg);
                    quitmsg = quitmsg.replaceAll("%p", MXE.getPlayerPrefix(p) + p.getDisplayName());
                }

                if(MXE.getPlayerSB().getEntryTeam(p.getDisplayName()) != null) MXE.getPlayerSB().getEntryTeam(p.getDisplayName()).removeEntry(p.getDisplayName());
                if(MXE.lpLoaded) {
                    func.updateUser(p,pg);
                }
                switch (lang) {
                    case "de":
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist wieder \""+ p.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + "\"!";
                        break;
                    default:
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You are \""+ p.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + "\" again!";
                }
                p.sendMessage(msg);
            } else {
                StringBuilder name = new StringBuilder(args[0]);
                for(int i = 1; i < args.length; i++) {
                    name.append("_").append(args[i]);
                }
                if(name.length() > 16) {
                    name = new StringBuilder(name.substring(0, 15));
                }
                if(name.toString().equals(p.getName())) {
                    p.performCommand("nick");
                    return true;
                }

                quitmsg = MXE.getCustomConfig().getString("messages.custom.quit");
                if(quitmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.quit)", 2);
                } else {
                    quitmsg = func.colCodes(quitmsg);
                    quitmsg = quitmsg.replaceAll("%p", MXE.getPlayerPrefix(p) + p.getDisplayName());
                }

                if(MXE.getPlayerSB().getEntryTeam(p.getDisplayName()) != null) MXE.getPlayerSB().getEntryTeam(p.getDisplayName()).removeEntry(p.getDisplayName());
                Nametag.setName(p, name.toString());
                MXE.getPlayerSB().getTeam("default").addEntry(name.toString());
                p.setDisplayName(name.toString());

                String prefix = "";
                if(MXE.lpLoaded) {
                    LuckPerms lp = LuckPermsProvider.get();
                    prefix = Objects.requireNonNull(lp.getGroupManager().getGroup("default")).getCachedData().getMetaData().getPrefix();
                }
                p.setPlayerListName(func.colCodes(prefix) + p.getDisplayName());

                MXE.updatePlayerSB();
                switch (lang) {
                    case "de":
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "Du bist jetzt \""+ p.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + "\"!";
                        break;
                    default:
                        msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.AQUA + "You now are \""+ p.getDisplayName() + ChatColor.RESET + ChatColor.AQUA + "\"!";
                }
                p.sendMessage(msg);
            }
            if(MXE.getCustomConfig().getBoolean("commands.nick.fake-join-leave") && !DB.getVanish(p.getUniqueId()) && quitmsg != null) {
                String joinmsg = MXE.getCustomConfig().getString("messages.custom.join");
                if(joinmsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.join)", 2);
                } else {
                    joinmsg = func.colCodes(joinmsg);
                    joinmsg = joinmsg.replaceAll("%p", MXE.getPlayerPrefix(p) + p.getDisplayName());
                    Bukkit.broadcastMessage(joinmsg);
                }
                Bukkit.broadcastMessage(quitmsg);
            }
        } else {
            error = ChatColor.DARK_RED + "[MXE] You can't perform this command while in console!";
            sender.sendMessage(error);
        }
        return true;
    }
}
