package ru.loper.suncontainer.event.rarity;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suneventmanager.SunEventManager;
import ru.loper.suneventmanager.api.modules.event.rarity.EventRarityManager;

public class ContainerEventRarityManager extends EventRarityManager {
    public ContainerEventRarityManager(CustomConfig config, Plugin plugin) {
        super(config, plugin);
    }

    @Override
    protected void loadRarities() {
        ConfigurationSection raresSection = config.getConfig().getConfigurationSection("rarities");
        if (raresSection == null) {
            getPlugin().getLogger().severe("Раздел 'rarities' не найден в конфиге!");
            return;
        }

        for (String rarityKey : raresSection.getKeys(false)) {
            ConfigurationSection raritySection = raresSection.getConfigurationSection(rarityKey);
            if (raritySection != null) {
                rarities.put(rarityKey.toLowerCase(), new ContainerEventRarity(raritySection, SunEventManager.getInstance().getLootManager(), config));
            }
        }
    }
}
