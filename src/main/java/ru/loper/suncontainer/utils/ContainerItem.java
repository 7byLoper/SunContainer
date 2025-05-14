package ru.loper.suncontainer.utils;

import org.bukkit.inventory.ItemStack;

public record ContainerItem(String name, ItemRarity rarity, double price, ItemStack itemStack) { }