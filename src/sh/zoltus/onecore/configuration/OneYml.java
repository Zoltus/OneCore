package sh.zoltus.onecore.configuration;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class OneYml extends YamlConfiguration {

    //private final String name;
    private final File file;

    public OneYml(String name, File path) {
        this.file = new File(path, name);
        options().parseComments(true);
        options().copyDefaults(true);
        options().copyHeader(true);
        reload();
        save();
    }

    public <T> T getOrDefault(String key, T def) {
        T value = (T) get(key);
        return value == null ? def : value;
    }

    public <T> T getOrSetDefault(String key, T def) {
        T value = getOrDefault(key, def);
        boolean contains = contains(key);
        if (!contains) {
            set(key, def);
        }
        return value;
    }


    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        //Reloads loads file and sets defaults from source
        try {
            Files.createParentDirs(file);
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            load(file);
            InputStream defaultStream = getResource(file.getName());
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, Charsets.UTF_8));
                //Copies header
                options().setHeader(defaultConfig.options().getHeader());
                //Copies default values which havent been added yet & comments
                setDefaults(defaultConfig);

            }
        } catch (IOException | InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }

    }

    private InputStream getResource(String filename) {
        try {
            URL url = getClass().getClassLoader().getResource(filename);
            if (url != null) {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                return connection.getInputStream();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    //autosave on edit? bulkedit?
}

