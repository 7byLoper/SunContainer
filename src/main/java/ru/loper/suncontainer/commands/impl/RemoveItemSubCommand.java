package ru.loper.suncontainer.commands.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.utils.ContainerItem;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.utils.Colorize;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class RemoveItemSubCommand implements SubCommand {
    private final LootManager lootManager;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /container removeitem <название>"));
            return;
        }

        if (lootManager.removeItem(args[1])) {
            sender.sendMessage(Colorize.parse("&b ▶ &fПредмет '&e" + args[1] + "&f' успешно удален"));
            return;
        }

        sender.sendMessage(Colorize.parse("&c ▶ &fПредмет с названием '&e" + args[1] + "&f' не найден"));
    }

    @Override
    public List<String> onTabCompleter(CommandSender commandSender, String[] args) {
        if (args.length == 2) {
            return lootManager.getAllItems().stream()
                    .map(ContainerItem::name)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, args[1])).toList();
        }

        return Collections.emptyList();
    }
}
