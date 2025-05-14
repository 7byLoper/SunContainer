package ru.loper.suncontainer.commands.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.utils.ItemRarity;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.utils.Colorize;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class AddItemSubCommand implements SubCommand {
    private final LootManager lootManager;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Colorize.parse("&c ▶ &fЭта команда доступна только игрокам"));
            return;
        }

        if (args.length < 4) {
            sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /container additem <название> <редкость> <стоимость>"));
            return;
        }

        try {
            ItemRarity rarity = ItemRarity.valueOf(args[2].toUpperCase());
            try {
                double price = Double.parseDouble(args[3]);
                lootManager.addItem(args[1], rarity, price, player.getInventory().getItemInMainHand());
                sender.sendMessage(Colorize.parse("&b ▶ &fПредмет '&e" + args[1] + "&f' успешно добавлен"));
            } catch (NumberFormatException e) {
                sender.sendMessage(Colorize.parse("&c ▶ &fВведенное вами число некорректно"));
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Colorize.parse("&c ▶ &fДоступные редкости: " +
                    Arrays.stream(ItemRarity.values())
                            .map(Enum::name)
                            .map(String::toLowerCase)
                            .collect(Collectors.joining("/"))));
        }

    }

    @Override
    public List<String> onTabCompleter(CommandSender commandSender, String[] args) {
        if (args.length == 3) {
            return Stream.of(ItemRarity.values())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .filter(rarity -> StringUtil.startsWithIgnoreCase(rarity, args[2]))
                    .toList();
        }

        return Collections.emptyList();
    }
}
