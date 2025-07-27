package ru.loper.suncontainer.api.storage;

public interface ContainersStorage {
    void createTable();

    int getContainers(String player);

    void setContainers(String player, int containers);

    void takeContainers(String player, int containers);

    void addContainers(String player, int containers);
}
