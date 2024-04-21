package com.flow.mafk.listener;

import com.flow.mafk.database.CachedDataService;
import com.flow.mafk.database.StorageDataService;
import com.flow.mafk.util.object.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!CachedDataService.findUser(player.getUniqueId()).isPresent()) {
            StorageDataService.loadUserData(player.getUniqueId()).thenAccept(data -> {
                CachedDataService.getUserList().add(data);
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (CachedDataService.findUser(player.getUniqueId()).isPresent()) {
            StorageDataService.saveUserData(CachedDataService.findUser(player.getUniqueId()).get());
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        CachedDataService.findUser(player.getUniqueId()).ifPresent(user -> {
            if (user.isAfkState())
                user.setAfkState(false);
        });
    }

    @EventHandler
    public void onDamageDeal(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            CachedDataService.findUser(damager.getUniqueId()).ifPresent(user -> {
                if (user.isAfkState())
                    user.setAfkState(false);
            });
        }
    }

    @EventHandler
    public void onDamageReceive(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            CachedDataService.findUser(victim.getUniqueId()).ifPresent(user -> {
                if (user.isAfkState())
                    user.setAfkState(false);
            });
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CachedDataService.findUser(player.getUniqueId()).ifPresent(user -> {
            if (user.isAfkState())
                user.setAfkState(false);
        });
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        CachedDataService.findUser(player.getUniqueId()).ifPresent(user -> {
            if (user.isAfkState())
                user.setAfkState(false);
        });
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        CachedDataService.findUser(player.getUniqueId()).ifPresent(user -> {
            if (user.isAfkState())
                user.setAfkState(false);
        });
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        CachedDataService.findUser(player.getUniqueId()).ifPresent(user -> {
            if (user.isAfkState())
                user.setAfkState(false);
        });
    }

    @EventHandler
    public void onCommand(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CachedDataService.findUser(player.getUniqueId()).ifPresent(user -> {
            if (user.isAfkState())
                user.setAfkState(false);
        });
    }

}
