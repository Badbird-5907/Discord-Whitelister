package net.badbird5907.whitelister.storage;

import net.badbird5907.whitelister.object.WhitelistedUser;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface StorageProvider {
    void init();
    int getTotalWhitelisted();
    long[] getWhitelistedIds();
    WhitelistedUser getWhitelistedUser(OfflinePlayer player);
    WhitelistedUser getWhitelistedUser(long discordId);
    WhitelistedUser[] getWhitelistedUsers();
    void save(WhitelistedUser user);
}
