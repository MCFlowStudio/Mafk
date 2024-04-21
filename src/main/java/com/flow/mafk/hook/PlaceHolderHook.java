package com.flow.mafk.hook;

import com.flow.mafk.database.CachedDataService;
import com.flow.mafk.util.MessageUtil;
import com.flow.mafk.util.object.User;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlaceHolderHook extends PlaceholderExpansion {

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if (player == null) {
            return "";
        }

        if ("state".equals(params)) {
            Optional<User> userOpt = CachedDataService.findUser(player.getUniqueId());
            if (!userOpt.isPresent()) return "N/A";
            User user = userOpt.get();
            if (user.isAfkState())
                return "켜짐";
            return "꺼짐";
        }


        if ("point".equals(params)) {
            Optional<User> userOpt = CachedDataService.findUser(player.getUniqueId());
            if (!userOpt.isPresent()) return "N/A";
            User user = userOpt.get();
            return MessageUtil.formatNumber(user.getAfkPoint());
        }

        return null;
    }

    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mafk";
    }

    @Override
    public @NotNull String getAuthor() {
        return "minhyeok";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

}
