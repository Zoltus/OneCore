package io.github.zoltus.onecore.player.teleporting;


import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.User;
import io.github.zoltus.onecore.utils.ChatUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Request {

    private static final OneCore plugin = OneCore.getPlugin();

    public static Request getLatest(User accepter) {
        List<Request> requests = accepter.getRequests();
        return requests.isEmpty() ? null : requests.get(requests.size() - 1);
    }

    public static Request get(User sender, User accepter) {
        return accepter
                .getRequests()
                .stream()
                .filter(request -> request.getSender() == sender && request.getAccepter() == accepter)
                .findFirst()
                .orElse(null);
    }

    public enum TeleportType {
        TPA, TPHERE
    }

    private final TeleportType type;
    @Getter
    private final User sender; //teleporter, target
    @Getter
    private final User accepter;
    private final List<Request> requests;
    private final BukkitTask expiryTask;

    private Request(User sender, User accepter, TeleportType type) {
        this.type = type;
        this.sender = sender;
        this.accepter = accepter;
        this.expiryTask = expireTimer();
        this.requests = accepter.getRequests();
        requests.add(this);
        sendChat();
    }

    private BukkitTask expireTimer() {
        return Bukkit.getScheduler().runTaskLater(plugin, () -> {
            requests.remove(this);
            sender.sendMessage(TP_EXPIRED.getString());
        }, 20L * Config.TELEPORT_EXPIRE.getInt());
    }

    public static boolean hasRequest(User sender, User accepter) {
        return accepter.getRequests().stream()
                .anyMatch(req -> req.getSender() == sender && req.getAccepter() == accepter);
    }

    public static void send(User sender, User accepter, TeleportType type) {
        if (sender == accepter) { //Cant self teleport
            TP_CANT_SELF_TELEPORT.send(sender);
        } else if (!accepter.isTpEnabled()) { //Cant teleport if tp toggled
            TP_TOGGLE_IS_OFF.send(sender, PLAYER_PH, accepter.getName());
        } else if (hasRequest(sender, accepter)) {
            TP_YOU_ALREADY_SENT_REQUEST.send(sender, PLAYER_PH, accepter.getName());
        } else {
            new Request(sender, accepter, type);
        }
    }

    public void accept() {
        User teleporter = type == TeleportType.TPA ? sender : accepter;
        User target = type == TeleportType.TPA ? accepter : sender;
        TP_ACCEPTED.send(sender, PLAYER_PH, accepter.getName());
        TP_YOU_ACCEPTED.send(accepter, PLAYER_PH, sender.getName());
        removeRequest();
        teleporter.teleport(target);
    }

    public void deny() {
        removeRequest();
        TP_DENIED.send(sender, PLAYER_PH, accepter.getName());
        TP_YOU_DENIED.send(accepter, PLAYER_PH, accepter.getName());
    }

    private void removeRequest() {
        requests.remove(this);
        expiryTask.cancel();
    }

    private void sendChat() {
        sender.sendMessage(TP_SENT.replace(PLAYER_PH, accepter.getName()));
        TP_RECEIVED.send(accepter, PLAYER_PH, sender.getName());
    }
}
