package ru.loper.suncontainer.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.loper.suncontainer.SunContainer;
import ru.loper.suncontainer.config.PluginConfigManager;
import ru.loper.suncontainer.event.rarity.ContainerEventRarity;
import ru.loper.suncore.utils.MessagesUtils;
import ru.loper.suneventmanager.api.modules.event.Event;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContainerEvent extends Event {
    private final ContainerEventResults results = new ContainerEventResults();

    public ContainerEvent() {
        super("container", SunContainer.getInstance().getConfigManager().getEventConfigManager());
    }

    @Override
    public void prestart() {
    }

    @Override
    public void start() {
    }

    @Override
    public void open() {

    }

    @Override
    public void remove() {
        announceTopPlayers();
    }

    public void addPlayerResult(UUID playerId, double sapphires) {
        results.addResult(playerId, sapphires);
    }

    private void announceTopPlayers() {
        List<Map.Entry<UUID, Double>> topPlayers = results.getResults().entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        PluginConfigManager configManager = SunContainer.getInstance().getConfigManager();
        List<String> endMessages = configManager.getEventEndMessages();
        String unknownPlaceholder = configManager.getUnknownPlaceholder();

        for (String message : endMessages) {
            String formatted = message;

            formatted = formatted.replace("{first-name}", getPlayerName(topPlayers, 0, unknownPlaceholder))
                    .replace("{first-value}", getPlayerValue(topPlayers, 0))
                    .replace("{second-name}", getPlayerName(topPlayers, 1, unknownPlaceholder))
                    .replace("{second-value}", getPlayerValue(topPlayers, 1))
                    .replace("{third-name}", getPlayerName(topPlayers, 2, unknownPlaceholder))
                    .replace("{third-value}", getPlayerValue(topPlayers, 2))
                    .replace("{fourth-name}", getPlayerName(topPlayers, 3, unknownPlaceholder))
                    .replace("{fourth-value}", getPlayerValue(topPlayers, 3))
                    .replace("{fifth-name}", getPlayerName(topPlayers, 4, unknownPlaceholder))
                    .replace("{fifth-value}", getPlayerValue(topPlayers, 4));

            MessagesUtils.broadcast(formatted);
        }
    }

    private String getPlayerName(List<Map.Entry<UUID, Double>> topPlayers, int position, String unknownPlaceholder) {
        if (position < topPlayers.size()) {
            Player player = Bukkit.getPlayer(topPlayers.get(position).getKey());
            return player != null ? player.getName() : unknownPlaceholder;
        }
        return unknownPlaceholder;
    }

    private String getPlayerValue(List<Map.Entry<UUID, Double>> topPlayers, int position) {
        if (position < topPlayers.size()) {
            return String.format("%.2f", topPlayers.get(position).getValue());
        }
        return "0.00";
    }

    public ContainerEventRarity getEventRarity() {
        return (ContainerEventRarity) eventRarity;
    }

}