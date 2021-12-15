package net.badbird5907.whitelister.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.whitelister.Whitelister;
import net.badbird5907.whitelister.listeners.JDAListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class JDAManager {
    @Getter
    private static JDA jda;
    public static long whitelist, unwhitelist;
    public static Role permsRole,whitelistRole;
    @SneakyThrows
    public static void init(){
        JDABuilder builder = JDABuilder.createDefault(Whitelister.getInstance().getConfig().getString("token"));
        builder.addEventListeners(new JDAListener());
        jda = builder.build();
    }
    public static Guild getGuild(){
        return jda.getGuildById(Whitelister.getInstance().getConfig().getLong("server"));
    }
}
