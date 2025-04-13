package io.github.zoltus.onecore.data.configuration;

import com.google.common.io.Files;
import io.github.zoltus.onecore.OneCore;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class OneYml extends YamlConfiguration {

    private final File file;
    private YamlConfiguration defaultVals;

    /**
     * @param name of the file
     * @param path of the file
     */
    public OneYml(String name, File path) {
        this.file = new File(path, name);
        options().parseComments(true);
        options().copyDefaults(true);
        reload();
        save();
    }

    /**
     * Gets default value from jars yml file.
     *
     * @param path for value
     * @return value
     */
    public <T> T getOrDefault(String path) {
        T value = (T) get(path);
        T defaultVal = (T) defaultVals.get(path);
        if (value == null && defaultVal == null) {
            OneCore.getPlugin().getLogger().warning("§cMissing config: " + path);
        } else if (value == null) {
            OneCore.getPlugin().getLogger().warning("§cMissing config: " + path + " Using default: " + defaultVal);
        }
        return value == null ? defaultVal : value;
    }

    /**
     * Gets from user set value.
     *
     * @param path for value
     * @return value
     */
    public <T> T getOrDefault(String path, T def) {
        T value = (T) get(path);
        return value == null ? def : value;
    }

    public float getFloat(String path) {
        Object rawValue = getOrDefault(path);
        return switch (rawValue) {
            case Float f -> f;
            case Double d -> d.floatValue();
            case Integer i -> i.floatValue();
            case Long l -> l.floatValue();
            case String s -> NumberUtils.toFloat(s);
            default -> throw new IllegalArgumentException("Cannot convert " + rawValue.getClass() + " to float");
        };
    }

    //* Reloads config from file
    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save " + file, e);
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
                defaultVals = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
                //Copies header
                options().setHeader(defaultVals.options().getHeader());
                //Copies default values which havent been added yet & comments
                setDefaults(defaultVals);

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
            Bukkit.getLogger().log(Level.SEVERE, "Could not get resource " + filename, ex);
        }
        return null;
    }
}

