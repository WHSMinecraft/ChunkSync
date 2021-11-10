package de.whsminecraft.ChunkSync;


import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.Queue;

public class ChunkReplacer {
    private static ChunkReplacer instance;

    public static ChunkReplacer getInstance() {
        if (instance == null)
            instance = new ChunkReplacer();
        return instance;
    }

    private Queue<Change> changes;

    private ChunkReplacer() {
        instance = this;
        changes = new LinkedList<>();
    }


    public void start() {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                int maxChanges = 4096;

                while (maxChanges > 0 && !changes.isEmpty()) {
                    Change c = changes.poll();
                    c.location.getBlock().setBlockData(c.data);
                    Plugin.getInstance().getLogger().info("" + c.location + c.data);
                    maxChanges--;
                }
                if (maxChanges != 4096)
                    Plugin.getInstance().getLogger().info("Processed " + (4096 - maxChanges) + " changes");
            }
        }.runTaskTimer(Plugin.getInstance(), 0, 1);

        Plugin.getInstance().getLogger().info("Started replacer task with id " + task.getTaskId());
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
