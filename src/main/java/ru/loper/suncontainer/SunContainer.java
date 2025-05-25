package ru.loper.suncontainer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncontainer.commands.ContainerCommand;
import ru.loper.suncontainer.config.DatabaseManager;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncore.SunCore;

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
        if (checkSunCoreVersion()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
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

    private boolean checkSunCoreVersion() {
        if (!Bukkit.getPluginManager().isPluginEnabled("SunCore")) {
            getLogger().severe("Для работы плагина необходимо скачать SunCore");
            return true;
        }

        SunCore sunCore = SunCore.getInstance();
        int version = extractVersion(sunCore.getDescription().getVersion());

        if (version < 1032) {
            getLogger().severe("Для работы плагина необходим SunCore версии 1.0.3.2 и выше");
            return true;
        }

        return false;
    }

    private int extractVersion(String ver) {
        String version = ver.replaceAll("[^0-9]", "");
        return Integer.parseInt(version);
    }
}
