package io.github.zoltus.onecore.utils;

import lombok.Getter;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Zoltus
 */
public class ChatBuilder implements Listener {

    //Todo redo this whole class
/*  Hover Content
    Content
    Entity
    EntitySerializer
    Item
    Text
    ItemSerializer
    TextSerializer
    */

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

    //<Command,Consumer> for each component with custom click event, "/hash<key>"
    private static final Map<String, Consumer<Player>> clickCommands = new HashMap<>();

    //Lines to be replaced
    private final String[] lines;
    //BaseComponent for each row
    private final List<BaseComponent> rowComponents = new ArrayList<>();
    //<key, component> for each key to be replaced with component
    private final Map<String, Component> placeholders = new HashMap<>();

    /**
     * @param string to be replaced with components
     */
    public ChatBuilder(String... string) {
        this.lines = string;
    }

    public ChatBuilder addComponents(Component... components) {
        for (Component component : components) {
            placeholders.put(component.getKey(), component);
        }
        return this;
    }

    /**
     * @param chatComponent to add extra to
     * @param word          to be split % replaced
     * @param wordComp      to get colors
     */
    private void handleWord(BaseComponent chatComponent, String word, BaseComponent wordComp) {
        if (placeholders.containsKey(word)) {
            chatComponent.addExtra(copyFormatting(placeholders.get(word).toBaseComponent(), wordComp));
        } else {
            Map.Entry<String, Component> entry = placeholders.entrySet().stream()
                    .filter(entry1 -> word.contains(entry1.getKey()))
                    .findFirst().orElse(null);
            //If placeholders are not found it adds space, else replaces stuff
            if (entry == null) {
                chatComponent.addExtra(copyFormatting(new TextComponent(word), wordComp));
            } else {
                String match = entry.getKey();
                //BaseComponent component = entry.getValue();
                String[] wordSplit = word.split("(?<=" + match + ")|(?=" + match + ")");
                Arrays.stream(wordSplit).forEach(wordword -> {              //todo cleanup this wordword shit
                    Component comp = placeholders.getOrDefault(wordword, new Component(wordword, wordword));
                    chatComponent.addExtra(copyFormatting(comp.toBaseComponent(), wordComp));
                });
            }
        }
    }

    /**
     * Builds the components
     *
     * @return ChatBuilder
     */
    public ChatBuilder build() {
        Arrays.stream(lines).forEach(line -> {// §atesti %var% §btesti a
            TextComponent chatComponent = new TextComponent();
            BaseComponent[] wordComponents = TextComponent.fromLegacyText(line); // [§atesti %var% ,§btesti a]
            for (BaseComponent wordComponent : wordComponents) {
                //Splits color sections to words
                String[] words = wordComponent.toLegacyText().split("(?<= )|(?= )"); //§atesti %var%
                //Handles word in color component
                Arrays.stream(words).forEach(word -> handleWord(chatComponent, ChatColor.stripColor(word), wordComponent));
            }
            rowComponents.add(chatComponent);
        });
        return this;
    }
//

    /**
     * Copies BaseComponent colors.
     *
     * @param to   component
     * @param from component
     * @return BaseComponent
     */
    private BaseComponent copyFormatting(BaseComponent to, BaseComponent from) {
        to.copyFormatting(from, ComponentBuilder.FormatRetention.FORMATTING, true);
        return to;
    }

    /**
     * @param players to receive the message
     */
    public void send(Player... players) {
        rowComponents.forEach(components -> Arrays.stream(players).forEach(player -> player.spigot().sendMessage(components)));
    }

    /**
     * This handles click command executions for consumers
     *
     * @param e event
     */
    @EventHandler
    private static void onChat(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String cmd = e.getMessage().split(" ")[0];
        if (clickCommands.containsKey(cmd)) {
            e.setCancelled(true);
            clickCommands.get(cmd).accept(p);
        }
    }

    public static final class Component {
        @Getter
        private String key, text;
        private BaseComponent baseComponent;

        private HoverEvent hoverEvent;
        private ClickEvent clickEvent;

        public Component(String key, String text) {
            this.key = key;
            this.text = text;
        }

        public Component(BaseComponent baseComponent) {
            this.baseComponent = baseComponent;
        }

        public Component setHoverEvent(HoverEvent event) {
            this.hoverEvent = event;
            return this;
        }

        public Component setClickEvent(ClickEvent event) {
            this.clickEvent = event;
            return this;
        }

        public Component setCustomAction(Consumer<Player> consumer) {
            clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + this.hashCode() + key);
            clickCommands.put("/" + this.hashCode() + key, consumer);
            return this;
        }

        public BaseComponent toBaseComponent() {
            BaseComponent comp = baseComponent == null ? new TextComponent(text) : baseComponent;
            comp.setClickEvent(clickEvent);
            comp.setHoverEvent(hoverEvent);
            return comp;
        }
    }
}