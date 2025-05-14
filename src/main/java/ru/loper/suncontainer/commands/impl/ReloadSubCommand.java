package ru.loper.suncontainer.commands.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncore.api.command.SubCommand;
import ru.loper.suncore.utils.Colorize;

import java.util.List;

@RequiredArgsConstructor
public class ReloadSubCommand implements SubCommand {
    private final PluginConfigManager configManager;
    private final LootManager lootManager;

    @Override
    public void onCommand(CommandSender commandSender, String[] args) {
        configManager.reloadAll();
        lootManager.loadAllItems();
        commandSender.sendMessage(Colorize.parse("&b ▶ &fПлагин успешно перезагружен"));
    }

    @Override
    public List<String> onTabCompleter(CommandSender commandSender, String[] strings) {
        return List.of();
    }
}
