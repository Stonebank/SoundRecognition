package dk.hk.stonebank.utility;

import dk.hk.stonebank.Settings;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Hassan on 23-03-2023
 * Stonebank <https://github.com/Stonebank>
 */
public class Utils {

    public static boolean hasIdenticalLength() {
        var mp3_directory = getFilesInDirectory(Settings.MP3_PATH);
        var wav_directory = getFilesInDirectory(Settings.WAV_PATH);
        return mp3_directory.length == wav_directory.length;
    }

    public static File[] getFilesInDirectory(Path path) {
        var directory = path.toFile().listFiles();
        if (directory == null)
            throw new NullPointerException(path.getFileName() + " is null. Please check the path in Settings.java");
        return directory;
    }

    public static void printProgressBar(int current, int total) {
        int width = 50;
        double progress = (double) current / total;
        int progressBarWidth = (int) (progress * width);
        System.out.print("\r[");
        for (int i = 0; i < width; i++)
            System.out.print(ConsoleColor.colorText(i < progressBarWidth ? "=" : " ", ConsoleColor.ANSI_GREEN));
        System.out.print("] " + ConsoleColor.colorText((int) (progress * 100) + "%", ConsoleColor.ANSI_GREEN));
    }

}
