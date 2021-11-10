package de.whsminecraft.ChunkSync;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChunkClient {
    private String ip;
    private int port;

    public ChunkClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void requestChunk(Chunk target) {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(ip, port);
            OutputStream out = clientSocket.getOutputStream();
            out.write(String.format("get-chunk %d %d\n", target.getX(), target.getZ()).getBytes(StandardCharsets.UTF_8));
            try {
                clientSocket.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Plugin.getInstance().getLogger().info("Reply");
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ChunkReplacer replacer = ChunkReplacer.getInstance();
            for (int y = 0; y < 256; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        try {
                            String data = in.readLine();
                            BlockData blockData = Bukkit.createBlockData(data);
                            Location location = new Location(
                                    target.getWorld(),
                                    x + target.getX() * 16,
                                    y,
                                    z + target.getZ() * 16
                            );
                            replacer.add(new ChunkReplacer.Change(location, blockData));
                        } catch (IOException | IllegalArgumentException e) {}
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
