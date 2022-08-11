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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BackupHandler {

    private static int UPTIMEHOURS = 0;

    //Backups based time from startup
    public static void backupTimer() {
        File inputDirectory = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
        File outputPath = new File(OneCore.getPlugin().getDataFolder().getAbsolutePath() + "/backups/");
        List<Integer> hours = List.of(4, 12, 24); //todo hours to config and uptime interval possibly,to singleton
        Bukkit.getScheduler().runTaskTimerAsynchronously(OneCore.getPlugin(), () -> {
            //backup on start
            if (UPTIMEHOURS == 0) {
                createTarGz(inputDirectory, outputPath, "playerdata-startup");
            } else {
                hours.stream() //backup every hour which is marked on list
                        .filter(integer -> UPTIMEHOURS % integer == 0)
                        .forEach(integer -> createTarGz(inputDirectory, outputPath, "playerdata-" + integer + "h"));
            }
            UPTIMEHOURS++;
        }, 0, 72000); //Every hour
    }


    private static void createTarGz(File inputPath, File outputPath, String outputName) {
        //todo a
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
