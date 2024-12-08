package com.bnjrKemal.advantureChest;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;

import java.util.Random;

public class RandomLocationGenerator {

    /**
     * Gives a random location in the specified world from loaded chunks
     * until it returns a solid block.
     */
    public static Location getRandomLocation(World world) {
        Chunk[] loadedChunks = world.getLoadedChunks();

        if (loadedChunks.length == 0) {
            return null;
        }
        Random random = new Random();
        Chunk randomChunk = null;
        WorldBorder worldBorder = world.getWorldBorder();
        double borderSize = worldBorder.getSize() / 2;

        Block block = null;

        while (randomChunk == null || block == null || !block.isSolid()) {
            randomChunk = loadedChunks[random.nextInt(loadedChunks.length)];
            int x = randomChunk.getX() * 16 + random.nextInt(16); // Random X within chunk
            int z = randomChunk.getZ() * 16 + random.nextInt(16); // Random Z within chunk
            int y = world.getHighestBlockYAt(x, z); // Get the highest block's Y
            block = new Location(world, x, y, z).getBlock();

            // Ensure the location is within the world border
            if (Math.abs(x) > borderSize || Math.abs(z) > borderSize) {
                block = null; // Out of world border, reset and try again
            }
        }

        return block.getLocation().add(0, 1, 0).toCenterLocation(); // Adjust the location to be just above the block
    }
}
