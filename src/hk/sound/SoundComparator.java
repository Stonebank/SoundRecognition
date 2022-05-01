package hk.sound;

import com.google.common.base.Stopwatch;
import com.musicg.fingerprint.FingerprintManager;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;
import hk.Settings;
import hk.utility.ConverterType;
import hk.utility.MP3Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SoundComparator {

    private final File directory;

    private final ConverterType type;

    private final Stopwatch stopwatch = Stopwatch.createStarted();

    private final MP3Converter converter = new MP3Converter(new File(Settings.MP3_DIRECTORY));

    private String main_sound;

    public SoundComparator(File directory, ConverterType type) {
        this.directory = directory;
        this.type = type;
    }

    public void compare() {

        switch (type) {
            case VANILLA_JAVA:
                converter.convert();
                break;
            case LIBRARY:
                converter.convertJL();
                break;
            default:
                System.err.println("Type is not recognized.");
                System.exit(0);
        }

        System.out.println("CONVERSION_TYPE=" + type);

        File[] audio_file = directory.listFiles();
        if (audio_file != null) {

            if (audio_file.length == 0) {
                System.err.println("ERROR! Directory is empty.");
                return;
            }

            System.out.println("Select main sound (input integer in console):");

            for (int i = 0; i < audio_file.length; i++) {

                if (!audio_file[i].getName().endsWith(".wav") && !audio_file[i].isDirectory()) {
                    System.err.println("Could not read '" + audio_file[i].getName() + "'");
                    System.err.println("Sounds can only load and read .wav files. Please remove any other filetype including folders.");
                    System.exit(0);
                    return;
                }

                System.out.println(i + ": " + audio_file[i].getPath());

            }

            try (Scanner scanner = new Scanner(System.in)) {

                while (main_sound == null) {

                    int index = scanner.nextInt();

                    if (index < 0 || index > audio_file.length - 1) {
                        System.err.println("You can select between 0 - " + audio_file.length);
                        continue;
                    }

                    main_sound = audio_file[index].getPath();
                    System.out.println("You have selected the main sound to be " + index + ": " + main_sound);

                }

            }

            byte[] main_sound_byte = new FingerprintManager().extractFingerprint(new Wave(main_sound));

            ArrayList<Sound> sounds = new ArrayList<>();

            for (int i = 0; i < audio_file.length; i++) {

                if (audio_file[i] == null || !audio_file[i].getPath().endsWith(".wav") || audio_file[i].getPath().equalsIgnoreCase(main_sound))
                    continue;

                long elapsedTime = stopwatch.elapsed().toMillis();
                long soundsLeft = audio_file.length - i;
                long eta = TimeUnit.MILLISECONDS.toSeconds((elapsedTime / (i + 1)) * soundsLeft);

                double progress = ((double) i / audio_file.length) * 100;

                System.out.println("Current index: " + i);
                System.out.println("Remaining time: " + formatTime(eta) + "[progress=" + progress + "%]");
                System.out.println("Now comparing " + audio_file[i].getName() + "\n");

                byte[] sound = new FingerprintManager().extractFingerprint(new Wave(audio_file[i].getPath()));
                FingerprintSimilarity similarity = new FingerprintSimilarityComputer(main_sound_byte, sound).getFingerprintsSimilarity();

                sounds.add(new Sound(audio_file[i].getName(), similarity.getScore(), similarity.getMostSimilarFramePosition()));

            }

            System.out.println("\nOverall score:");
            sounds.forEach(System.out::println);

            sounds.sort(Comparator.comparing(Sound::getScore).reversed());

            System.out.println("\nBest match: " + sounds.get(0) + "\n");
            System.out.println("You selected " + main_sound);
            System.out.println("Application finished in " + formatTime(stopwatch.stop().elapsed().getSeconds()));

        }

    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        minutes -= hours * 60;
        seconds -= (hours * 60 * 60) + (minutes * 60);
        hours -= days * 24;
        return (days != 0 ? days + " " + (days > 1 ? "days " : "day ") : "")
                + (hours != 0 ? hours + " " + (hours > 1 ? "hours " : "hour ") : "")
                + (minutes != 0 ? minutes + " " + (minutes > 1 ? "minutes " : "minute ") : "")
                + (seconds != 0 ? seconds + " " + (seconds > 1 ? "seconds " : "second ") : "");
    }

}
