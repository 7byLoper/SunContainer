package ru.loper.suncontainer.event.rarity.chests;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import ru.loper.suncontainer.SunContainer;
import ru.loper.suneventmanager.api.modules.event.Event;
import ru.loper.suneventmanager.api.modules.event.rarity.EventRarity;
import ru.loper.suneventmanager.api.modules.loot.chest.Chest;
import ru.loper.suneventmanager.api.modules.loot.chest.ChestsManager;

import java.util.HashSet;
import java.util.Set;

public class ContainerChestsManager extends ChestsManager {
    public ContainerChestsManager(EventRarity eventRarity) {
        super(eventRarity);
    }

    @Override
    public void loadChests(Event event) {
        Location eventLocation = event.getEventLocation();
        if (eventLocation == null) return;

        Set<Chest> chests = new HashSet<>();

        SunContainer plugin = SunContainer.getInstance();

        for (Vector chestOffset : chestOffsets) {
            Location chestLocation = eventLocation.clone().add(chestOffset);
            ContainerChest chest = new ContainerChest(this, chestLocation, event, plugin.getConfigManager(), plugin.getLootManager());
            chest.spawn();
            chests.add(chest);
        }

        eventChests.put(event, chests);
    }
}
