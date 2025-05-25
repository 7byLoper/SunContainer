package ru.loper.suncontainer.menu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncontainer.SunContainer;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncontainer.utils.ContainerItem;
import ru.loper.suncontainer.utils.ItemRarity;
import ru.loper.suncontainer.utils.Utils;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.gui.Menu;
import ru.loper.suncore.api.items.ItemBuilder;
import ru.loper.suncore.utils.MessagesUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ContainerAnimationMenu extends Menu {

    private static final Material DEFAULT_RARITY_MATERIAL = Material.BLACK_STAINED_GLASS_PANE;

    private final transient CustomConfig config;
    private transient BukkitTask task;
    private transient boolean fastPrize = false;
    private final transient LootManager lootManager;
    private final EnumMap<ItemRarity, ItemBuilder> rarities;
    private final String title;
    private final List<String> broadcast;

    private final transient Random random = ThreadLocalRandom.current();
    private final int maxTicks;
    private final List<Integer> slots;
    private transient int step = 0;
    private transient double sapphires = 0.0;
    private transient ItemRarity currentRarity = ItemRarity.DEFAULT;

    public ContainerAnimationMenu(PluginConfigManager configManager, LootManager lootManager) {
        this.lootManager = lootManager;
        config = configManager.getAnimationMenuConfig();
        title = configManager.getAnimationMenuTitle();
        maxTicks = configManager.getAnimationItemTicks();
        slots = configManager.getAnimationSlots();
        broadcast = new ArrayList<>(configManager.getOpenBroadcast());
        rarities = configManager.getRarities();
    }

    @Override
    public String getTitle() {
        return title.replace("{price}", "0");
    }

    @Override
    public int getSize() {
        return config.getConfig().getInt("rows", 3) * 9;
    }

    @Override
    public void getItemsAndButtons() {
        addDecor();
        task = runAnimation();
    }

    private void addDecor() {
        ConfigurationSection decorSection = config.getConfig().getConfigurationSection("decor");
        if (decorSection == null) return;

        for (String key : decorSection.getKeys(false)) {
            try {
                Material material = Material.valueOf(key.toUpperCase());
                addDecorItems(material, decorSection.getIntegerList(key));
            } catch (IllegalArgumentException e) {
                SunCore.printStacktrace("Ошибка при загрузке декораций для анимационного меню", e);
            }
        }
    }


    @Override
    public void onClose(@NotNull InventoryCloseEvent e) {
        if (fastPrize) {
            fastPrize = false;
            return;
        }

        if (task != null && !task.isCancelled()) {
            task.cancel();
            fastPrize();
        }
    }

    private void fastPrize() {
        Player player = getOpener();
        if (player == null) return;

        for (; step < slots.size(); step++) {
            ContainerItem item = lootManager.getRandomItem(currentRarity);
            if (item != null && item.itemStack() != null) {
                Utils.addItemInventory(player, item.itemStack());
                sapphires += item.price();
            }
            updateRarity();
        }

        broadcastPrise();
    }

    private void broadcastPrise() {
        Player player = getOpener();
        if (player == null) return;

        broadcast.replaceAll(a -> a
                .replace("{player}", player.getName())
                .replace("{price}", String.format("%.2f", sapphires)));

        broadcast.forEach(MessagesUtils::broadcast);
    }

    private BukkitTask runAnimation() {
        Player player = getOpener();
        if (player == null) return null;

        return new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (step >= slots.size()) {
                    fastPrize = true;
                    player.closeInventory();
                    broadcastPrise();
                    cancel();
                    return;
                }

                int slot = slots.get(step);
                ContainerItem containerItem = lootManager.getRandomItem(currentRarity);

                if (containerItem == null) {
                    step++;
                    return;
                }

                if (ticks == 0) {
                    setItemsRarity(slot, containerItem.rarity());
                }

                if (ticks++ <= maxTicks) {
                    updateSlot(slot, containerItem);
                    return;
                }

                completeItemTransfer(containerItem, slot);
                updateInventoryTitle(sapphires);
                updateRarity();

                ticks = 0;
                step++;
            }

            private void updateSlot(int slot, ContainerItem item) {
                getInventory().setItem(slot, item.itemStack());
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 2.0f);
            }

            private void completeItemTransfer(ContainerItem item, int slot) {
                if (item.itemStack() != null) {
                    Utils.addItemInventory(player, item.itemStack());
                    sapphires += item.price();
                    getInventory().setItem(slot, item.itemStack());
                }
            }

            private void updateInventoryTitle(double sapphires) {
                Inventory newInventory = Utils.updateTitle(
                        getInventory(),
                        title.replace("{price}", String.format("%.2f", sapphires))
                );
                setInventory(newInventory);
                fastPrize = true;
                player.openInventory(newInventory);
            }
        }.runTaskTimer(SunContainer.getInstance(), 5L, 4L);
    }

    private void updateRarity() {
        if (random.nextInt(5) == 0) {
            currentRarity = Utils.upgradeRarity(currentRarity);
        }
    }

    private void setItemsRarity(int slot, ItemRarity rarity) {
        ItemBuilder rarityBuilder = getRarityBuilder(rarity);
        Inventory inv = getInventory();
        inv.setItem(slot + 9, rarityBuilder.build());
        inv.setItem(slot - 9, rarityBuilder.build());
    }

    private ItemBuilder getRarityBuilder(ItemRarity rarity) {
        return rarities.getOrDefault(rarity, new ItemBuilder(DEFAULT_RARITY_MATERIAL));
    }
}