package ru.loper.suncontainer.event.menu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncontainer.SunContainer;
import ru.loper.suncontainer.api.animation.impl.BaseContainerAnimation;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncontainer.event.rarity.ContainerLootSettings;
import ru.loper.suncontainer.event.rarity.chests.ContainerChest;
import ru.loper.suncontainer.utils.ContainerItem;
import ru.loper.suncontainer.utils.ItemRarity;
import ru.loper.suncontainer.utils.Utils;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.api.gui.Menu;
import ru.loper.suncore.api.items.ItemBuilder;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ContainerEventAnimationMenu extends Menu {
    private static final Material DEFAULT_RARITY_MATERIAL = Material.BLACK_STAINED_GLASS_PANE;
    private final BaseContainerAnimation animation;
    private final CustomConfig config;
    private final ContainerLootSettings lootSettings;
    private final ContainerChest chest;
    private final LootManager lootManager;
    private final EnumMap<ItemRarity, ItemBuilder> rarities;
    private final String title;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final int maxTicks;
    private final List<Integer> slots;
    protected int step = 0;
    protected double sapphires = 0.0;
    protected ItemRarity currentRarity = ItemRarity.DEFAULT;
    protected AnimationRunnable task;


    public ContainerEventAnimationMenu(PluginConfigManager configManager, LootManager lootManager, ContainerLootSettings lootSettings, ContainerChest chest) {
        this.lootManager = lootManager;
        this.lootSettings = lootSettings;
        this.chest = chest;
        config = configManager.getAnimationMenuConfig();
        title = configManager.getAnimationMenuTitle();
        maxTicks = lootSettings.getItemTicks();
        slots = configManager.getAnimationSlots();
        rarities = configManager.getRarities();

        this.animation = new BaseContainerAnimation() {
            @Override
            public void playAnimation(Player player, List<Integer> slots, List<Object> items) {
                currentPlayer = player;
                animationInventory = getInventory();
                task = new AnimationRunnable();
                task.runTaskTimer(SunContainer.getInstance(), lootSettings.getTickDelay(), lootSettings.getTickDelay());
            }

            @Override
            public void stopAnimation() {
                if (task != null && !task.isCancelled()) {
                    task.cancel();
                }
            }
        };
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
        animation.playAnimation(getOpener(), slots, new ArrayList<>());
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
        if (task != null && task.updateTitle) {
            task.updateTitle = false;
            return;
        }

        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    private void updateRarity() {
        if (random.nextInt(0, 100) <= lootSettings.getRarityChangeChance()) {
            currentRarity = Utils.upgradeRarity(currentRarity);
        }
    }

    private void setItemsRarity(int slot, ItemRarity rarity) {
        ItemBuilder rarityBuilder = getRarityBuilder(rarity);
        inventory.setItem(slot + 9, rarityBuilder.build());
        inventory.setItem(slot - 9, rarityBuilder.build());
    }

    private ItemBuilder getRarityBuilder(ItemRarity rarity) {
        return rarities.getOrDefault(rarity, new ItemBuilder(DEFAULT_RARITY_MATERIAL));
    }

    private class AnimationRunnable extends BukkitRunnable {
        protected boolean updateTitle = false;
        private int ticks = 0;

        @Override
        public void run() {
            Player player = animation.getCurrentPlayer();
            if (player == null) return;

            if (step >= slots.size()) {
                completeAnimation(player);
                return;
            }

            int slot = slots.get(step);
            ContainerItem containerItem = lootManager.getRandomItem(currentRarity);

            if (containerItem == null) {
                nextStep(player);
                return;
            }

            if (ticks == 0) {
                setItemsRarity(slot, containerItem.rarity());
            }

            if (ticks++ <= maxTicks) {
                updateSlot(player, slot, containerItem.itemStack());
                return;
            }

            completeItemTransfer(containerItem, slot);
            updateInventoryTitle(sapphires);
            updateRarity();

            ticks = 0;
            nextStep(player);
        }

        private void nextStep(Player player) {
            chest.addLootItem();
            step++;

            if (chest.getLootItems() >= lootSettings.getLootItems()) {
                completeAnimation(player);
            }
        }

        private void completeAnimation(Player player) {
            player.closeInventory();
            cancel();
        }

        private void updateSlot(Player player, int slot, ItemStack item) {
            inventory.setItem(slot, item);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 2.0f);
        }

        private void completeItemTransfer(ContainerItem item, int slot) {
            if (item.itemStack() != null) {
                Utils.addItemInventory(animation.getCurrentPlayer(), item.itemStack());
                sapphires += item.price();
                inventory.setItem(slot, item.itemStack());
            }
        }

        private void updateInventoryTitle(double sapphires) {
            updateTitle = true;
            String newTitle = title.replace("{price}", String.format("%.2f", sapphires));
            Inventory newInventory = Utils.updateTitle(inventory, newTitle);

            ItemStack[] contents = inventory.getContents();
            animation.setInventory(newInventory);
            setInventory(newInventory);
            newInventory.setContents(contents);
            animation.getCurrentPlayer().openInventory(newInventory);
        }
    }
}