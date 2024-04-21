package com.flow.mafk.task;

import com.flow.mafk.Mafk;
import com.flow.mafk.database.CachedDataService;
import com.flow.mafk.util.MessageUtil;
import com.flow.mafk.util.object.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AfkTask extends BukkitRunnable {
    private final Map<UUID, Location> lastLocations = new HashMap<>();
    private static final Map<UUID, Long> afkStartTimes = new HashMap<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            Location currentLocation = player.getLocation().clone();

            // 움직임 감지 및 잠수 상태 처리
            if (lastLocations.containsKey(playerId) && isSameLocation(currentLocation, lastLocations.get(playerId))) {
                // 움직임이 없는 경우
                long lastMoveTime = afkStartTimes.getOrDefault(playerId, System.currentTimeMillis());
                long elapsed = System.currentTimeMillis() - lastMoveTime;
                if (elapsed >= Mafk.getInstance().getConfig().getInt("setting.afk-period", 60000)) {  // 움직임이 없는 지 60초가 지났는지 확인
                    setAfkState(playerId);
                }
            } else {
                // 움직임이 있으면 타이머 리셋
                afkStartTimes.put(playerId, System.currentTimeMillis());
            }

            // 위치 업데이트
            lastLocations.put(playerId, currentLocation);
        }
    }

    private boolean isSameLocation(Location current, Location last) {
        return current.getBlockX() == last.getBlockX() &&
                current.getBlockY() == last.getBlockY() &&
                current.getBlockZ() == last.getBlockZ();
    }

    private void setAfkState(UUID playerId) {
        Optional<User> userOpt = CachedDataService.findUser(playerId);
        userOpt.ifPresent(user -> {
            if (!user.isAfkState()) {
                user.setAfkState(true);
            }
            long lastPointTime = afkStartTimes.getOrDefault(playerId, System.currentTimeMillis());
            long elapsedSincePoint = System.currentTimeMillis() - lastPointTime;
            if (elapsedSincePoint >= Mafk.getInstance().getConfig().getInt("setting.point-period", 30000)) {  // 30초마다 포인트 부여
                user.addAfkPoint(Mafk.getInstance().getConfig().getInt("setting.auto-give-point", 3));
                if (Bukkit.getPlayer(playerId) != null)
                    Bukkit.getPlayer(playerId).sendMessage(MessageUtil.formatMessage("receive-point", MessageUtil.formatNumber(user.getAfkPoint())));
                afkStartTimes.put(playerId, System.currentTimeMillis());
            }
        });
    }

    public static void resetAfkState(UUID playerId) {
        Optional<User> userOpt = CachedDataService.findUser(playerId);
        userOpt.ifPresent(user -> {
            afkStartTimes.remove(playerId);
        });
    }
}
