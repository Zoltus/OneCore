package io.github.zoltus.onecore.data.configuration;

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

    //* Yml file
    private final File file;

    /**
     * @param name of the file
     * @param path of the file
     */
    public OneYml(String name, File path) {
        this.file = new File(path, name);
        options().parseComments(true);
        options().copyDefaults(true);
        options().copyHeader(true);
        reload();
        save();
    }

    /**
     * Gets value from config, if its null it returns default vlaue
     *
     * @param path for value
     * @param def default value
     * @return value
     */
    public <T> T getOrDefault(String path, T def) {
        T value = (T) get(path);
        return value == null ? def : value;
    }

    /**
     * Gets value from config, if its null it returns default value,
     * if value is null it also sets default value
     *
     * @param path for value
     * @param def default value
     * @return value
     */
    public <T> T getOrSetDefault(String path, T def) {
        T value = getOrDefault(path, def);
        boolean contains = contains(path);
        if (!contains) {
            set(path, def);
        }
        return value;
    }

    //* Reloads config from file
    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //* Reloads config from file
    public void reload() {
        //Reloads loads file and sets defaults from source
        try {
            Files.createParentDirs(file);
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            load(file);
            InputStream defaultStream = getResource(file.getName());
            if (defaultStream != null) {
                //todo switch to use Files.bufferedreader
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

    //* Gets resource from classpath
    //why not plugin.saveresource
    private InputStream getResource(String filename) {
        try {
            URL url = getClass().getClassLoader().getResource(filename);
            if (url != null) {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                return connection.getInputStream();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

