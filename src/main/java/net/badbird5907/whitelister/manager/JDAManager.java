package net.badbird5907.whitelister.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.whitelister.Whitelister;
import net.badbird5907.whitelister.listeners.JDAListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class JDAManager {
    @Getter
    private static JDA jda;
    public static long whitelist, unwhitelist;
    public static Role permsRole,whitelistRole;
    @SneakyThrows
    public static void init(){
        JDABuilder builder = JDABuilder.createDefault(Whitelister.getInstance().getConfig().getString("token"), GatewayIntent.GUILD_MEMBERS);
        builder.addEventListeners(new JDAListener());
        jda = builder.build();

    }
    public static Guild getGuild(){
        return jda.getGuildById(Whitelister.getInstance().getConfig().getLong("server"));
    }
}
