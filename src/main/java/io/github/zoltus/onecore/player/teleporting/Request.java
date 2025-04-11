package io.github.zoltus.onecore.player.teleporting;


import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Request {

    private static final OneCore plugin = OneCore.getPlugin();

    public static Request getLatest(User accepter) {
        List<Request> requests = accepter.getRequests();
        return requests.isEmpty() ? null : requests.getLast();
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
            TP_EXPIRED.send(sender);
        }, 20L * Config.TELEPORT_EXPIRE.getInt());
    }

    public static boolean hasRequest(User sender, User accepter) {
        return accepter.getRequests().stream()
                .anyMatch(req -> req.getSender() == sender && req.getAccepter() == accepter);
    }

    public static void send(User sender, User accepter, TeleportType type) {
        if (sender == accepter) { //Cant self teleport
            TP_CANT_SELF_TELEPORT.send(sender);
        } else if (!accepter.isHasTpEnabled()) { //Cant teleport if tp toggled
            TP_TOGGLE_IS_OFF.rb(PLAYER_PH, accepter.getName()).send(sender);
        } else if (hasRequest(sender, accepter)) {
            TP_YOU_ALREADY_SENT_REQUEST.rb(PLAYER_PH, accepter.getName()).send(sender);
        } else {
            new Request(sender, accepter, type);
        }
    }

    public void accept() {
        User teleporter = type == TeleportType.TPA ? sender : accepter;
        User target = type == TeleportType.TPA ? accepter : sender;
        TP_ACCEPTED.rb(PLAYER_PH, accepter.getName()).send(sender);
        TP_YOU_ACCEPTED.rb(PLAYER_PH, sender.getName()).send(accepter);
        removeRequest();
        teleporter.teleport(target);
    }

    public void deny() {
        removeRequest();
        TP_DENIED.rb(PLAYER_PH, accepter.getName()).send(sender);
        TP_YOU_DENIED.rb(PLAYER_PH, sender.getName()).send(accepter);
    }

    private void removeRequest() {
        requests.remove(this);
        expiryTask.cancel();
    }

    private void sendChat() {
        TP_SENT.rb(PLAYER_PH, accepter.getName()).send(sender); // todo test
        TP_RECEIVED.rb(PLAYER_PH, sender.getName()).rb(TYPE_PH, type.name()).send(accepter);
    }
}
