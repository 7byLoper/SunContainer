package ru.loper.suncontainer.api.animation;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ContainerAnimation {
    /**
     * Запускает анимацию открытия контейнера
     *
     * @param player Игрок, открывающий контейнер
     * @param slots  Слоты, в которых будет происходить анимация
     * @param items  Предметы для анимации (может быть список списков для разных этапов)
     */
    void playAnimation(Player player, List<Integer> slots, List<ItemStack> items);

    /**
     * Останавливает анимацию досрочно
     */
    void stopAnimation();

    /**
     * Устанавливает обработчик завершения анимации
     *
     * @param handler Обработчик
     */
    void setCompletionHandler(AnimationCompletionHandler handler);

    /**
     * Устанавливает обработчик обновления слота
     *
     * @param handler Обработчик
     */
    void setSlotUpdateHandler(SlotUpdateHandler handler);
}
