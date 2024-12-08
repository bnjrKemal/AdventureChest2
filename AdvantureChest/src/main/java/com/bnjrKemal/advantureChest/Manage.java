package com.bnjrKemal.advantureChest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

import static com.bnjrKemal.advantureChest.RandomLocationGenerator.getRandomLocation;

public class Manage {

    public static void manage(ConfigurationSection configuration){

        String worldStr = configuration.getString("world"); if(worldStr == null) return;
        World world = Bukkit.getWorld(worldStr); if(world == null) return;

        Location location = getRandomLocation(world);

        List<String> announce = configuration.getStringList("announce");
        StringBuilder combinedMessage = new StringBuilder();
        for (String a : announce) {
            combinedMessage.append(a
                            .replace("{location}",
                                    "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")")
                            .replace("{world}", location.getWorld().getName()))
                    .append("\n");
        }

        location.getBlock().setType(Material.CHEST);
        new Hologram(location, configuration);

        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(combinedMessage.toString());
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(component));
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(location));
    }

}
