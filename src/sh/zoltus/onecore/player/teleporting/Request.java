package sh.zoltus.onecore.player.teleporting;


import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.player.command.User;

import java.util.List;

import static sh.zoltus.onecore.configuration.yamls.Config.TELEPORT_EXPIRE;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

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
    private final User sender, accepter; //, teleporter, target
    private final List<Request> requests;
    private final BukkitTask expiryTask;

    private Request(User sender, User accepter, TeleportType type) {
        this.type = type;
        this.sender = sender;
        this.accepter = accepter;
        this.expiryTask = expirtyTimer();
        this.requests = accepter.getRequests();
        requests.add(this);
        sendChat();
    }

    private BukkitTask expirtyTimer() {
        return Bukkit.getScheduler().runTaskLater(plugin, () -> {
            requests.remove(this);
            sender.sendMessage(TP_EXPIRED.getString());
        }, 20L * TELEPORT_EXPIRE.getInt());
    }

    public static boolean hasRequest(User sender, User accepter) {
        return accepter.getRequests().stream()
                .anyMatch(req -> req.getSender() == sender && req.getAccepter() == accepter);
    }

    public static void send(User sender, User accepter, TeleportType type) {
        if (sender == accepter) { //Cant self teleport
            sender.sendMessage(TP_CANT_SELF_TELEPORT.getString());
        } else if (!accepter.isTpEnabled()) { //Cant teleport if tp toggled
            sender.sendMessage(TP_TOGGLE_IS_OFF.rp(PLAYER_PH, accepter.getName()));
        } else if (hasRequest(sender, accepter)) {
            sender.sendMessage(TP_YOU_ALREADY_SENT_REQUEST.rp(PLAYER_PH, accepter.getName()));
        } else {
            new Request(sender, accepter, type);
        }
    }

    public void accept() {
        User teleporter = type == TeleportType.TPA ? sender : accepter;
        User target = type == TeleportType.TPA ? accepter : sender;
        String senderMsg = TP_ACCEPTED.rp(PLAYER_PH, accepter.getName());
        String accepterMsg = TP_YOU_ACCEPTED.rp(PLAYER_PH, accepter.getName());
        sender.sendMessage(senderMsg);
        accepter.sendMessage(accepterMsg);

        cancel();
        Teleport.start(teleporter, target, null);
    }

    public void deny() {
        cancel();
        sender.sendMessage(TP_DENIED.rp(PLAYER_PH, accepter.getName()));
        accepter.sendMessage(TP_YOU_DENIED.rp(PLAYER_PH, accepter.getName()));
    }

    private void cancel() {
        requests.remove(this);
        expiryTask.cancel();
    }

    private void sendChat() {
        sender.sendMessage(TP_SENT.rp(PLAYER_PH, accepter.getName()));
                /*ChatBuilder cb = new ChatBuilder(
                TP_RECEIVED.rp(PLAYER_PH, sender.getName())
                , TP_RECEIVED_ACCEPT_LINE.getString()
        );

        ChatBuilder.Component comp = new ChatBuilder.Component(ACCEPT_PH, TP_ACCEPT_BUTTON.getString());
        comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + TPACCEPT_LABEL.getString() + " " + sender.getName()));
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TP_ACCEPT_BUTTON_HOVER.getString())));

        ChatBuilder.Component comp2 = new ChatBuilder.Component(DENY_PH, TP_DENY_BUTTON.getString());
        comp2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + TPDENY_LABEL.getString() + " " + sender.getName()));
        comp2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TP_DENY_BUTTON_HOVER.getString())));
        cb.addComponents(comp, comp2);
        cb.build();
        cb.send(accepter);*/
    }


}
