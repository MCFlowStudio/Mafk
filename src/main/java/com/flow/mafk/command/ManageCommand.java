package com.flow.mafk.command;

import com.flow.mafk.Mafk;
import com.flow.mafk.database.CachedDataService;
import com.flow.mafk.database.StorageDataService;
import com.flow.mafk.util.object.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ManageCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("/잠수관리 지급 [닉네임] [포인트]");
            sender.sendMessage("/잠수관리 차감 [닉네임] [포인트]");
            sender.sendMessage("/잠수관리 설정 [닉네임] [포인트]");
            sender.sendMessage("/잠수관리 확인 [닉네임]");
            sender.sendMessage("/잠수관리 리로드");
            return true;
        }
        String targetName = args[1];
        if (Bukkit.getOfflinePlayer(targetName) == null) {
            sender.sendMessage("알 수 없는 유저입니다.");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        Boolean isCached = CachedDataService.findUser(target.getUniqueId()).isPresent();
        switch (args[0]) {
            case "리로드": {
                Mafk.getInstance().reloadConfig();
                sender.sendMessage("콘피그를 리로드했습니다.");
                break;
            }
            case "확인": {
                if (isCached) {
                    sender.sendMessage(targetName + " 님이 보유 포인트는 " + CachedDataService.findUser(target.getUniqueId()).get().getAfkPoint() + " 포인트입니다.");
                    break;
                } else {
                    if (!StorageDataService.loadUserDataSync(target.getUniqueId()).isPresent()) {
                        sender.sendMessage("알 수 없는 유저입니다.");
                        break;
                    }
                    sender.sendMessage(targetName + " 님이 보유 포인트는 " + StorageDataService.loadUserDataSync(target.getUniqueId()).get().getAfkPoint() + " 포인트입니다.");
                    break;
                }
            }
            case "지급": {
                if (args.length < 3 || !isNumeric(args[2])) {
                    sender.sendMessage("올바른 사용법이 아닙니다.");
                    break;
                }
                Integer num = Integer.parseInt(args[2]);
                if (isCached) {
                    CachedDataService.findUser(target.getUniqueId()).get().addAfkPoint(num);
                    sender.sendMessage(targetName + " 님에게 " + num + " 포인트를 지급했습니다.");
                    break;
                } else {
                    if (!StorageDataService.loadUserDataSync(target.getUniqueId()).isPresent()) {
                        sender.sendMessage("알 수 없는 유저입니다.");
                        break;
                    }
                    User user = StorageDataService.loadUserDataSync(target.getUniqueId()).get();
                    user.addAfkPoint(num);
                    StorageDataService.saveUserDataSync(user);
                    sender.sendMessage(targetName + " 님에게 " + num + " 포인트를 지급했습니다.");
                    break;
                }
            }
            case "차감": {
                if (args.length < 3 || !isNumeric(args[2])) {
                    sender.sendMessage("올바른 사용법이 아닙니다.");
                    break;
                }
                Integer num = Integer.parseInt(args[2]);
                if (isCached) {
                    CachedDataService.findUser(target.getUniqueId()).get().removeAfkPoint(num);
                    sender.sendMessage(targetName + " 님에게서 " + num + " 포인트를 차감했습니다.");
                    break;
                } else {
                    if (!StorageDataService.loadUserDataSync(target.getUniqueId()).isPresent()) {
                        sender.sendMessage("알 수 없는 유저입니다.");
                        break;
                    }
                    User user = StorageDataService.loadUserDataSync(target.getUniqueId()).get();
                    user.removeAfkPoint(num);
                    StorageDataService.saveUserDataSync(user);
                    sender.sendMessage(targetName + " 님에게서 " + num + " 포인트를 차감했습니다.");
                    break;
                }
            }
            case "설정": {
                if (args.length < 3 || !isNumeric(args[2])) {
                    sender.sendMessage("올바른 사용법이 아닙니다.");
                    break;
                }
                Integer num = Integer.parseInt(args[2]);
                if (isCached) {
                    CachedDataService.findUser(target.getUniqueId()).get().setAfkPoint(num);
                    sender.sendMessage(targetName + " 님의 포인트를 " + num + " (으)로 설정했습니다.");
                    break;
                } else {
                    if (!StorageDataService.loadUserDataSync(target.getUniqueId()).isPresent()) {
                        sender.sendMessage("알 수 없는 유저입니다.");
                        break;
                    }
                    User user = StorageDataService.loadUserDataSync(target.getUniqueId()).get();
                    user.setAfkPoint(num);
                    StorageDataService.saveUserDataSync(user);
                    sender.sendMessage(targetName + " 님의 포인트를 " + num + " (으)로 설정했습니다.");
                    break;
                }
            }
        }
        return true;
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 첫 번째 인자 자동완성
            if ("잠수관리".equalsIgnoreCase(command.getName())) {
                if (args[0].isEmpty() || "리로드".startsWith(args[0].toLowerCase())) {
                    completions.add("리로드");
                }
                if (args[0].isEmpty() || "확인".startsWith(args[0].toLowerCase())) {
                    completions.add("확인");
                }
                if (args[0].isEmpty() || "지급".startsWith(args[0].toLowerCase())) {
                    completions.add("지급");
                }
                if (args[0].isEmpty() || "차감".startsWith(args[0].toLowerCase())) {
                    completions.add("차감");
                }
                if (args[0].isEmpty() || "설정".startsWith(args[0].toLowerCase())) {
                    completions.add("설정");
                }
            }
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "확인":
                case "지급":
                case "차감":
                case "설정":
                    Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                    break;
            }
        }

        return completions;
    }

}
