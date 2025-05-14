package ru.loper.suncontainer.config;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import ru.loper.suncore.api.config.CustomConfig;
import ru.loper.suncore.utils.Colorize;

import java.util.List;

@Getter
public class ConfigManager {
    private final CustomConfig openMenuConfig;
    private final CustomConfig animationMenuConfig;
    private final Plugin plugin;
    private List<String> openBroadcast;

    public ConfigManager(Plugin plugin) {
        plugin.saveDefaultConfig();
        this.plugin = plugin;
        openMenuConfig = new CustomConfig("menu/open_menu.yml", false, plugin);
        animationMenuConfig = new CustomConfig("menu/animation_menu.yml", false, plugin);
        loadValues();
    }

    public void reload() {
        plugin.reloadConfig();
        openMenuConfig.reloadConfig();
        animationMenuConfig.reloadConfig();
        loadValues();
    }

    private void loadValues(){
        openBroadcast = configMessages("messages.open_message");
    }

    public String configMessage(String path) {
        String message = plugin.getConfig().getString(path, "unknown");
        return Colorize.parse(message);
    }

    public List<String> configMessages(String path) {
        List<String> message = plugin.getConfig().getStringList(path);
        return Colorize.parse(message);
    }
}
