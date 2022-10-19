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
    public static String SQLiteCreateTable(String tablename) {
        String sql = null;
        if (tablename.equalsIgnoreCase("users")) {
            sql = "CREATE TABLE IF NOT EXISTS users (" + // make sure to put your table name in here too.
                    "`UUID` varchar(32) NOT NULL," +
                    "`username` varchar(32) NOT NULL," +
                    "PRIMARY KEY (`UUID`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
                    ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.
        } //else if(tablename.equalsIgnoreCase()) { }
        else {
            func.cMSG(ChatColor.RED + "[MXE] Couldn't find db table");
        }
        return sql;
    }

    public static Connection getSQLConnection(String dbname) {
        File dataFolder = new File(MXE.getPlDir(), dbname+".db");
        if(!dataFolder.exists()) {
            try {
                if(!dataFolder.createNewFile()) {
                    func.cMSG(ChatColor.RED + "[MXE] Couldn't create database file");
                }
            } catch (IOException ex) {
                func.cMSG(ChatColor.RED + "File write error " + ex.getMessage());
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
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: Couldn't initialize SQLite");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: SQLite JBDC Library not found. (insert into /plugins/MXE/lib/)");
            func.cMSG(ChatColor.DARK_RED + "[MXE] SQL error: " + ex.getMessage());
        }
        return null;
    }

    public static void load(String dbname) {
        try {
            conn = getSQLConnection(dbname);
            Statement s = Objects.requireNonNull(conn).createStatement();
            s.executeUpdate(SQLiteCreateTable(dbname));

            if(dbname.equals("users")) {
                ArrayList<String> col = new ArrayList<>();
                col.add("ALTER TABLE users ADD COLUMN muted BOOL NOT NULL");
                col.add("ALTER TABLE users ADD COLUMN banned BOOL NOT NULL");
                col.add("ALTER TABLE users ADD COLUMN tempmute BOOL");
                col.add("ALTER TABLE users ADD COLUMN tempban BOOL");
                col.add("ALTER TABLE users ADD COLUMN mutetime varchar(64)");
                col.add("ALTER TABLE users ADD COLUMN bantime varchar(64)");

                for(int i = 0; i < col.size(); i++) {
                    try {
                        s.executeUpdate(col.get(i));
                    } catch(SQLException ex) {
                        func.cMSG(ChatColor.BLACK + "[MXE] SQL warn: Failed to create column " + i + ". Ignore this if said column already exists.");
                    }
                }
                s = Objects.requireNonNull(conn).createStatement();
            }
            s.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
