package net.badbird5907.whitelister.storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import net.badbird5907.whitelister.Whitelister;
import net.badbird5907.whitelister.object.WhitelistedUser;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class FileStorageProvider implements StorageProvider{ //haha shitty code go brrrrr
    private File file;
    @Override
    public void init() {
        file = new File(Whitelister.getInstance().getDataFolder() + "/data-schema.json");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*
    schema:
{
  "whitelisted": [
    {
      "userId": 456951144166457345,
      "mcName": "Badbird5907",
      "uuid": "5bd217f6-b89a-4064-a7f9-11733e8baafa"
    }
  ]
}
using gson
     */
    @Override
    public long[] getWhitelistedIds() {
        JsonObject fullData = getData();
        JsonArray whitelisted = fullData.getAsJsonArray("whitelisted");
        long[] ids = new long[whitelisted.size()];
        for (int i = 0; i < whitelisted.size(); i++) {
            JsonObject object = whitelisted.get(i).getAsJsonObject();
            ids[i] = object.get("userId").getAsLong();
        }
        return ids;
    }

    @Override
    public int getTotalWhitelisted() {
        JsonObject fullData = getData();
        JsonArray whitelisted = fullData.getAsJsonArray("whitelisted");
        return whitelisted.size();
    }

    @Override
    public void whitelistMember(long userId, UUID uuid, String mcName) {
        JsonObject data = new JsonObject();
        data.addProperty("userId",userId);
        data.addProperty("mcName",mcName);
        data.addProperty("uuid",uuid.toString());
        JsonObject fullData = getData();
        JsonArray whitelisted = fullData.getAsJsonArray("whitelisted");
        whitelisted.add(data);
        fullData.remove("whitelisted");
        fullData.add("whitelisted",whitelisted);
        save(fullData);
    }

    @Override
    public void unWhitelistMember(long id) {
        JsonObject fullData = getData();
        JsonArray whitelisted = fullData.getAsJsonArray("whitelisted");
        for (JsonElement element : whitelisted) {
            JsonObject object = element.getAsJsonObject();
            if (object.get("userId").getAsLong() == id){
                whitelisted.remove(element);
                break;
            }
        }
        fullData.remove("whitelisted");
        fullData.add("whitelisted",whitelisted);
        save(fullData);
    }

    @Override
    public void unWhitelistMember(UUID id) {
        JsonObject fullData = getData();
        JsonArray whitelisted = fullData.getAsJsonArray("whitelisted");
        for (JsonElement element : whitelisted) {
            JsonObject object = element.getAsJsonObject();
            if (object.get("uuid").getAsString() == id.toString()){
                whitelisted.remove(element);
                break;
            }
        }
        fullData.remove("whitelisted");
        fullData.add("whitelisted",whitelisted);
        save(fullData);
    }

    @Override
    public void unWhitelistMember(String mcName) {
        JsonObject fullData = getData();
        JsonArray whitelisted = fullData.getAsJsonArray("whitelisted");
        for (JsonElement element : whitelisted) {
            JsonObject object = element.getAsJsonObject();
            if (object.get("mcName").getAsString().equalsIgnoreCase(mcName)){
                whitelisted.remove(element);
                break;
            }
        }
        fullData.remove("whitelisted");
        fullData.add("whitelisted",whitelisted);
        save(fullData);
    }

    @Override
    public WhitelistedUser getWhitelistedUser(OfflinePlayer player) {
        JsonObject fullData = getData();
        JsonArray whitelisted = fullData.getAsJsonArray("whitelisted");
        for (JsonElement element : whitelisted) {
            JsonObject object = element.getAsJsonObject();
            if (object.get("uuid").getAsString() == player.getUniqueId().toString()){
                WhitelistedUser user =  Whitelister.getGson().fromJson(object,WhitelistedUser.class);
                user.onLoad();
                return user;
            }
        }
        return null;
    }

    @Override
    public void save(WhitelistedUser user) {
        JsonObject data = Whitelister.getGson().toJsonTree(user).getAsJsonObject();
        JsonObject fullData = getData();
        JsonArray whitelisted = fullData.getAsJsonArray("whitelisted");
        for (JsonElement element : whitelisted.deepCopy()) {
            JsonObject object = element.getAsJsonObject();
            if (object.get("uuid").getAsString().equalsIgnoreCase(user.getUuid().toString())){
                whitelisted.remove(element);
            }
        }
        whitelisted.add(data);
        fullData.remove("whitelisted");
        fullData.add("whitelisted",whitelisted);
        save(fullData);
    }

    public void save(JsonObject data){
        try {
            Files.write(file.toPath(), Whitelister.getGson().toJson(data).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SneakyThrows
    public JsonObject getData(){
        //read all lines of file
        String json = String.join("\n", Files.readAllLines(file.toPath()));
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
