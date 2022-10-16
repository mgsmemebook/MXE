package me.mgsmemebook.mxe.db;
import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.sql.*;
import java.util.UUID;

import static me.mgsmemebook.mxe.db.SQLite.getSQLConnection;

public class DB {
    private static String connurl = "jdbc:h2:" + MXE.getPlDir() + "/data/database";
    public static Connection conn;

    public static void initialize() {
        conn = getSQLConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE UUID = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "SQL error: Feler beim Herstellen der Verbindung");
            func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
        }
    }
    public static void close(PreparedStatement ps, ResultSet rs) {
        try {
            if(ps != null) {
                ps.close();
            } if(rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "SQL error: Fehler beim Schließen der Datenbank-Verbindung");
            func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
        }
    }

    public static void addDBPlayer(String uuid, String username) {
        if(Bukkit.getPlayer(UUID.fromString(uuid)) == null || Bukkit.getPlayer(username) == null) {
            func.cMSG(ChatColor.AQUA + "addPlayer: Spieler " + username + " nicht gefunden.");
        } else {
            PreparedStatement ps = null;
            Connection conn = null;
            try {
                conn = getSQLConnection();
                String sql = "INSERT INTO users (UUID, username) VALUES (?, ?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, uuid);
                ps.setString(2, username);
                ps.execute();
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "SQL error: Fehler beim Schreiben in die Datenbank");
                func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "SQL error: Fehler beim Schließen der Datenbank-Verbindung");
                    func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
                }
            }
        }
    }
    public static String getDBPlayer(String uuid) {
        if(Bukkit.getPlayer(UUID.fromString(uuid)) == null) {
            func.cMSG(ChatColor.AQUA + "getPlayer: Spieler nicht gefunden.");
        } else {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs;
            try {
                conn = getSQLConnection();
                String sql = "SELECT * FROM users WHERE UUID = '"+uuid+"'";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                return rs.getString("username");
            } catch (SQLException ex) {
                func.cMSG(ChatColor.DARK_RED + "SQL error: Fehler beim lesen der Datenbank-Daten");
                func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException ex) {
                    func.cMSG(ChatColor.DARK_RED + "SQL error: Fehler beim Schließen der Datenbank-Verbindung");
                    func.cMSG(ChatColor.DARK_RED + "SQL error: " + ex.getMessage());
                }
            }
        }
        return null;
    }
}
