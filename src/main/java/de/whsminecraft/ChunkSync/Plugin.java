package de.whsminecraft.ChunkSync;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Plugin extends JavaPlugin {
    private static Plugin instance;
    private static ChunkServer csInstance;
    private static ChunkClient ccInstance;

    public static Plugin getInstance() {
        return instance;
    }

    public static ChunkServer getServerInstance() {
        return csInstance;
    }

    public static ChunkClient getClientInstance() {
        return ccInstance;
    }

    public Plugin() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Commands cmds = new Commands();

        if ("server".equals(getConfig().getString("mode"))) {
            getLogger().info("Starting in server mode");
            csInstance = new ChunkServer();
            try {
                csInstance.start(1337);
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().severe("could not start up server");
                return;
            }
        } else { // client
            getLogger().info("Starting in client mode");
            ccInstance = new ChunkClient();
            try {
                ccInstance.startConnection("localhost", 1337);
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().severe("could not connect to server");
                return;
            }
            String reply = ccInstance.sendMessage("hello server");
            getServer().getPluginManager().registerEvents(new ChunkSelector(), this);
        }

        getCommand("chunksync").setExecutor(cmds);


        getLogger().info("Plugin was successfully enabled.");
    }

    @Override
    public void onDisable() {
        if (ccInstance != null) {
            ccInstance.stopConnection();
        }

        if (csInstance != null) {
            csInstance.stop();
        }

        getLogger().info("Plugin was successfully disabled.");
    }
}
