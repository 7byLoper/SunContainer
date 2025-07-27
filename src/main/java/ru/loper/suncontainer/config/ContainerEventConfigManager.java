package ru.loper.suncontainer.config;

import org.bukkit.plugin.Plugin;
import ru.loper.suncontainer.event.rarity.ContainerEventRarityManager;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suneventmanager.config.EventConfigManager;

public class ContainerEventConfigManager extends EventConfigManager {
    public ContainerEventConfigManager(CustomConfig eventConfig, Plugin plugin) {
        super(eventConfig, plugin);
        rarityManager = new ContainerEventRarityManager(eventConfig, plugin);
    }
}
