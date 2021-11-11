package de.whsminecraft.ChunkSync;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.data.BlockData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChunkServer implements Runnable {
    private int port;

    public ChunkServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket ss;
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            Socket s;
            try {
                s = ss.accept();
                new Thread(new ChunkHandler(s)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class ChunkHandler implements Runnable {
        private Socket s;
        public ChunkHandler(Socket socket) {
            s = socket;
        }
        @Override
        public void run() {
            try {
                String request = new BufferedReader(new InputStreamReader(s.getInputStream())).readLine();
                String[] args = request.split(" ");
                int cx, cz;
                String worldName;
                if (args.length != 4 || !"get-chunk".equals(args[0])) {
                    Plugin.getInstance().getLogger().info("Received invalid request: " + request);
                    return;
                }

                cx = Integer.parseInt(args[1]);
                cz = Integer.parseInt(args[2]);
                worldName = args[3];

                Plugin.getInstance().getLogger().info("Chunk requested: (" + cx + ", " + cz + ") in " + worldName);

                ChunkSnapshot chunk = Bukkit.getWorld(worldName).getChunkAt(cx, cz).getChunkSnapshot();
                PrintWriter out = new PrintWriter(new BufferedOutputStream(s.getOutputStream()));

                for (int y = 0; y < 256; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            BlockData b = chunk.getBlockData(x, y, z);
                            String data = b.getAsString();
                            out.println(data);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
