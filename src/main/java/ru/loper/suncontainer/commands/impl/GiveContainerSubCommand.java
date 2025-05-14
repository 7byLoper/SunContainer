package ru.loper.suncontainer.commands.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.loper.suncontainer.SunContainer;
import ru.loper.suncontainer.config.DatabaseManager;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.utils.Colorize;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class GiveContainerSubCommand implements SubCommand {
    private final DatabaseManager databaseManager;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /container give <игрок> <количество>"));
            return;
        }

        try {
            Bukkit.getScheduler().runTaskAsynchronously(SunContainer.getInstance(), () -> {
                int amount = Integer.parseInt(args[2]);
                int playerCount = databaseManager.getValue(args[1]);
                databaseManager.setValue(args[1], playerCount + amount);
                sender.sendMessage(Colorize.parse("&b ▶ &fВы успешно выдали игроку &e" + args[1] + " &f" + amount + " контейнеров"));
            });
        } catch (NumberFormatException e) {
            sender.sendMessage(Colorize.parse("&c ▶ &fВведенное вами число некорректно"));
        }
    }

    @Override
    public List<String> onTabCompleter(CommandSender commandSender, String[] args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, args[1]))
                    .toList();
        }

        return Collections.emptyList();
    }
}
