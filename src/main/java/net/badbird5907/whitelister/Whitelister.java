package net.badbird5907.whitelister;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.blib.bLib;
import net.badbird5907.whitelister.manager.JDAManager;
import net.badbird5907.whitelister.storage.FileStorageProvider;
import net.badbird5907.whitelister.storage.StorageProvider;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;

public final class Whitelister extends JavaPlugin {

    @Getter
    private static Whitelister instance;
    @Getter
    private static OkHttpClient httpClient;
    @Getter
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private StorageProvider storageProvider;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        bLib.create(this);
        setupConfig();
        JDAManager.init();
        httpClient = new OkHttpClient();
        storageProvider = new FileStorageProvider();
        storageProvider.init();
    }

    @SneakyThrows
    private void setupConfig(){
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        File configFile = new File(getDataFolder() + "/config.yml");
        if (!configFile.exists())
            Files.copy(getResource("config.yml"),configFile.toPath());
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
