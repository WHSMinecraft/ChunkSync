package de.whsminecraft.ChunkSync;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;


public class ChunkSelector implements Listener {
    private ChunkClient chunkClient;
    private Material selectTool;

    public ChunkSelector(ChunkClient client, Material selectTool) {
        this.chunkClient = client;
        this.selectTool = selectTool;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRightClick(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        ItemStack heldItem = e.getPlayer().getInventory().getItemInMainHand();
        if (heldItem == null || heldItem.getType() != selectTool)
            return;


        Block target;

        if (action == Action.RIGHT_CLICK_BLOCK) {
            target = e.getClickedBlock();
        } else {
            target = getTargetBlock(e.getPlayer(), 100);
        }

        if (target == null)
            return;

        Chunk targetChunk = target.getChunk();
        Plugin.getInstance().getLogger().info("Selected chunk: (" + targetChunk.getX() + ", " + targetChunk.getZ() + ") in " + targetChunk.getWorld().getName() + " by " + e.getPlayer().getDisplayName());

        new Thread(
                () -> chunkClient.requestChunk(targetChunk),
                "Chunk request at " + targetChunk.getX() + "," + targetChunk.getZ() + " in " + targetChunk.getWorld().getName()
        ).start();
    }

    static Block getTargetBlock(Player player, int maxDistance) throws IllegalStateException
    {
        Location eye = player.getEyeLocation();
        Material eyeMaterial = eye.getBlock().getType();
        boolean passThroughWater = (eyeMaterial == Material.WATER);
        BlockIterator iterator = new BlockIterator(player.getLocation(), player.getEyeHeight(), maxDistance);
        Block result = player.getLocation().getBlock().getRelative(BlockFace.UP);
        while (iterator.hasNext())
        {
            result = iterator.next();
            Material type = result.getType();
            if (type != Material.AIR &&
                    (!passThroughWater || type != Material.WATER) &&
                    type != Material.GRASS &&
                    type != Material.SNOW) return result;
        }

        return result;
    }
}
