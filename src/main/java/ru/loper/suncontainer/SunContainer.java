package ru.loper.suncontainer;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncontainer.commands.ContainerCommand;
import ru.loper.suncontainer.config.ConfigManager;
import ru.loper.suncontainer.config.DatabaseManager;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;

import java.util.Optional;

@Getter
public final class SunContainer extends JavaPlugin {
    @Getter
    private static SunContainer instance;
    private DatabaseManager databaseManager;
    private LootManager lootManager;
    private PluginConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new PluginConfigManager(this);
        databaseManager = new DatabaseManager(this);
        lootManager = new LootManager(this);

        databaseManager.connect();

        Optional.ofNullable(getCommand("container")).orElseThrow().setExecutor(new ContainerCommand(this));
    }

    @Override
    public void onDisable() {
        databaseManager.disconnect();
    }
}
