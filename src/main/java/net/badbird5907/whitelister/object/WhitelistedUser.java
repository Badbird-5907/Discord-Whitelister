package net.badbird5907.whitelister.object;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.whitelister.Whitelister;
import net.badbird5907.whitelister.manager.JDAManager;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class WhitelistedUser {
    @SerializedName("userId")
    private long userId;
    @SerializedName("uuid")
    private UUID uuid;
    @SerializedName("mcName")
    private String name;

    public WhitelistedUser(OfflinePlayer offlinePlayer,long id){
        uuid = offlinePlayer.getUniqueId();
        userId = id;
        name = offlinePlayer.getName();
    }
    public WhitelistedUser(long id,UUID uuid,String name){
        this.userId = id;
        this.uuid = uuid;
        this.name = name;
    }

    public void onLoad(){
        name = getOfflinePlayer().getName();
    }
    public void setWhitelisted(boolean whitelist){
        if(whitelist){
            getOfflinePlayer().setWhitelisted(true);
            if (userId < 1)
                return;
            getMember().thenAcceptAsync((m)-> {
                JDAManager.getGuild().addRoleToMember(m,JDAManager.whitelistRole).queue();
            });
        }else{
            getOfflinePlayer().setWhitelisted(false);
            if (userId < 1)
                return;
            getMember().thenAcceptAsync((m)-> {
                JDAManager.getGuild().removeRoleFromMember(m,JDAManager.whitelistRole).queue();
            });
        }
    }
    public CompletableFuture<Member> getMember(){
        CompletableFuture<Member> member = new CompletableFuture<>();
        if (userId == -1 || userId == 0) {
            return member;
        }
        JDAManager.getGuild().retrieveMemberById(userId).queue(m ->{
            if (m == null)
                return;
            member.complete(m);
        });
        return member;
    }

    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(uuid);
    }
    public String getName(){
        return getOfflinePlayer().getName();
    }
    public String getCachedName(){
        return name;
    }
    public void save(){
        Whitelister.getInstance().getStorageProvider().save(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhitelistedUser user = (WhitelistedUser) o;
        return userId == user.userId && Objects.equals(uuid, user.uuid) && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, uuid, name);
    }
}
