package com.flow.mafk.util.object;

import com.flow.mafk.Mafk;
import com.flow.mafk.task.AfkTask;
import com.flow.mafk.util.MessageUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class User {

    private UUID uuid;
    private boolean afkState = false;
    private double afkPoint = 0.0D;

    public User(UUID uuid, boolean afkState, double afkPoint) {
        this.uuid = uuid;
        this.afkState = afkState;
        this.afkPoint = afkPoint;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isAfkState() {
        return afkState;
    }

    public void setAfkState(boolean afkState) {
        this.afkState = afkState;
        if (Bukkit.getPlayer(this.uuid) == null)
            return;
        Player player = Bukkit.getPlayer(this.uuid);
        if (afkState) {
            player.sendMessage(MessageUtil.formatMessage("afk-start"));
            List<String> startCommand = Mafk.getInstance().getConfig().getStringList("command.afk-start");
            if (startCommand == null || startCommand.isEmpty())
                return;
            for (String command : startCommand)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command));
        } else {
            AfkTask.resetAfkState(this.uuid);
            player.sendMessage(MessageUtil.formatMessage("afk-end"));
            List<String> endCommand = Mafk.getInstance().getConfig().getStringList("command.afk-end");
            if (endCommand == null || endCommand.isEmpty())
                return;
            for (String command : endCommand)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command));
        }

    }

    public double getAfkPoint() {
        return afkPoint;
    }

    public void addAfkPoint(double afkPoint) {
        this.afkPoint = this.afkPoint + afkPoint;
    }

    public void removeAfkPoint(double afkPoint) {
        this.afkPoint = this.afkPoint - afkPoint;
    }

    public void setAfkPoint(double afkPoint) {
        this.afkPoint = afkPoint;
    }
}
