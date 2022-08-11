package sh.zoltus.onecore.data;


import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.bukkit.Bukkit;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.data.configuration.yamls.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BackupHandler {

    private final OneCore plugin;
    private int UPTIMEHOURS = 0;
    //todo remove static plugins
    private final List<Integer> hours = List.of(4, 12, 24); //todo hours to config and uptime interval possibly,to singleton
    private final List<Backup> backupFiles = new ArrayList<>();
    //output directory
    private final File dataFolder;
    private final File outputDirectory;
    private final File worldFolder = Bukkit.getWorlds().get(0).getWorldFolder();

    public BackupHandler(OneCore plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.outputDirectory = new File(dataFolder, "backups");
    }

    record Backup(String name, File file) {
    }

    //Backups based time from startup
    public void start() {
        //backups
        Backup stats = new Backup("stats", new File(worldFolder, "stats"));
        Backup database = new Backup("database", new File(dataFolder, "database.db"));
        Backup playerdata = new Backup("playerdata", new File(worldFolder, "playerdata"));

        if (Config.BACKUPS_STATS_ENABLED.getBoolean()) {
            backupFiles.add(stats);
        }
        if (Config.BACKUPS_DATABASE_ENABLED.getBoolean()) {
            backupFiles.add(database);
        }
        if (Config.BACKUPS_PLAYERDATA_ENABLED.getBoolean()) {
            backupFiles.add(playerdata);
        }
        //Starts backup task if there is any backup files.
        if (!backupFiles.isEmpty()) {
            startBackupTaskAsync(outputDirectory);
        }
    }

    private void startBackupTaskAsync(File outputDirectory) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Backup backup : backupFiles) {
                //backup on start
                if (UPTIMEHOURS == 0) {
                    createTarGz(backup.file(), outputDirectory, backup.name() + "-startup");
                } else {
                    hours.stream() //backup every hour which is marked on list
                            .filter(integer -> UPTIMEHOURS % integer == 0)
                            .forEach(integer -> createTarGz(backup.file(), outputDirectory, backup.name() + "-" + integer + "h"));
                }
            }
            UPTIMEHOURS++;
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
                    new RegexFileFilter("^(.*?)"),
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
            throw new RuntimeException(e);
        }
    }
}
