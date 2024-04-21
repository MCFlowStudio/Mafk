package com.flow.mafk.database;

import com.flow.mafk.Mafk;

public class FileDataService {

    public static String SQL_IP;
    public static String SQL_PORT;
    public static String SQL_PASSWORD;
    public static String SQL_USERNAME;
    public static String SQL_DATABASE;
    public static Integer SQL_POOLSIZE;

    public static void init() {
        Mafk.getInstance().getInstance().saveDefaultConfig();
        SQL_IP = Mafk.getInstance().getInstance().getConfig().getString("mysql.ip");
        SQL_PORT = Mafk.getInstance().getInstance().getConfig().getString("mysql.port");
        SQL_PASSWORD = Mafk.getInstance().getInstance().getConfig().getString("mysql.password");
        SQL_USERNAME = Mafk.getInstance().getInstance().getConfig().getString("mysql.username");
        SQL_DATABASE = Mafk.getInstance().getInstance().getConfig().getString("mysql.database");
        SQL_POOLSIZE = Mafk.getInstance().getInstance().getConfig().getInt("mysql.poolsize");
    }

}
