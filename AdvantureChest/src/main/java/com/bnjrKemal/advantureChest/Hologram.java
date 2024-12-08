package com.bnjrKemal.advantureChest;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    public static List<ArmorStand> armorStands = new ArrayList<>();

    public Hologram(Location baseLocation, ConfigurationSection configurationSection) {
        spawnHologram(baseLocation, configurationSection.getStringList("hologram"));
    }

    private void spawnHologram(Location baseLocation, List<String> lines) {
        World world = baseLocation.getWorld();
        if (world == null) return;

        double yOffset = 0.3;

        for (String line : lines) {
            Location lineLocation = baseLocation.clone().add(0, yOffset * armorStands.size() + 0.5, 0);
            ArmorStand armorStand = (ArmorStand) world.spawnEntity(lineLocation, EntityType.ARMOR_STAND);

            armorStand.customName(Component.text(line));
            armorStand.setCustomNameVisible(true);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setMarker(true);
            armorStand.setSmall(true);

            armorStands.add(armorStand);
        }
    }

    public void remove() {
        for (ArmorStand armorStand : armorStands) {
            if (armorStand != null && !armorStand.isDead()) {
                armorStand.remove();
            }
        }
    }
}
