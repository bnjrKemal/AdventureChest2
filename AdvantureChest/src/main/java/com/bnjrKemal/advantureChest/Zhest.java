package com.bnjrKemal.advantureChest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.bnjrKemal.advantureChest.AdvantureChest.getInstance;

public class Zhest implements Listener { //Sonda bıraktığım clazz (31. satırda error var)

    // Sandık envanterlerini tutacak yapı
    private final Map<String, List<ItemStack>> savedInventories = new HashMap<>();

    // /adventurechest add <isim> komutu
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return false;
        if(!player.isOp()) return false;

        if (command.getName().equalsIgnoreCase("adventurechest")) {
            if (args.length < 2) return false;

            Block targetBlock = player.getTargetBlockExact(5);

            if (targetBlock != null && targetBlock.getType() == Material.CHEST) {
                Chest chest = (Chest) targetBlock.getState();
                Inventory chestInventory = chest.getInventory();

                if (args[0].equalsIgnoreCase("add")) {
                    String name = args[1];
                    List<ItemStack> items = Arrays.asList(chestInventory.getContents());

                    savedInventories.put(name, items);
                    saveInventoryToFile(name, items);
                    player.sendMessage("Sandık envanteri " + name + " olarak eklendi.");
                    return true;
                }
            }
        }
        return false;
    }

    // Sandık sağ tıklama eventi
    @EventHandler
    public void onPlayerRightClickChest(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            Chest chest = (Chest) block.getState();
            Inventory chestInventory = chest.getInventory();
            if (!savedInventories.isEmpty()) {
                Random random = new Random();
                List<ItemStack> randomItems = savedInventories.values()
                        .stream()
                        .skip(random.nextInt(savedInventories.size()))
                        .findFirst()
                        .orElse(new ArrayList<>());

                chestInventory.clear();

                for (int i = 0; i < randomItems.size(); i++) {
                    ItemStack item = randomItems.get(i);
                    if (item != null) {
                        Bukkit.getScheduler().runTaskLater(getInstance(), () -> {
                            chestInventory.addItem(item);
                            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
                        }, i * 10L); // 10 tick gecikme
                    }
                }
            }
        }
    }

    private void saveInventoryToFile(String name, List<ItemStack> items) {
        File file = new File(getInstance().getDataFolder() + "/chests/" + name + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            config.set("items." + i, item);
        }

        try {
            config.save(file);
        } catch (IOException ignored) {}
    }

    private List<ItemStack> loadInventoryFromFile(String name) {
        File file = new File(getInstance().getDataFolder() + "/chests/" + name + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<ItemStack> items = new ArrayList<>();
        for (String key : Objects.requireNonNull(config.getConfigurationSection("items")).getKeys(false)) {
            items.add(config.getItemStack("items." + key));
        }
        return items;
    }

}
