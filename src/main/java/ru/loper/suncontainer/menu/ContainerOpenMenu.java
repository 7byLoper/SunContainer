package ru.loper.suncontainer.menu;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.loper.suncontainer.SunContainer;
import ru.loper.suncontainer.config.ConfigManager;
import ru.loper.suncontainer.config.DatabaseManager;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.gui.Button;
import ru.loper.suncore.api.gui.Menu;
import ru.loper.suncore.api.items.ItemBuilder;

import java.util.List;

public class ContainerOpenMenu extends Menu {
    private final CustomConfig config;
    private final PluginConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final LootManager lootManager;

    public ContainerOpenMenu(PluginConfigManager configManager, DatabaseManager databaseManager, LootManager lootManager) {
        this.databaseManager = databaseManager;
        this.configManager = configManager;
        this.lootManager = lootManager;
        config = configManager.getOpenMenuConfig();
    }

    @Override
    public String getTitle() {
        return config.configMessage("title");
    }

    @Override
    public int getSize() {
        return config.getConfig().getInt("rows", 3) * 9;
    }

    @Override
    public void getItemsAndButtons() {
        addOpenItem();
        addDecor();
    }

    private void addOpenItem() {
        ConfigurationSection itemSection = config.getConfig().getConfigurationSection("items.open_item");
        if (itemSection == null) return;

        int containers = databaseManager.getValue(getOpener().getName());

        ItemBuilder builder = ItemBuilder.fromConfig(itemSection);

        String displayName = itemSection.getString("display_name", "").replace("{containers}", String.valueOf(containers));
        builder.name(displayName);

        List<String> lore = itemSection.getStringList("lore");
        lore.replaceAll(s -> s.replace("{containers}", String.valueOf(containers)));
        builder.lore(lore);

        buttons.add(new Button(builder.build(), itemSection.getInt("slot")) {
            @Override
            public void onClick(InventoryClickEvent e) {
                if (e.getWhoClicked() instanceof Player player) {
                    if (containers > 0) {
                        new ContainerAnimationMenu(configManager, lootManager).show(player);
                        databaseManager.setValue(player.getName(), containers - 1);
                        return;
                    }
                    configManager.configMessages("messages.no_keys_message").forEach(player::sendMessage);
                }
            }
        });
    }

    private void addDecor() {
        ConfigurationSection decorSection = config.getConfig().getConfigurationSection("items.decor");
        if (decorSection == null) return;
        for (String key : decorSection.getKeys(false)) {
            try {
                addDecorItems(Material.valueOf(key.toUpperCase()), decorSection.getIntegerList(key));
            } catch (IllegalArgumentException e) {
                SunCore.printStacktrace("Ошибка при загрузке декораций для tags menu", e);
            }
        }
    }
}
