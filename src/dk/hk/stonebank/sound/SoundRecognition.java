package dk.hk.stonebank.sound;

import com.google.common.base.Stopwatch;
import com.musicg.fingerprint.FingerprintManager;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;
import dk.hk.stonebank.Settings;
import dk.hk.stonebank.utility.ConsoleColor;
import dk.hk.stonebank.utility.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Created by Hassan on 23-03-2023
 * Stonebank <https://github.com/Stonebank>
 */
public class SoundRecognition {

    private String selectedSound;

    public void printMenu() {

        var wav_directory = Utils.getFilesInDirectory(Settings.WAV_PATH);

        if (wav_directory.length == 0)
            throw new NullPointerException("No wav files found in " + Settings.WAV_PATH.getFileName() + ". Please check the path in Settings.java");

        System.out.println(ConsoleColor.colorText("Select a sound to compare:", ConsoleColor.ANSI_CYAN));

        for (int i = 0; i < wav_directory.length; i++) {

            // if the file isn't a wav file, we skip it
            if (!wav_directory[i].getName().endsWith(".wav"))
                continue;

            System.out.println(i + ". " + wav_directory[i].getName());

        }

        try (var scanner = new Scanner(System.in)) {

            while (selectedSound == null) {

                int selectionIndex = scanner.nextInt();

                if (selectionIndex < 0 || selectionIndex > wav_directory.length - 1) {
                    System.err.println("Invalid selection. You can select between 0 and " + wav_directory.length);
                    continue;
                }

                selectedSound = wav_directory[selectionIndex].getPath();
                System.out.println(ConsoleColor.colorText("You selected: " + selectedSound, ConsoleColor.ANSI_CYAN));

            }

        }

        start();

    }

    private void start() {

        var stopwatch = Stopwatch.createStarted();

        byte[] selectedSoundBytes = new FingerprintManager().extractFingerprint(new Wave(selectedSound));

        var sounds = new ArrayList<SoundData>();
        var wavDirectory = Utils.getFilesInDirectory(Settings.WAV_PATH);

        for (int i = 0; i < wavDirectory.length; i++) {

            // if the file isn't a wav file, we skip it
            if (!wavDirectory[i].getName().endsWith(".wav"))
                continue;

            // If the file is the same as the selected file, we skip it
            if (wavDirectory[i].getPath().equalsIgnoreCase(selectedSound))
                continue;

            byte[] soundBytes = new FingerprintManager().extractFingerprint(new Wave(wavDirectory[i].getPath()));
            var similarity = new FingerprintSimilarityComputer(selectedSoundBytes, soundBytes).getFingerprintsSimilarity();

            sounds.add(new SoundData(wavDirectory[i].getName(),
                    similarity.getMostSimilarFramePosition(), similarity.getsetMostSimilarTimePosition(), similarity.getScore()));

            Utils.printProgressBar(i, wavDirectory.length);

        }

        sounds.sort(Comparator.comparing(SoundData::score).reversed());

        System.out.println(ConsoleColor.colorText("\nOverall score: " + sounds.stream().mapToDouble(SoundData::score).average().orElse(0), ConsoleColor.ANSI_GREEN));
        System.out.println(ConsoleColor.colorText("\nBest match: " + sounds.get(0), ConsoleColor.ANSI_GREEN));
        System.out.println(ConsoleColor.colorText("\nApplication finished in " + stopwatch.stop().elapsed().toMillis() + " ms", ConsoleColor.ANSI_GREEN));

    }

}
