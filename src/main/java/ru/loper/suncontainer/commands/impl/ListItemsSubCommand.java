package ru.loper.suncontainer.commands.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.utils.ContainerItem;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.utils.Colorize;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class ListItemsSubCommand implements SubCommand {
    private final LootManager lootManager;

    @Override
    public void onCommand(CommandSender sender, String[] strings) {
        Collection<ContainerItem> items = lootManager.getAllItems();
        if (items.isEmpty()) {
            sender.sendMessage(Colorize.parse("&c ▶ &fНет добавленных предметов"));
            return;
        }

        sender.sendMessage(Colorize.parse("&b ▶ &fСписок предметов (&e" + items.size() + "&f):"));
        items.forEach(item -> sender.sendMessage(Colorize.parse(
                String.format(" &7- &f%s &7(Редкость: &f%s&7, Цена: &f%.2f&7)",
                        item.name(),
                        item.rarity().name().toLowerCase(),
                        item.price())
        )));
    }

    @Override
    public List<String> onTabCompleter(CommandSender commandSender, String[] strings) {
        return List.of();
    }
}
