package ru.loper.suncontainer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncontainer.SunContainer;
import ru.loper.suncontainer.commands.impl.*;
import ru.loper.suncontainer.config.DatabaseManager;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncontainer.menu.ContainerOpenMenu;
import ru.loper.suncore.api.command.AdvancedSmartCommandExecutor;
import ru.loper.suncore.api.command.SmartCommandExecutor;

public class ContainerCommand extends AdvancedSmartCommandExecutor {
    private final PluginConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final LootManager lootManager;

    public ContainerCommand(SunContainer plugin) {
        this.configManager = plugin.getConfigManager();
        this.databaseManager = plugin.getDatabaseManager();
        this.lootManager = plugin.getLootManager();

        addSubCommand(new AddItemSubCommand(lootManager), new Permission("suncontainer.command.additem"), "additem");
        addSubCommand(new GiveContainerSubCommand(databaseManager), new Permission("suncontainer.command.givecontainer"), "givecontainer");
        addSubCommand(new ListItemsSubCommand(lootManager), new Permission("suncontainer.command.listitems"), "listitems");
        addSubCommand(new ReloadSubCommand(configManager, lootManager), new Permission("suncontainer.command.reload"), "reload");
        addSubCommand(new RemoveItemSubCommand(lootManager), new Permission("suncontainer.command.removeitem"), "removeitem");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                new ContainerOpenMenu(configManager, databaseManager, lootManager).show(player);
            }
            return true;
        }

        SmartCommandExecutor.SubCommandWrapper subCommand = this.getCommandByLabel(args[0]);
        if (subCommand == null) {
            return true;
        }

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(this.getDontPermissionMessage());
            return true;
        }

        subCommand.getCommand().onCommand(sender, args);
        return true;
    }

    @Override
    public String getDontPermissionMessage() {
        return configManager.getNoPermission();
    }
}
