package ru.loper.suncontainer.event.rarity;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncontainer.event.rarity.chests.ContainerChestsManager;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suneventmanager.api.modules.event.rarity.EventRarity;
import ru.loper.suneventmanager.api.modules.loot.LootManager;

import java.util.Objects;

@Getter
public class ContainerEventRarity extends EventRarity {
    private final ContainerLootSettings lootSettings;

    public ContainerEventRarity(@NotNull ConfigurationSection rareSection,
                                @NotNull LootManager lootManager,
                                @NotNull CustomConfig config) {
        super(rareSection, lootManager, config);
        chestsManager = new ContainerChestsManager(this);
        lootSettings = new ContainerLootSettings(
                Objects.requireNonNull(rareSection.getConfigurationSection("loot"), "loot section is null, please edit your config")
        );
    }

    public int getLootItemsCount() {
        return lootSettings.getLootItems();
    }

    public int getRarityChangeChance() {
        return lootSettings.getRarityChangeChance();
    }

    public int getItemTicks() {
        return lootSettings.getItemTicks();
    }

    public int getTickDelay() {
        return lootSettings.getTickDelay();
    }

}