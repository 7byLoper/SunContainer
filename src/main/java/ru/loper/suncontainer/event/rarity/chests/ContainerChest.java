package ru.loper.suncontainer.event.rarity.chests;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncontainer.config.LootManager;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncontainer.event.ContainerEvent;
import ru.loper.suncontainer.event.menu.ContainerEventAnimationMenu;
import ru.loper.suncontainer.event.rarity.ContainerLootSettings;
import ru.loper.suneventmanager.api.modules.event.Event;
import ru.loper.suneventmanager.api.modules.event.EventState;
import ru.loper.suneventmanager.api.modules.loot.chest.Chest;
import ru.loper.suneventmanager.api.modules.loot.chest.ChestsManager;
import ru.loper.suneventmanager.hook.HologramsHook;
import ru.loper.suneventmanager.utils.TimeUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ContainerChest extends Chest {
    private final PluginConfigManager configManager;
    private final LootManager lootManager;
    private boolean inUse = false;
    private int lootItems;
    private String usePlayer;

    public ContainerChest(ChestsManager chestsManager, Location location, Event event, PluginConfigManager configManager, LootManager lootManager) {
        super(chestsManager, location, event);
        this.configManager = configManager;
        this.lootManager = lootManager;
    }

    protected void updateHologram(List<String> lines) {
        List<String> formattedLines = lines.stream()
                .map(line -> line.replace("{time}", TimeUtils.formatSeconds(event.getEventTimer()))
                        .replace("{max-loot}", String.valueOf(getEvent().getEventRarity().getLootItemsCount()))
                        .replace("{loot}", String.valueOf(lootItems)))
                .collect(Collectors.toList());

        HologramsHook.createOrUpdateHologram(formattedLines, this.hologramLocation, this.hologramName);
    }

    @Override
    public void onPlayerOpen(Player player) {
        if (event.getCurrentState() != EventState.STARTED) return;

        if (inUse) {
            player.sendMessage(configManager.getContainerIsOpeningMessage().replace("{player}", usePlayer));
            return;
        }

        inUse = true;
        usePlayer = player.getName();
        ContainerEventAnimationMenu menu = createAnimationMenu(player);

        menu.show(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 1));
    }

    private @NotNull ContainerEventAnimationMenu createAnimationMenu(Player player) {
        ContainerLootSettings lootSettings = getEvent().getEventRarity().getLootSettings();
        return new ContainerEventAnimationMenu(configManager, lootManager, lootSettings, this) {
            @Override
            public void onClose(@NotNull InventoryCloseEvent e) {
                super.onClose(e);
                inUse = false;
                usePlayer = null;

                if (event instanceof ContainerEvent containerEvent) {
                    containerEvent.addPlayerResult(player.getUniqueId(), sapphires);
                }
            }
        };
    }

    @Override
    public void open() {
        //no logic
    }

    public ContainerEvent getEvent() {
        return (ContainerEvent) event;
    }

    public void addLootItem() {
        lootItems += 1;
        if (lootItems >= getEvent().getEventRarity().getLootItemsCount()) {
            getEvent().updateStarted();
        }
    }

    @Override
    public void remove() {
        super.remove();
        inUse = false;
        usePlayer = null;
    }
}