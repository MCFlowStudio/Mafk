package com.flow.mafk.util;

import com.flow.mafk.Mafk;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class MessageUtil {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component formatMessage(String key, Object... args) {
        FileConfiguration config = Mafk.getInstance().getConfig();
        Object rawMessage = config.get("message." + key);

        // 메시지 초기화
        String message = "알 수 없는 메시지";
        if (rawMessage instanceof List) {
            List<String> messages = (List<String>) rawMessage;
            message = String.join("\n", messages);
        } else if (rawMessage instanceof String) {
            message = (String) rawMessage;
        }

        // 플레이스홀더 대체
        for (int i = 0; i < args.length; i++) {
            String replacement;
            if (args[i] instanceof Integer) {
                replacement = NumberFormat.getNumberInstance(Locale.getDefault()).format(args[i]);
            } else if (args[i] instanceof Double) {
                replacement = new DecimalFormat("#,###.##").format(args[i]);
            } else {
                replacement = args[i].toString();
            }
            message = message.replace("%" + (i + 1) + "%", replacement);
        }

        // 레거시 색상 코드를 처리
        Component component = LegacyComponentSerializer.legacySection().deserialize(message);

        // MiniMessage 포맷 처리
        String serializedMessage = LegacyComponentSerializer.legacySection().serialize(component);
        return component;
    }

    public static Component formatMessage(Player player, String key, Object... args) {
        FileConfiguration config = Mafk.getInstance().getConfig();
        Object rawMessage = config.get("message." + key);

        String message = "알 수 없는 메시지";
        if (rawMessage instanceof List<?>) {
            List<?> messages = (List<?>) rawMessage;
            message = String.join("\n", messages.stream().map(Object::toString).toArray(String[]::new));
        } else if (rawMessage instanceof String) {
            message = (String) rawMessage;
        }

        for (int i = 0; i < args.length; i++) {
            String replacement;
            if (args[i] instanceof Integer) {
                replacement = NumberFormat.getNumberInstance(Locale.getDefault()).format(args[i]);
            } else if (args[i] instanceof Double) {
                replacement = new DecimalFormat("#,###.#").format(args[i]);
            } else {
                replacement = args[i].toString();
            }
            message = message.replace("%" + (i + 1) + "%", replacement);
        }

        // PlaceholderAPI 플레이스홀더 대체
        message = PlaceholderAPI.setPlaceholders(player, message);

        // 레거시 색상 코드 처리
        Component component = LegacyComponentSerializer.legacySection().deserialize(message);

        // MiniMessage 포맷 처리 (여기서 추가 변환 필요 없으면 이 단계 생략 가능)
        String serializedMessage = LegacyComponentSerializer.legacySection().serialize(component);
        return component;
    }

    public static ItemStack loadItem(String key, Object... args) {
        FileConfiguration config = Mafk.getInstance().getConfig();
        String path = "items." + key + ".";

        Material type = Material.getMaterial(config.getString(path + "type", "AIR"));
        int amount = config.getInt(path + "amount", 1);
        ItemStack item = new ItemStack(type, amount);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // 이름에서 직접 플레이스홀더 치환
            String name = config.getString(path + "name", "");
            for (int i = 0; i < args.length; i++) {
                name = name.replace("%" + (i + 1) + "%", args[i] != null ? args[i].toString() : "");
            }
            meta.displayName(miniMessage.deserialize(name).decoration(TextDecoration.ITALIC, false));

            // 로어에서 직접 플레이스홀더 치환
            List<String> loreLines = config.getStringList(path + "lore");
            List<Component> lore = loreLines.stream()
                    .map(line -> {
                        for (int i = 0; i < args.length; i++) {
                            line = line.replace("%" + (i + 1) + "%", args[i] != null ? args[i].toString() : "");
                        }
                        return miniMessage.deserialize(line).decoration(TextDecoration.ITALIC, false);
                    })
                    .collect(Collectors.toList());

            if (!lore.isEmpty()) {
                meta.lore(lore);
            }

            // 커스텀 모델 데이터 설정
            if (config.contains(path + "customModelData")) {
                meta.setCustomModelData(config.getInt(path + "customModelData"));
            }

            // 인챈트 추가
            if (config.contains(path + "enchantments")) {
                config.getConfigurationSection(path + "enchantments").getKeys(false).forEach(enchantKey -> {
                    Enchantment enchantment = Enchantment.getByName(enchantKey);
                    if (enchantment != null) {
                        meta.addEnchant(enchantment, config.getInt(path + "enchantments." + enchantKey), true);
                    }
                });
            }

            item.setItemMeta(meta);
        }
        return item;
    }

    public static String formatNumber(double number) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        return formatter.format(number);
    }

    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        // 결과 문자열을 구성합니다.
        StringBuilder timeString = new StringBuilder();

        if (hours > 0) {
            timeString.append(hours).append("시간 ");
        }
        if (minutes > 0) {
            timeString.append(minutes).append("분 ");
        }
        if (secs > 0 || timeString.length() == 0) {
            timeString.append(secs).append("초");
        }

        return timeString.toString().trim();
    }


}
