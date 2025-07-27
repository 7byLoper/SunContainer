package ru.loper.suncontainer.event.rarity;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class ContainerLootSettings {
    private final int lootItems;
    private final int rarityChangeChance;
    private final int itemTicks;
    private final int tickDelay;

    public ContainerLootSettings(ConfigurationSection section) {
        lootItems = section.getInt("items");
        rarityChangeChance = section.getInt("change_chance");
        itemTicks = section.getInt("item_ticks");
        tickDelay = section.getInt("tick_delay");
    }
}
