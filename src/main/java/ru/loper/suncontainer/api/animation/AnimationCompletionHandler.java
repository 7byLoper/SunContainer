package ru.loper.suncontainer.api.animation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface AnimationCompletionHandler {
    /**
     * Вызывается при завершении анимации
     *
     * @param player        Игрок
     * @param rewardedItems Выданные предметы
     */
    void onAnimationComplete(Player player, List<ItemStack> rewardedItems);
}
