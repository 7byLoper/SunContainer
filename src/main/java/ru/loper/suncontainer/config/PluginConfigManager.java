package ru.loper.suncontainer.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import ru.loper.suncontainer.SunContainer;
import ru.loper.suncontainer.utils.ItemRarity;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.api.config.ConfigManager;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.database.DataBaseManager;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suncore.utils.Colorize;

import java.util.EnumMap;
import java.util.List;

@Getter
public class PluginConfigManager extends ConfigManager {
    private ContainerEventConfigManager eventConfigManager;
    private DataBaseManager databaseManager;
    private List<String> openBroadcast;
    private List<String> eventEndMessages;
    private List<Integer> animationSlots;
    private EnumMap<ItemRarity, ItemBuilder> rarities;
    private String animationMenuTitle, noPermission, containerIsOpeningMessage, unknownPlaceholder;

    private int animationItemTicks;

    public PluginConfigManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfigs() {
        addCustomConfig(new CustomConfig("menu/animation_menu.yml", plugin));
        addCustomConfig(new CustomConfig("menu/open_menu.yml", plugin));
        addCustomConfig(new CustomConfig("event.yml", plugin));
        plugin.saveDefaultConfig();
    }

    @Override
    public void loadValues() {
        openBroadcast = configMessages("messages.open_message");
        eventEndMessages = configMessages("messages.end_message");
        noPermission = configMessage("messages.no_permission");
        containerIsOpeningMessage = configMessage("messages.container_event_is_opening");
        unknownPlaceholder = configMessage("messages.unknown_placeholder");

        animationSlots = getAnimationMenuConfig().getConfig().getIntegerList("item_slots");
        animationItemTicks = getAnimationMenuConfig().getConfig().getInt("item_ticks", 10);
        animationMenuTitle = getAnimationMenuConfig().configMessage("title");

        ConfigurationSection databaseSection = plugin.getConfig().getConfigurationSection("database");
        if (databaseSection != null) {
            databaseManager = new DataBaseManager(databaseSection, plugin);
        } else {
            plugin.getLogger().severe("Ошибка загрузки базы данных, плагин не будет работать");
        }

        if (SunContainer.getInstance().isActiveEventManager()) {
            eventConfigManager = new ContainerEventConfigManager(getEventConfig(), plugin);
        }

        loadRarities();
    }

    private void loadRarities() {
        rarities = new EnumMap<>(ItemRarity.class);

        ConfigurationSection rarityForms = getAnimationMenuConfig().getConfig().getConfigurationSection("rarity_forms");
        ItemBuilder defaultForm = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE);

        for (ItemRarity rarity : ItemRarity.values()) {
            rarities.put(rarity, defaultForm);
        }

        if (rarityForms == null) return;

        for (String key : rarityForms.getKeys(false)) {
            ConfigurationSection rarityItem = rarityForms.getConfigurationSection(key);
            if (rarityItem == null) continue;

            try {
                ItemRarity rarity = ItemRarity.valueOf(key.toUpperCase());
                rarities.put(rarity, ItemBuilder.fromConfig(rarityItem));
            } catch (IllegalArgumentException exception) {
                SunCore.printStacktrace("Неизвестная редкость: " + key, exception);
            }
        }
    }

    public CustomConfig getAnimationMenuConfig() {
        return getCustomConfig("menu/animation_menu.yml");
    }

    public CustomConfig getOpenMenuConfig() {
        return getCustomConfig("menu/open_menu.yml");
    }

    public CustomConfig getEventConfig() {
        return getCustomConfig("event.yml");
    }

    public String configMessage(String path) {
        String message = plugin.getConfig().getString(path, "unknown");
        return Colorize.parse(message);
    }

    public List<String> configMessages(String path) {
        List<String> message = plugin.getConfig().getStringList(path);
        return Colorize.parse(message);
    }
}
