package sh.zoltus.onecore.data.database;

import com.google.gson.InstanceCreator;
import org.bukkit.OfflinePlayer;
import sh.zoltus.onecore.player.User;

import java.lang.reflect.Type;

public record OneUserAdapter(OfflinePlayer offP) implements InstanceCreator<User> {
    @Override
    public User createInstance(Type type) {
        return new User(offP);
    }
}