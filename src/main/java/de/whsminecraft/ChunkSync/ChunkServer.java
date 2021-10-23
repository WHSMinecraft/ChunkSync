package de.whsminecraft.ChunkSync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChunkServer {
    private ServerSocket ss;
    private Socket cs;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) throws IOException {
        ss = new ServerSocket(port);
        while (true) {
            cs = ss.accept();
            out = new PrintWriter(cs.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            String request = in.readLine();
            Plugin.getInstance().getLogger().info("Request: " + request);
            if ("hello server".equals(request)) {
                out.println("hello client");
            }
            else {
                out.println("unrecognised request");
            }

        }
    }

    public void stop() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.close();

        try {
            cs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
