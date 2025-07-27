package ru.loper.suncontainer.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContainerEventResults {
    private final Map<UUID, Double> playerResults = new HashMap<>();

    public void addResult(UUID playerId, double sapphires) {
        playerResults.merge(playerId, sapphires, Double::sum);
    }

    public Map<UUID, Double> getResults() {
        return new HashMap<>(playerResults);
    }

    public void clearResults() {
        playerResults.clear();
    }
}