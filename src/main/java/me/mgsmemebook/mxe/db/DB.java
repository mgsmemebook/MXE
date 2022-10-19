package me.mgsmemebook.mxe.db;

import me.mgsmemebook.mxe.func;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static java.lang.String.valueOf;
import static me.mgsmemebook.mxe.db.SQLite.getSQLConnection;

public class DB {
    public static String getDBVar(String dbname, String wanted, String wherevar, String whereval) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection(dbname);
            String sql = "SELECT ? FROM ? WHERE ? = ?";
            ps = Objects.requireNonNull(conn).prepareStatement(sql);
            ps.setString(1, wanted);
            ps.setString(2, dbname);
            ps.setString(3, wherevar);
            ps.setString(4, whereval);
            rs = ps.executeQuery();
            return rs.getString(wanted);
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            }
        }
        return null;
    }
    public static void setDBPlayerMute(boolean muted, boolean tempmute, String mutetime, UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB addPlayer] Player not found.");
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = getSQLConnection("users");
                String sql = "UPDATE users SET muted = ?, tempmute = ?, mutetime = ? WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setBoolean(1, muted);
                ps.setBoolean(2, tempmute);
                ps.setString(3, mutetime);
                ps.setString(4, uuid.toString());
                ps.execute();
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }

    public static void addDBPlayer(UUID uuid, String username) {
        if(Bukkit.getPlayer(uuid) == null || Bukkit.getPlayer(username) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB addPlayer] Player " + username + " not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "INSERT INTO users (UUID, username, muted, banned) VALUES (?, ?, ?, ?)";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setBoolean(3, false);
                ps.setBoolean(4, false);
                ps.execute();
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't insert into database");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }
    public static String getDBPlayer(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB getPlayer] Player not found.");
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT * FROM users WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setString(1, uuid.toString());
                rs = ps.executeQuery();
                return rs.getString("username");
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }
    public static String getPlayerUUID(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection("users");
            String sql = "SELECT UUID FROM users WHERE username = ?";
            ps = Objects.requireNonNull(conn).prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            return rs.getString("UUID");
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            }
        }
        return null;
    }
    public static void banDBPlayer(UUID uuid, boolean tempban, String bantime, String reason) {
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB addPlayer] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "UPDATE users SET banned = ?, tempban = ?, bantime = ?, reason = ? WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setBoolean(1, true);
                ps.setBoolean(2, tempban);
                ps.setString(3, bantime);
                ps.setString(4, reason);
                ps.setString(5, uuid.toString());
                ps.executeUpdate();
                func.cMSG(ChatColor.RED + "[MXE] Banned player: " + p.getName());
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't update database");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }

    public static void unbanDBPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB unbanDBPlayer] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection("users");
                String sql = "UPDATE users SET banned = ?, tempban = ?, bantime = ?, reason = ? WHERE UUID = ?";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                ps.setBoolean(1, false);
                ps.setBoolean(2, false);
                ps.setString(3, "");
                ps.setString(4, "");
                ps.setString(5, uuid.toString());
                ps.executeUpdate();
                func.cMSG(ChatColor.RED + "[MXE] Unbanned player: " + p.getName());
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't update database");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
    }

    public static ArrayList<String> getPlayerBanInfo(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) {
            func.cMSG(ChatColor.AQUA + "[MXE DB getDBPlayer] Player not found.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            ResultSet rs;
            try {
                conn = getSQLConnection("users");
                String sql = "SELECT * FROM users WHERE UUID = '"+uuid+"'";
                ps = Objects.requireNonNull(conn).prepareStatement(sql);
                rs = ps.executeQuery();
                ArrayList<String> baninf = new ArrayList<>();

                if(rs.getBoolean("banned")) {
                    baninf.add(valueOf(rs.getBoolean("tempban")));
                    baninf.add(rs.getString("bantime"));
                    if(rs.getString("reason") != null) {
                        baninf.add(rs.getString("reason"));
                    }
                    return baninf;
                } else {
                    return null;
                }
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't retrieve table data");
                func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't close database connection");
                    func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }

}
