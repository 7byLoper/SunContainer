package ru.loper.suncontainer.api.animation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SlotUpdateHandler {
    /**
     * Вызывается при обновлении слота
     *
     * @param player Игрок
     * @param slot   Обновляемый слот
     * @param item   Новый предмет в слоте
     * @param step   Текущий шаг анимации
     */
    void onSlotUpdate(Player player, int slot, ItemStack item, int step);
}