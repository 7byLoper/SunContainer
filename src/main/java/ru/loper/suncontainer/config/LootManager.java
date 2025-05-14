package ru.loper.suncontainer.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.loper.suncontainer.utils.ContainerItem;
import ru.loper.suncontainer.utils.ItemRarity;
import ru.loper.suncore.api.config.CustomConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class LootManager {
    private final CustomConfig config;
    private final Map<ItemRarity, List<ContainerItem>> itemsByRarity = new ConcurrentHashMap<>();
    private final Map<String, ContainerItem> itemsByName = new ConcurrentHashMap<>();
    private boolean initialized = false;

    public LootManager(Plugin plugin) {
        this.config = new CustomConfig("items.yml", false, plugin);
        loadAllItems();
    }

    public synchronized void loadAllItems() {
        itemsByRarity.clear();
        itemsByName.clear();

        for (ItemRarity rarity : ItemRarity.values()) {
            itemsByRarity.put(rarity, new ArrayList<>());
        }

        ConfigurationSection itemsSection = config.getConfig().getConfigurationSection("items");
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null) continue;

            try {
                ItemRarity rarity = ItemRarity.valueOf(itemSection.getString("rarity", "DEFAULT").toUpperCase());
                double value = itemSection.getDouble("value", 0.0);
                ItemStack itemStack = itemSection.getItemStack("item");

                if (itemStack != null) {
                    ContainerItem containerItem = new ContainerItem(key, rarity, value, itemStack);
                    itemsByRarity.get(rarity).add(containerItem);
                    itemsByName.put(key, containerItem);
                }
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        initialized = true;
    }

    public void addItem(String name, ItemRarity rarity, double value, ItemStack item) {
        ConfigurationSection itemSection = config.getConfig().createSection("items." + name);
        itemSection.set("rarity", rarity.toString());
        itemSection.set("value", value);
        itemSection.set("item", item.clone());
        config.saveConfig();

        ContainerItem containerItem = new ContainerItem(name, rarity, value, item.clone());
        synchronized (this) {
            itemsByRarity.computeIfAbsent(rarity, k -> new ArrayList<>()).add(containerItem);
            itemsByName.put(name, containerItem);
        }
    }

    public boolean removeItem(String name) {
        if (!config.getConfig().contains("items." + name)) {
            return false;
        }

        config.getConfig().set("items." + name, null);
        config.saveConfig();

        synchronized (this) {
            ContainerItem removed = itemsByName.remove(name);
            if (removed != null) {
                List<ContainerItem> rarityItems = itemsByRarity.get(removed.rarity());
                if (rarityItems != null) {
                    rarityItems.removeIf(item -> item.name().equals(name));
                }
            }
        }

        return true;
    }

    public ContainerItem getRandomItem(ItemRarity rarity) {
        if (!initialized) {
            loadAllItems();
        }

        List<ContainerItem> items = itemsByRarity.get(rarity);
        if (items == null || items.isEmpty()) {
            return null;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(items.size());
        return items.get(randomIndex);
    }

    public Collection<ContainerItem> getAllItems() {
        return itemsByName.values();
    }

    public ContainerItem getItemByName(String name) {
        return itemsByName.get(name);
    }

    public List<ContainerItem> getItemsByRarity(ItemRarity rarity) {
        return Collections.unmodifiableList(itemsByRarity.getOrDefault(rarity, Collections.emptyList()));
    }
}
