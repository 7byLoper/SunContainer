package ru.loper.suncontainer.api.animation.impl;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public abstract class BaseContainerAnimation {
    @Getter
    protected Player currentPlayer;
    protected Inventory animationInventory;

    /**
     * Запускает анимацию
     *
     * @param player Игрок
     * @param slots  Слоты для анимации
     * @param items  Предметы для анимации
     */
    public abstract void playAnimation(Player player, List<Integer> slots, List<Object> items);

    /**
     * Останавливает анимацию
     */
    public abstract void stopAnimation();

    /**
     * Обновляет инвентарь анимации
     *
     * @param inventory Новый инвентарь
     */
    public void setInventory(Inventory inventory) {
        this.animationInventory = inventory;
    }
}