package io.github.zoltus.onecore.data;


import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class BackupHandler {

    private final OneCore plugin;
    private int upTimeHours = 0;
    private final List<Integer> hours = List.of(4, 12, 24);
    private final List<File> backupFiles = new ArrayList<>();

    public BackupHandler(OneCore plugin) {
        this.plugin = plugin;
        //output directory
        File dataFolder = plugin.getDataFolder();
        File outputDirectory = new File(dataFolder, "backups");
        File worldFolder = Bukkit.getWorlds().get(0).getWorldFolder();
        //noinspection ResultOfMethodCallIgnored
        outputDirectory.mkdirs();

        //Backups based time from startup
        if (Config.BACKUPS_STATS_ENABLED.getBoolean()) {
            backupFiles.add(new File(worldFolder, "stats"));
        }
        if (Config.BACKUPS_DATABASE_ENABLED.getBoolean()) {
            backupFiles.add(new File(dataFolder, "database.db"));
        }
        if (Config.BACKUPS_PLAYERDATA_ENABLED.getBoolean()) {
            backupFiles.add(new File(worldFolder, "playerdata"));
        }
        if (!backupFiles.isEmpty()) {//Starts backup task if there is any backup files.
            startBackupTaskAsync(outputDirectory);
        }
    }

    private void startBackupTaskAsync(File outputDirectory) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (File file : backupFiles) {
                String fileName = file.getName();
                //todo fix db name ending
                String name = upTimeHours == 0 ? fileName + "-startup" : fileName + "-" + upTimeHours + "h";
                // final Path target = Paths.get(plugin.getDataFolder() + "/backups/" + "f" + "(" + "t" + ").db");
                hours.stream() //backup every hour which is marked on list
                        .filter(integer -> upTimeHours % integer == 0)
                        .forEach(integer -> {
                            try {
                                if (file.isFile()) {
                                    Files.copy(file.toPath(), Paths.get(outputDirectory + "/" + name), StandardCopyOption.REPLACE_EXISTING);
                                } else {
                                    createTarGz(file, outputDirectory, name);
                                }
                            } catch (IOException e) {
                                plugin.getLogger().warning("Backup failed! " + e.getMessage());
                            }
                        });
            }
            upTimeHours++;
        }, 0, 72000); //Every hour
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createTarGz(File inputPath, File outputPath, String outputName) {
        outputPath.mkdirs();
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath + "/" + outputName + ".tar.gz");
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             GzipCompressorOutputStream gzipOutputStream = new GzipCompressorOutputStream(bufferedOutputStream);
             TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(gzipOutputStream)) {
            tarArchiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
            tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            List<File> files = new ArrayList<>(FileUtils.listFiles(
                    inputPath,
                    new RegexFileFilter("^(.*?)"), //todo document
                    DirectoryFileFilter.DIRECTORY
            ));
            for (File currentFile : files) {
                String relativeFilePath = inputPath.toURI().relativize(
                        new File(currentFile.getAbsolutePath()).toURI()).getPath();
                TarArchiveEntry tarEntry = new TarArchiveEntry(currentFile, relativeFilePath);
                tarEntry.setSize(currentFile.length());
                tarArchiveOutputStream.putArchiveEntry(tarEntry);
                tarArchiveOutputStream.write(IOUtils.toByteArray(new FileInputStream(currentFile)));
                tarArchiveOutputStream.closeArchiveEntry();
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Backup failed creating tar! " + e.getMessage());
        }
    }
}
