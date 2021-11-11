package de.whsminecraft.ChunkSync;


import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Queue;

public class ChunkReplacer {
    private static ChunkReplacer instance;

    public static ChunkReplacer getInstance() {
        if (instance == null)
            instance = new ChunkReplacer(Plugin.getInstance().getConfig().getInt("client.block-replace-budget", 4096));
        return instance;
    }

    // Block updates per tick. Updates have to be performed on the main thread, thus small chunks are important.
    private int MAX_CHANGES;
    private Queue<Change> changes;

    private ChunkReplacer(int maxChanges) {
        instance = this;
        MAX_CHANGES = maxChanges;
        changes = new LinkedList<>();
    }


    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int changeBudget = MAX_CHANGES;

                while (changeBudget > 0 && !changes.isEmpty()) {
                    Change c = changes.poll();
                    c.location.getBlock().setBlockData(c.data);
                    changeBudget--;
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);
    }

    public void add(Change change) {
        changes.offer(change);
    }

    public static class Change {
        public Location location;
        public BlockData data;
        public Change(Location location, BlockData blockData) {
            this.location = location;
            this.data = blockData;
        }
    }
}
