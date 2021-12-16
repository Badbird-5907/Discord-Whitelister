package net.badbird5907.whitelister.listeners;

import net.badbird5907.blib.util.Logger;
import net.badbird5907.whitelister.Whitelister;
import net.badbird5907.whitelister.manager.JDAManager;
import net.badbird5907.whitelister.object.WhitelistedUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import static net.badbird5907.whitelister.manager.JDAManager.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class JDAListener extends ListenerAdapter {
    public static Pattern uuidRegex = Pattern.compile("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b\n");
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDAManager.getJda().getGuildById(Whitelister.getInstance().getConfig().getLong("server"))
                .upsertCommand("whitelist","whitelist a player")
                .addOption(OptionType.STRING, "player", "The player to whitelist",true)
                .addOption(OptionType.USER,"discord","discord",false)
                .addOption(OptionType.BOOLEAN,"silent","Whether the command is silent",false).queue(command ->{
                    whitelist = command.getIdLong();
                });
        JDAManager.getJda().getGuildById(Whitelister.getInstance().getConfig().getLong("server"))
                .upsertCommand("unwhitelist","unwhitelist a player")
                .addOption(OptionType.STRING, "player", "The player to unwhitelist",true)
                .addOption(OptionType.USER,"discord","discord",false)
                .addOption(OptionType.BOOLEAN,"silent","Whether the command is silent",false).queue(command ->{
                    unwhitelist = command.getIdLong();
                });
        JDAManager.getJda().getGuildById(Whitelister.getInstance().getConfig().getLong("server"))
                .upsertCommand("listwhitelist","list whitelisted players")
                .addOption(OptionType.BOOLEAN,"ignorecache","Ignores the cached player name and (potentially) fetches the name from mojang API",false)
                .addOption(OptionType.BOOLEAN,"silent","Whether the command is silent",false).queue(command ->{
                    list = command.getIdLong();
                });
        permsRole = JDAManager.getJda().getGuildById(Whitelister.getInstance().getConfig().getLong("server")).getRoleById(Whitelister.getInstance().getConfig().getLong("perms"));
        whitelistRole = JDAManager.getJda().getGuildById(Whitelister.getInstance().getConfig().getLong("server")).getRoleById(Whitelister.getInstance().getConfig().getLong("whitelist-role"));
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild().getIdLong() == Whitelister.getInstance().getConfig().getLong("server")) {
            if (!event.getMember().getRoles().contains(permsRole)){
                event.reply("You dont have perms L + bozo + skill issue + cope").queue();
                return;
            }
            if (event.getCommandIdLong() == whitelist){
                String player = event.getOption("player").getAsString();
                OptionMapping memberMapping = event.getOption("discord");
                OptionMapping silentMapping = event.getOption("silent");
                Member member = null;
                boolean silent = false;
                if (memberMapping != null) {
                    member = memberMapping.getAsMember();
                }
                if (silentMapping != null) {
                    silent = silentMapping.getAsBoolean();
                }
                OfflinePlayer offlinePlayer;
                if (uuidRegex.matcher(player).matches()){
                    offlinePlayer = Whitelister.getInstance().getServer().getOfflinePlayer(UUID.fromString(player));
                }else{
                    offlinePlayer = Whitelister.getInstance().getServer().getOfflinePlayer(player);
                }
                if (offlinePlayer.isWhitelisted()){
                    event.reply("Player is already whitelisted idiot").queue();
                    return;
                }
                WhitelistedUser user = Whitelister.getInstance().getStorageProvider().getWhitelistedUser(offlinePlayer);
                if (user == null) {
                    user = new WhitelistedUser(offlinePlayer,(member == null ? -1 : member.getIdLong()));
                }
                if (member != null)
                    user.setUserId(member.getIdLong());
                user.setWhitelisted(true);
                user.save();
                EmbedBuilder builder = new EmbedBuilder().setTitle("Whitelisted").addField(new MessageEmbed.Field("Name",offlinePlayer.getName(),false)).addField(new MessageEmbed.Field("UUID",offlinePlayer.getUniqueId().toString(),false));
                if (member != null){
                    builder.addField(new MessageEmbed.Field("Discord",member.getAsMention(),false));
                }
                event.replyEmbeds(builder.build()).setEphemeral(silent).queue();
            } else if (event.getCommandIdLong() == unwhitelist){
                String player = event.getOption("player").getAsString();
                Member member = null;
                OptionMapping memberMapping = event.getOption("discord");
                OptionMapping silentMapping = event.getOption("silent");
                boolean silent = false;
                if (memberMapping != null) {
                    member = memberMapping.getAsMember();
                }
                if (silentMapping != null) {
                    silent = silentMapping.getAsBoolean();
                }
                OfflinePlayer offlinePlayer;
                if (uuidRegex.matcher(player).matches()){
                    offlinePlayer = Whitelister.getInstance().getServer().getOfflinePlayer(UUID.fromString(player));
                }
                else{
                    offlinePlayer = Whitelister.getInstance().getServer().getOfflinePlayer(player);
                }
                if (!offlinePlayer.isWhitelisted()){
                    event.reply("Player is not whitelisted idiot").queue();
                    return;
                }
                WhitelistedUser user = Whitelister.getInstance().getStorageProvider().getWhitelistedUser(offlinePlayer);
                if (user == null) {
                    user = new WhitelistedUser(offlinePlayer,(member == null ? -1 : member.getIdLong()));
                }
                if (member != null)
                    user.setUserId(member.getIdLong());
                user.setWhitelisted(false);
                user.save();
                EmbedBuilder builder = new EmbedBuilder().setTitle("Un-Whitelist").addField(new MessageEmbed.Field("Name",offlinePlayer.getName(),false)).addField(new MessageEmbed.Field("UUID",offlinePlayer.getUniqueId().toString(),false));
                if (member != null){
                    builder.addField(new MessageEmbed.Field("Discord",member.getAsMention(),false));
                }
                event.replyEmbeds(builder.build()).setEphemeral(silent).queue();
            }else if (event.getCommandIdLong() == list){
                OptionMapping silentMapping = event.getOption("silent");
                boolean silent = false;
                if (silentMapping != null) {
                    silent = silentMapping.getAsBoolean();
                }
                OptionMapping ignoreCacheMapping = event.getOption("ignorecache");
                boolean ignoreCache = false;
                if (ignoreCacheMapping != null) {
                    ignoreCache = ignoreCacheMapping.getAsBoolean();
                }
                List<WhitelistedUser> users = Arrays.asList(Whitelister.getInstance().getStorageProvider().getWhitelistedUsers());
                List<MessageEmbed> embeds = new ArrayList<>();
                for (WhitelistedUser user : users) {
                    EmbedBuilder builder = new EmbedBuilder().setTitle("Whitelisted")
                            .addField(new MessageEmbed.Field("Name" + (!ignoreCache ? " (cached)" : ""),(ignoreCache ? user.getName() : user.getCachedName()),false))
                            .addField(new MessageEmbed.Field("UUID",user.getOfflinePlayer().getUniqueId().toString(),false));
                    if (user.getUserId() != -1){
                        builder.addField(new MessageEmbed.Field("Discord","<@" + user.getUserId() + ">",false));
                    }
                    embeds.add(builder.build());
                }
                event.replyEmbeds(embeds).setEphemeral(silent).queue();
            }
            else {
                event.reply("ur dumb lol").queue();
            }
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getGuild().getIdLong() == Whitelister.getInstance().getConfig().getLong("guild")) {
            WhitelistedUser user = Whitelister.getInstance().getStorageProvider().getWhitelistedUser(event.getUser().getIdLong());
            if (user != null && user.getOfflinePlayer().isWhitelisted()) {
                Logger.debug("Re-adding the whitelist role to " + event.getUser().getName() + " because they joined the server");
                event.getGuild().addRoleToMember(event.getMember(), whitelistRole).queue();
            }
        }
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {

    }
}
