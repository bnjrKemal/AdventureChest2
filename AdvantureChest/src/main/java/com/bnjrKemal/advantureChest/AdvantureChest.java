package com.bnjrKemal.advantureChest;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static com.bnjrKemal.advantureChest.Hologram.armorStands;
import static com.bnjrKemal.advantureChest.Manage.manage;

public final class AdvantureChest extends JavaPlugin {

    private static AdvantureChest instance;

    @Override
    public void onEnable() {
        instance = this;

        new TimeSystem();

        Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class,
                new Listener() {},
                EventPriority.NORMAL,
                (listener, event) -> {

                    File file = new File(getDataFolder(), "chests/example.yml");
                    ConfigurationSection configuration = YamlConfiguration.loadConfiguration(file);

                    manage(configuration);
                    },
                this);
    }

    public static AdvantureChest getInstance() {
        return instance;
    }

    @Override
    public void onDisable(){
        for(ArmorStand stand : armorStands)
            stand.remove();
    }

}
