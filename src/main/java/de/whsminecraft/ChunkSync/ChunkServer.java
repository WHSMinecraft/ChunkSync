package de.whsminecraft.ChunkSync;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.data.BlockData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChunkServer implements Runnable {
    private int port;
    private ServerSocket ss;

    public ChunkServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            Socket s;
            try {
                s = ss.accept();
                Plugin.getInstance().getLogger().info("New request: " + s.getInetAddress().getHostName() + ":" + s.getPort());
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
                if (args.length != 3 || !"get-chunk".equals(args[0])) {
                    Plugin.getInstance().getLogger().info("Received malformed request: " + request);
                    return;
                }

                cx = Integer.parseInt(args[1]);
                cz = Integer.parseInt(args[2]);

                Plugin.getInstance().getLogger().info("Chunk requested: (" + cx + ", " + cz + ")");

                ChunkSnapshot chunk = Bukkit.getWorld("world").getChunkAt(cx, cz).getChunkSnapshot();
                PrintWriter out = new PrintWriter(new BufferedOutputStream(s.getOutputStream()));

                for (int y = 0; y < 256; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            BlockData b = chunk.getBlockData(x, y, z);
                            String data = b.getAsString();
                            out.println(data);
                            System.out.println("" + x + "," + y + "," + z + ": " + data);
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
