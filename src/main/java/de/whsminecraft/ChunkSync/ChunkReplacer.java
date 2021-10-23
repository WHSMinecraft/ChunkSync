package de.whsminecraft.ChunkSync;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Block;

public class ChunkReplacer {
    public boolean replace(Chunk to, ChunkSnapshot from) {
        int x1 = 16*to.getX();
        int z1 = 16*to.getZ();

        int x2 = 16*from.getX();
        int z2 = 16*from.getZ();

        for (int xo = 0; xo < 16; xo++) {
            for (int zo = 0; zo < 16; zo++) {
                for (int y = 0; y < 256; y++) {
                    Block b1 = to.getWorld().getBlockAt(x1+xo,y,z1+zo);
                    Block b2 = to.getWorld().getBlockAt(x2+xo,y,z2+zo);
                    b1.setType(b2.getType(), true);
                }
            }
        }
        return true;
    }
}
