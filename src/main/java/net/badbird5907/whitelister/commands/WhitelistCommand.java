package net.badbird5907.whitelister.commands;

import net.badbird5907.blib.command.BaseCommand;
import net.badbird5907.blib.command.Command;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.util.CC;
import net.badbird5907.whitelister.Whitelister;
import net.badbird5907.whitelister.object.WhitelistedUser;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class WhitelistCommand extends BaseCommand {
    @Command(name = "whitelist",usage = "<player> [discordid]", aliases = {"wl"}, permission = "whitelist.command")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0) {
            return CommandResult.INVALID_ARGS;
        }
        String target = null;
        long discordId = -1;
        if (args.length >= 1) {
            target = args[0];
        }
        if (args.length == 2){
            try {
                discordId = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                return CommandResult.INVALID_ARGS;
            }
        }
        OfflinePlayer player = sender.getServer().getOfflinePlayer(target);
        WhitelistedUser user = Whitelister.getInstance().getStorageProvider().getWhitelistedUser(player);
        if (user == null) {
            user = new WhitelistedUser(player, discordId);
        }
        user.setWhitelisted(true);
        sender.sendMessage(CC.GREEN + "Whitelisted " + player.getName());
        user.getMember().thenAcceptAsync(m ->{
            if (m != null) {
                sender.sendMessage(CC.GREEN + "Gave " + m.getEffectiveName() + " the whitelist role");
            }
        });
        return CommandResult.SUCCESS;
    }
    @Command(name = "unwhitelist",usage = "<player>", aliases = {"unwl"}, permission = "whitelist.command")
    public CommandResult exec(Sender sender, String[] args) {
        if (args.length == 0) {
            return CommandResult.INVALID_ARGS;
        }
        String target = null;
        if (args.length >= 1) {
            target = args[0];
        }
        OfflinePlayer player = sender.getServer().getOfflinePlayer(target);
        WhitelistedUser user = Whitelister.getInstance().getStorageProvider().getWhitelistedUser(player);
        if (user == null) {
            sender.sendMessage(CC.RED + "User is not whitelisted");
        }
        user.setWhitelisted(false);
        sender.sendMessage(CC.GREEN + "Un-whitelisted " + player.getName());
        user.getMember().thenAcceptAsync(m ->{
            if (m != null) {
                sender.sendMessage(CC.GREEN + "Took away the whitelist role from " + m.getEffectiveName());
            }
        });
        return CommandResult.SUCCESS;
    }
}
