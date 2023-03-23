package dk.hk.stonebank.sound;

import com.google.common.base.Stopwatch;
import com.musicg.fingerprint.FingerprintManager;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;
import dk.hk.stonebank.Settings;
import dk.hk.stonebank.utility.ConsoleColor;
import dk.hk.stonebank.utility.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println(ConsoleColor.colorText("Available processors: " + availableProcessors, ConsoleColor.ANSI_CYAN));

        var stopwatch = Stopwatch.createStarted();

        var fingerprintManager = new FingerprintManager();

        System.out.println(ConsoleColor.colorText("Extracting fingerprint from " + selectedSound, ConsoleColor.ANSI_CYAN));
        byte[] selectedSoundFingerprint = new FingerprintManager().extractFingerprint(new Wave(selectedSound));

        var sounds = Collections.synchronizedList(new ArrayList<SoundData>());
        var wavDirectory = Utils.getFilesInDirectory(Settings.WAV_PATH);

        var progress = new AtomicInteger();

        var futures = new ArrayList<Future<?>>();
        var service = Executors.newFixedThreadPool(availableProcessors);

        System.out.println(ConsoleColor.colorText("Initiating sound recognition...", ConsoleColor.ANSI_CYAN));

        for (int i = 0; i < wavDirectory.length; i++) {

            // if the file isn't a wav file, we skip it
            if (!wavDirectory[i].getName().endsWith(".wav"))
                continue;

            // if the file is the same as the selected sound, we skip it
            if (wavDirectory[i].getPath().equalsIgnoreCase(selectedSound))
                continue;

            int tempIndex = i;
            futures.add(service.submit(() -> {

                byte[] currentSoundBytes = fingerprintManager.extractFingerprint(new Wave(wavDirectory[tempIndex].getPath()));

                var similarity = new FingerprintSimilarityComputer(selectedSoundFingerprint, currentSoundBytes).getFingerprintsSimilarity();

                sounds.add(new SoundData(wavDirectory[tempIndex].getName(),
                        similarity.getMostSimilarFramePosition(), similarity.getsetMostSimilarTimePosition(), similarity.getScore()));
                Utils.printProgressBar(progress.incrementAndGet(), wavDirectory.length);

            }));

        }

        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        service.shutdown();

        sounds.sort(Comparator.comparing(SoundData::score).reversed());

        System.out.println(ConsoleColor.colorText(sounds.get(0).toString(), ConsoleColor.ANSI_GREEN));
        System.out.println(ConsoleColor.colorText("Application finished in " + stopwatch.stop().elapsed().toMillis() + " ms", ConsoleColor.ANSI_GREEN));

    }

}
