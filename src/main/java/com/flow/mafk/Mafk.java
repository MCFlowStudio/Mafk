package com.flow.mafk;

import com.flow.mafk.command.ManageCommand;
import com.flow.mafk.database.CachedDataService;
import com.flow.mafk.database.FileDataService;
import com.flow.mafk.database.StorageDataService;
import com.flow.mafk.database.mysql.DBConnection;
import com.flow.mafk.hook.PlaceHolderHook;
import com.flow.mafk.listener.PlayerListener;
import com.flow.mafk.task.AfkTask;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Mafk extends JavaPlugin {

    private static Mafk instance;
    public static Gson gson = new Gson();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        FileDataService.init();
        // SQL Register
        DBConnection.initialize(FileDataService.SQL_IP, FileDataService.SQL_PORT, FileDataService.SQL_PASSWORD, FileDataService.SQL_USERNAME, FileDataService.SQL_DATABASE, FileDataService.SQL_POOLSIZE);
        StorageDataService.initializeTables();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if ((new PlaceHolderHook()).isRegistered())
                (new PlaceHolderHook()).unregister();
            (new PlaceHolderHook()).register();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!CachedDataService.findUser(player.getUniqueId()).isPresent()) {
                StorageDataService.loadUserData(player.getUniqueId()).thenAccept(data -> {
                    CachedDataService.getUserList().add(data);
                });
            }
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("잠수관리").setExecutor(new ManageCommand());
        new AfkTask().runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (CachedDataService.findUser(player.getUniqueId()).isPresent()) {
                StorageDataService.saveUserDataSync(CachedDataService.findUser(player.getUniqueId()).get());
            }
        }
    }

    public static Mafk getInstance() {
        return instance;
    }

    public static void runSync(Runnable task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTask(instance);
    }

    public static void runAsync(Runnable task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskAsynchronously(getInstance());
    }

}
