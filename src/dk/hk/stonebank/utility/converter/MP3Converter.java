package dk.hk.stonebank.utility.converter;

import com.google.common.base.Stopwatch;
import dk.hk.stonebank.Settings;
import dk.hk.stonebank.utility.ConsoleColor;
import dk.hk.stonebank.utility.Utils;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Created by Hassan on 23-03-2023
 * Stonebank <https://github.com/Stonebank>
 */
public class MP3Converter {

    private final File mp3_directory = Settings.MP3_PATH.toFile();
    private final File wav_directory = Settings.WAV_PATH.toFile();

    public MP3Converter(ConverterMode mode) {

        if (!mp3_directory.exists())
            throw new NullPointerException("MP3 directory doesn't exist. Please check the path in Settings.java");

        if (Objects.requireNonNull(mp3_directory.listFiles()).length == 0)
            throw new IllegalStateException("MP3 directory is empty. Please add mp3 files to the directory");

        if (wav_directory.mkdir())
            System.out.println("Created WAV directory: " + wav_directory.getAbsolutePath());

        switch (mode) {
            case VANILLA -> vanillaConvert();
            case JLLAYER -> jLayerConvert();
            default -> throw new IllegalArgumentException("Converter mode must be specified");
        }

    }

    private void vanillaConvert() {

        // TODO: Implement vanilla converter

    }

    private void jLayerConvert() {

        // we initialize the stopwatch
        var stopwatch = Stopwatch.createStarted();

        // initialize the converter
        var converter = new Converter();

        // totalFiles is the number of files that needs to be converted
        int totalFiles = Math.abs(Utils.getFilesInDirectory(Settings.MP3_PATH).length - Utils.getFilesInDirectory(Settings.WAV_PATH).length);

        // currentFile keeps track of the current amount of files converted
        int currentFile = 0;

        System.out.println(ConsoleColor.colorText("Attention: There was detected a difference in the amount of files in the mp3 and wav directory.", ConsoleColor.ANSI_CYAN));
        System.out.println(ConsoleColor.colorText("JLayer converter started. Files to convert: " + totalFiles + " files", ConsoleColor.ANSI_CYAN));

        for (var file : Utils.getFilesInDirectory(Settings.MP3_PATH)) {

            // if the file is null or not an mp3 file, we skip it
            if (file == null || !file.getName().endsWith(".mp3"))
                continue;

            // If the file already exists in the wav directory, we skip it
            if (Files.exists(Paths.get(wav_directory.getAbsolutePath() + "/" + file.getName().replace(".mp3", ".wav"))))
                continue;

            // we try to convert the file, if it fails, we print an error message
            try {
                converter.convert(file.getAbsolutePath(), wav_directory.getAbsolutePath() + "/" + file.getName().replace(".mp3", ".wav"));
            } catch (JavaLayerException e) {
                System.err.println("Failed to convert file: " + file.getName());
                e.printStackTrace();
            }

            currentFile++;
            Utils.printProgressBar(currentFile, totalFiles);

        }

        System.out.println(ConsoleColor.colorText("\nJLayer converter finished. Converted " + totalFiles + " files in " + stopwatch.stop().elapsed().toMillis() + " ms", ConsoleColor.ANSI_GREEN));

    }

}
