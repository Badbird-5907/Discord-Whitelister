package net.badbird5907.whitelister.storage;

import net.badbird5907.whitelister.object.WhitelistedUser;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface StorageProvider {
    void init();
    int getTotalWhitelisted();
    long[] getWhitelistedIds();
    void whitelistMember(long userId, UUID uuid,String mcName);
    void unWhitelistMember(long id);
    void unWhitelistMember(UUID id);
    void unWhitelistMember(String mcName);
    WhitelistedUser getWhitelistedUser(OfflinePlayer player);
    WhitelistedUser getWhitelistedUser(long discordId);
    void save(WhitelistedUser user);
}
