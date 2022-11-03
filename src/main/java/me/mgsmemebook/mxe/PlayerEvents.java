package me.mgsmemebook.mxe;

import me.mgsmemebook.mxe.db.DB;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static me.mgsmemebook.mxe.db.DB.*;

public class PlayerEvents implements Listener {
    String lang = MXE.getCustomConfig().getString("messages.language");
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        ArrayList<String> baninf = DB.getPlayerBanInfo(p.getUniqueId());
        if(baninf != null) {
            //Banned
            String reason = null;
            if(baninf.size() == 3) {
                reason = baninf.get(2);
            }
            if(Boolean.parseBoolean(baninf.get(0))) {
                //Tempban
                Calendar cl = Calendar.getInstance();
                Calendar now = Calendar.getInstance();

                long longtime = Long.parseLong(baninf.get(1));

                now.setTime(new Date());
                cl.setTimeInMillis(longtime);

                long timeleft = longtime-now.getTimeInMillis();

                if(timeleft > 0) {
                    LocalDateTime ldt = LocalDateTime.ofInstant(cl.toInstant(), cl.getTimeZone().toZoneId());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                    String unbandate = ldt.format(dtf);

                    String msg;
                    if(reason != null) {
                        msg = MXE.getCustomConfig().getString("messages.custom.ban.banned-join.temporary.reason");
                        if(msg == null) {
                            func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.ban.banned-join.temporary.reason)", 2);
                        } else {
                            msg = func.colCodes(msg);
                            msg = msg.replaceAll("%d", unbandate);
                            msg = msg.replaceAll("%r", reason);
                        }
                    } else {
                        msg = MXE.getCustomConfig().getString("messages.custom.ban.banned-join.temporary.no-reason");
                        if(msg == null) {
                            func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.ban.banned-join.temporary.no-reason)", 2);
                        } else {
                            msg = func.colCodes(msg);
                            msg = msg.replaceAll("%d", unbandate);
                        }
                    }

                    p.kickPlayer(msg);
                    e.setJoinMessage("");
                } else {
                    DB.unbanDBPlayer(p.getUniqueId());
                    playerJoin(p, e);
                }
            } else {
                //Permban
                String msg;
                if(reason != null) {
                    msg = MXE.getCustomConfig().getString("messages.custom.ban.banned-join.permanent.reason");
                    if(msg == null) {
                        func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.ban.banned-join.permanent.reason)", 2);
                    } else {
                        msg = func.colCodes(msg);
                        msg = msg.replaceAll("%r", reason);
                    }
                } else {
                    msg = MXE.getCustomConfig().getString("messages.custom.ban.banned-join.permanent.no-reason");
                    if(msg == null) {
                        func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.ban.banned-join.permanent.no-reason)", 2);
                    } else {
                        msg = func.colCodes(msg);
                    }
                }
                p.kickPlayer(msg);
                e.setJoinMessage("");
            }
        } else {
            //Not Banned
            playerJoin(p, e);
        }
    }
    public void playerJoin(Player p, PlayerJoinEvent e) {
        if (MXE.lpLoaded) {
            LuckPerms lp = LuckPermsProvider.get();
            User u = lp.getUserManager().getUser(p.getUniqueId());
            if (u != null) {
                String group = u.getPrimaryGroup();
                Group g = lp.getGroupManager().getGroup(group);
                if (g != null) {
                    func.updateUser(p, g);
                }
            }

            //Get vanished Players
            ArrayList<String> van = DB.getAllVanished();
            if (van != null) {
                Group pg = lp.getGroupManager().getGroup(u.getPrimaryGroup());
                for (String user : van) {
                    Player v = Bukkit.getPlayerExact(user);
                    Group vg = lp.getGroupManager().getGroup(Objects.requireNonNull(lp.getUserManager().getUser(v.getUniqueId())).getPrimaryGroup());
                    if (pg != null && vg != null) {
                        if (!pg.getWeight().isPresent() || vg.getWeight().isPresent() && vg.getWeight().getAsInt() > pg.getWeight().getAsInt()) {
                            p.hidePlayer(MXE.getPlugin(), v);
                        }
                    } else if (pg == null && vg != null) {
                        p.hidePlayer(MXE.getPlugin(), v);
                    }
                }
            }
            p.setScoreboard(MXE.getPlayerSB());
        } else {
            //Get vanished Players
            ArrayList<String> van = DB.getAllVanished();
            if (van != null) {
                for (String user : van) {
                    Player v = Bukkit.getPlayerExact(user);
                    p.hidePlayer(MXE.getPlugin(), v);
                }
            }
        }

        //Check DB
        String res = getDBPlayer(p.getUniqueId());
        func.cMSG(ChatColor.DARK_AQUA + "[MXE] SQL: Found database entries: " + res, 3);
        if (res == null) {
            func.cMSG(ChatColor.DARK_AQUA + "[MXE] SQL: Player not found - Adding to database", 3);
            addDBPlayer(p.getUniqueId(), p.getName());
        }
        Location loc = getBackCoords(p.getUniqueId());
        if(loc == null) {
            addBackCoords(p.getUniqueId());
        }

        String joinmsg = MXE.getCustomConfig().getString("messages.custom.join");
        if(joinmsg == null) {
            func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.join)", 2);
        } else {
            joinmsg = func.colCodes(joinmsg);
            joinmsg = joinmsg.replaceAll("%p", MXE.getPlayerPrefix(p) + p.getDisplayName());
            e.setJoinMessage(joinmsg);
        }
        if(MXE.getCustomConfig().getBoolean("messages.custom.tablist.enabled")) {
            String header = MXE.getCustomConfig().getString("messages.custom.tablist.header");
            if(header == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.tablist.header)", 2);
            } else {
                header = func.colCodes(header);
                header = header.replaceAll("%u", p.getName());
                header = header.replaceAll("%v", MXE.getPlugin().getDescription().getVersion());
            }
            String footer = MXE.getCustomConfig().getString("messages.custom.tablist.footer");
            if(footer == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.tablist.footer)", 2);
            } else {
                footer = func.colCodes(footer);
                footer = footer.replaceAll("%u", p.getName());
                footer = footer.replaceAll("%v", MXE.getPlugin().getDescription().getVersion());
            }
            p.setPlayerListHeaderFooter(header, footer);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        ArrayList<String> baninf = DB.getPlayerBanInfo(p.getUniqueId());
        if(baninf == null) {
            String quitmsg = MXE.getCustomConfig().getString("messages.custom.quit");
            if(quitmsg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.join)", 2);
            } else {
                quitmsg = func.colCodes(quitmsg);
                quitmsg = quitmsg.replaceAll("%p", MXE.getPlayerPrefix(p) + p.getDisplayName());
                e.setQuitMessage(quitmsg);
            }
            if(MXE.lpLoaded) {
                LuckPerms lp = LuckPermsProvider.get();
                lp.getUserManager().saveUser(Objects.requireNonNull(lp.getUserManager().getUser(p.getUniqueId())));
            }
        } else {
            e.setQuitMessage("");
        }
    }
    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        if(MXE.lpLoaded) {
            LuckPerms lp = LuckPermsProvider.get();
            lp.getUserManager().saveUser(Objects.requireNonNull(lp.getUserManager().getUser(p.getUniqueId())));
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        ArrayList<String> muteinf = DB.getPlayerMuteInfo(p.getUniqueId());
        long mutetime;

        if(muteinf != null) {
            boolean tempmute = Boolean.parseBoolean(muteinf.get(0));
            if(tempmute) {
                String timestring = muteinf.get(1);
                if(timestring == null) {
                    mutetime = 0;
                } else {
                    mutetime = Long.parseLong(timestring);
                }
                Calendar cl = Calendar.getInstance();
                Calendar now = Calendar.getInstance();

                now.setTime(new Date());
                cl.setTimeInMillis(mutetime);

                long timeleft = mutetime-now.getTimeInMillis();

                if(timeleft > 0) {
                    LocalDateTime ldt = LocalDateTime.ofInstant(cl.toInstant(), cl.getTimeZone().toZoneId());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                    String unmutedate = ldt.format(dtf);

                    String mutemsg = MXE.getCustomConfig().getString("messages.custom.mute.muted-chat.temporary");
                    if(mutemsg == null) {
                        func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.join)", 2);
                    } else {
                        mutemsg = func.colCodes(mutemsg);
                        mutemsg = mutemsg.replaceAll("%d", unmutedate);
                        p.sendMessage(mutemsg);
                    }
                } else {
                    DB.setDBPlayerMute(false, false, null, p.getUniqueId());
                }
            } else {
                String mutemsg = MXE.getCustomConfig().getString("messages.custom.mute.muted-chat.permanent");
                if(mutemsg == null) {
                    func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.join)", 2);
                } else {
                    mutemsg = func.colCodes(mutemsg);
                    p.sendMessage(mutemsg);
                }
            }
        } else {
            String msg = e.getMessage();
            msg = func.colCodes(msg);
            String chatmsg = MXE.getCustomConfig().getString("messages.custom.chat");
            if(chatmsg == null) {
                func.cMSG(ChatColor.YELLOW + "[MXE]: Warn: Configuration misconfigured! (messages.custom.join)", 2);
            } else {
                chatmsg = func.colCodes(chatmsg);
                chatmsg = chatmsg.replaceAll("%p", MXE.getPlayerPrefix(p) + p.getDisplayName());
                chatmsg = chatmsg.replaceAll("%m", msg);
                Bukkit.broadcastMessage(chatmsg);
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        Player k = e.getEntity().getKiller();
        String msg = e.getDeathMessage();
        msg = Objects.requireNonNull(msg).replaceAll(Objects.requireNonNull(p).getName(), MXE.getPlayerPrefix(p) + p.getDisplayName()+ChatColor.RESET+ChatColor.GRAY);
        if(k != null) {
            msg = msg.replaceAll(k.getName(), MXE.getPlayerPrefix(k)+k.getDisplayName()+ChatColor.RESET+ChatColor.GRAY);
        }
        msg = ChatColor.GRAY + msg;
        e.setDeathMessage(msg);

        if(DB.getBackCoords(p.getUniqueId()) != null) {
            DB.setBackCoords(p.getUniqueId());
        }
        switch (lang) {
            case "de":
                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Info: Kehre mit " + ChatColor.YELLOW + "/back" + ChatColor.GOLD + " wieder an deinen Totespunkt zurück!";
                break;
            default:
                msg = ChatColor.GOLD + "" + ChatColor.BOLD + "[Server]: " + ChatColor.RESET + ChatColor.GOLD + "Info: Return to your death location with " + ChatColor.YELLOW + "/back" + ChatColor.GOLD + "!";
        }
        p.sendMessage(msg);
    }

    /*
    @EventHandler
    public void onPlayerAchievement(PlayerAdvancementDoneEvent e) {
        Player p = e.getPlayer();
        Advancement advancement = e.getAdvancement();
        String msg = ChatColor.BLUE + MXE.getPlayerPrefix(p) + p.getDisplayName() + ChatColor.RESET + ChatColor.BLUE + "Hat ein achievement erreicht: " + advancement;

    }
    */

}
