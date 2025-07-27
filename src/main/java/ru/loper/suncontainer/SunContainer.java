package ru.loper.suncontainer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncontainer.api.database.ContainersDatabase;
import ru.loper.suncontainer.api.storage.ContainersStorage;
import ru.loper.suncontainer.commands.ContainerCommand;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncontainer.event.ContainerEvent;
import ru.loper.suncore.SunCore;
import ru.loper.suneventmanager.SunEventManager;
import ru.loper.suneventmanager.api.modules.event.EventManager;

import java.util.Optional;

@Getter
public final class SunContainer extends JavaPlugin {
    @Getter
    private static SunContainer instance;
    private ContainersStorage containersStorage;
    private LootManager lootManager;
    private PluginConfigManager configManager;
    private boolean activeEventManager;

    @Override
    public void onEnable() {
        instance = this;

        activeEventManager = Bukkit.getPluginManager().isPluginEnabled("SunEventManager");

        configManager = new PluginConfigManager(this);
        containersStorage = new ContainersDatabase(configManager.getDatabaseManager());
        containersStorage.createTable();
        lootManager = new LootManager(this);

        if (activeEventManager) {
            registerEvent();
        } else {
            getLogger().warning("Не удалось зарегистрировать ивент контейнера — отсутствует зависимость 'SunEventManager'. Приобрести её можно здесь: t.me/bySunDev");
        }

        if (checkSunCoreVersion()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Optional.ofNullable(getCommand("container")).orElseThrow().setExecutor(new ContainerCommand(this));
    }

    private void registerEvent() {
        EventManager eventManager = SunEventManager.getInstance().getEventManager();
        if (eventManager == null) return;

        eventManager.registerEvent(this, "container", ContainerEvent.class, configManager.getEventConfigManager());
    }

    private boolean checkSunCoreVersion() {
        if (!Bukkit.getPluginManager().isPluginEnabled("SunCore")) {
            getLogger().severe("Для работы плагина необходимо скачать SunCore");
            return true;
        }

        SunCore sunCore = SunCore.getInstance();
        int version = extractVersion(sunCore.getDescription().getVersion());

        if (version < 1036) {
            getLogger().severe("Для работы плагина необходим SunCore версии 1.0.3.6 и выше. Ссылка на скачивание: http://t.me/bysundev_bot?start=free-SunCore");
            return true;
        }

        return false;
    }

    private int extractVersion(String ver) {
        String version = ver.replaceAll("[^0-9]", "");
        return Integer.parseInt(version);
    }
}
