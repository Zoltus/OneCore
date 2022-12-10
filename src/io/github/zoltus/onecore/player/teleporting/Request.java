package io.github.zoltus.onecore.player.teleporting;


import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.player.User;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
        TP_YOU_ACCEPTED.send(accepter, PLAYER_PH, accepter.getName());
        cancel();
        Teleport.start(teleporter, target, null);
    }

    public void deny() {
        cancel();
        TP_DENIED.send(sender, PLAYER_PH, accepter.getName());
        TP_YOU_DENIED.send(accepter, PLAYER_PH, accepter.getName());
    }

    private void cancel() {
        requests.remove(this);
        expiryTask.cancel();
    }





    private void sendChat() {
        TP_SENT.send(sender, PLAYER_PH, accepter.getName());
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

    public static BaseComponent[] replacePlaceholder(String line, String placeholder, String replaceWith) {
        // create the string builder to build the new string
        ComponentBuilder sb = new ComponentBuilder();
        //Keeps previous colors
        ComponentBuilder.FormatRetention retention = ComponentBuilder.FormatRetention.FORMATTING;
        // split the line into sections based on the placeholder
        String[] sections = line.split(placeholder);
        // loop through all sections
        for (int i = 0; i < sections.length; i++) {
            sb.append(new TextComponent(sections[i]), retention);
            // check if there is a placeholder to be replaced
            if (i != sections.length - 1) {
                // add the hover event for the placeholder
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(replaceWith));
                // create a new text component with the placeholder
                TextComponent placeholderText = new TextComponent(placeholder);
                // set the hover event for the placeholder
                placeholderText.setHoverEvent(hoverEvent);
                // append the placeholder component to the stringbuilder
                sb.append(placeholderText, retention);
            }
        }
        return sb.create();
    }


}
