package me.mgsmemebook.mxe.db;

import me.mgsmemebook.mxe.MXE;
import me.mgsmemebook.mxe.func;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class SQLite {

    public static Connection conn = null;
    public static String SQLiteCreateTable(String tablename, String dbname) {
        String sql = null;
        if (tablename.equalsIgnoreCase("users")) {
            sql = "CREATE TABLE IF NOT EXISTS users (" + // make sure to put your table name in here too.
                    "`UUID` varchar(32) NOT NULL," +
                    "`username` varchar(32) NOT NULL," +
                    "PRIMARY KEY (`UUID`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
                    ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.
        }
        else if (tablename.equalsIgnoreCase("tpa")) {
            sql = "CREATE TABLE IF NOT EXISTS tpa (" +
                    "`id` INTEGER PRIMARY KEY," +
                    "`from` varchar(32) NOT NULL," +
                    "`to` varchar(32) NOT NULL," +
                    "`time` varchar(64) NOT NULL," +
                    "`tpahere` BOOL NOT NULL" +
                    ");";
        }
        else if (tablename.equalsIgnoreCase("homes")) {
            sql = "CREATE TABLE IF NOT EXISTS homes (" +
                    "`UUID` varchar(32) NOT NULL," +
                    "`home` varchar(32)," +
                    "`x` DOUBLE(8,2) NOT NULL," +
                    "`y` DOUBLE(8,2) NOT NULL," +
                    "`z` DOUBLE(8,2) NOT NULL," +
                    "`yaw` DOUBLE(8,2) NOT NULL," +
                    "`world` varchar(32) NOT NULL" +
                    ");";
        }
        else if (tablename.equalsIgnoreCase("back")) {
            sql = "CREATE TABLE IF NOT EXISTS back (" +
                    "`UUID` varchar(32) NOT NULL," +
                    "`x` DOUBLE(8,2) NOT NULL," +
                    "`y` DOUBLE(8,2) NOT NULL," +
                    "`z` DOUBLE(8,2) NOT NULL," +
                    "`yaw` DOUBLE(8,2) NOT NULL," +
                    "`world` varchar(32) NOT NULL," +
                    "PRIMARY KEY (`UUID`)" +
                    ");";
        }
        else {
            func.cMSG(ChatColor.RED + "[MXE] Couldn't find db table " + tablename, 1);
        }
        return sql;
    }

    public static Connection getSQLConnection(String dbname) {
        File dataFolder = new File(MXE.getPlDir()+"/db", dbname+".db");
        if(!dataFolder.exists()) {
            try {
                if(!dataFolder.createNewFile()) {
                    func.cMSG(ChatColor.RED + "[MXE] Couldn't create database file", 1);
                } else {
                    func.cMSG(ChatColor.BLUE + "[MXE] Created new database", 3);
                }
            } catch (IOException ex) {
                func.cMSG(ChatColor.RED + "File write error " + ex.getMessage(), 1);
            }
        }
        try {
            if(conn != null && !conn.isClosed()) {
                return conn;
            }
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return conn;
        } catch (SQLException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't initialize SQLite", 1);
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage(), 1);
        } catch (ClassNotFoundException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: SQLite JBDC Library not found. (insert into /plugins/MXE/lib/)", 1);
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage(), 1);
        }
        return null;
    }

    public static void load(String dbname) {
        try {
            conn = getSQLConnection(dbname);
            Statement s = Objects.requireNonNull(conn).createStatement();

            s.executeUpdate(SQLiteCreateTable("users", "users"));
            s.executeUpdate(SQLiteCreateTable("tpa", "users"));
            s.executeUpdate(SQLiteCreateTable("homes", "users"));
            s.executeUpdate(SQLiteCreateTable("back", "users"));

            ArrayList<String> col = new ArrayList<>();
            col.add("ALTER TABLE users ADD COLUMN muted BOOL NOT NULL");
            col.add("ALTER TABLE users ADD COLUMN banned BOOL NOT NULL");
            col.add("ALTER TABLE users ADD COLUMN tempmute BOOL");
            col.add("ALTER TABLE users ADD COLUMN tempban BOOL");
            col.add("ALTER TABLE users ADD COLUMN mutetime varchar(64)");
            col.add("ALTER TABLE users ADD COLUMN bantime varchar(64)");
            col.add("ALTER TABLE users ADD COLUMN reason varchar(128)");
            col.add("ALTER TABLE users ADD COLUMN vanished BOOL");
            col.add("ALTER TABLE users ADD COLUMN lastpm varchar(32)");

            for(int i = 0; i < col.size(); i++) {
                try {
                    s = Objects.requireNonNull(conn).createStatement();
                    s.executeUpdate(col.get(i));
                } catch(SQLException ex) {
                    func.cMSG(ChatColor.DARK_GRAY + "[MXE] SQL warn: Failed to create column " + i + " of table users. Ignore this if said column already exists.", 3);
                    func.cMSG(ChatColor.DARK_GRAY + "[MXE] SQL warn: " + ex.getMessage(), 3);
                }
            }

            s.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
