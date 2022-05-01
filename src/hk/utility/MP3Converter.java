package hk.utility;

import com.google.common.base.Stopwatch;
import hk.Settings;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class MP3Converter {

    private final File directory;
    private final Stopwatch stopwatch = Stopwatch.createStarted();

    private int conversions;

    public MP3Converter(File directory) {
        this.directory = directory;
    }

    public void convertJL() {

        if (directory == null || !directory.exists() || directory.listFiles() == null) {
            System.err.println("ERROR! Directory was not found.");
            return;
        }

        if (Objects.requireNonNull(directory.listFiles()).length == 0) {
            System.err.println("ERROR! Directory is empty.");
            return;
        }

        Converter converter = new Converter();

        for (File audio_file : Objects.requireNonNull(directory.listFiles())) {

            if (audio_file == null || !audio_file.getName().endsWith(".mp3")) {
                if (audio_file != null)
                    System.err.println("ATTENTION: Skipping " + audio_file.getName() + " since it is not a .mp3 file.");
                continue;
            }

            if (Files.exists(Paths.get(Settings.WAV_DIRECTORY + audio_file.getName().replaceAll(".mp3",".wav")))) {
                System.err.println("ATTENTION: Skipping " + audio_file.getName() + " since it already has been converted.");
                continue;
            }

            try {
                System.out.println("Converting " + audio_file.getName());
                converter.convert(audio_file.getPath(), Settings.WAV_DIRECTORY + audio_file.getName().replaceAll(".mp3", ".wav"));
                conversions++;
            } catch (JavaLayerException e) {
                System.err.println("ERROR! Could not convert " + audio_file.getName() + "...");
                e.printStackTrace();
            }

        }

        System.out.println("[JL converter]: Successfully converted " + conversions + " mp3 files in " + stopwatch.stop().elapsed() + " ms");

    }

    public void convert() {

        if (directory == null || !directory.exists() || directory.listFiles() == null) {
            System.err.println("ERROR! Directory was not found.");
            return;
        }

        if (Objects.requireNonNull(directory.listFiles()).length == 0) {
            System.err.println("ERROR! Directory is empty.");
            return;
        }

        for (File audio_file : Objects.requireNonNull(directory.listFiles())) {

            if (audio_file == null || !audio_file.getName().endsWith(".mp3")) {
                if (audio_file != null)
                    System.err.println("ATTENTION: Skipping " + audio_file.getName() + " since it is not a .mp3 file.");
                continue;
            }

            if (Files.exists(Paths.get(Settings.WAV_DIRECTORY + audio_file.getName().replaceAll(".mp3",".wav")))) {
                System.err.println("ATTENTION: Skipping " + audio_file.getName() + " since it already has been converted.");
                continue;
            }

            try {

                AudioInputStream mp3stream = AudioSystem.getAudioInputStream(audio_file);
                AudioFormat sourceFormat = mp3stream.getFormat();

                AudioFormat convertFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        sourceFormat.getSampleRate(), 16,
                        sourceFormat.getChannels(),
                        sourceFormat.getChannels() * 2,
                        sourceFormat.getSampleRate(), false
                );

                System.out.println("Converting " + audio_file.getName());
                AudioInputStream wav = AudioSystem.getAudioInputStream(convertFormat, mp3stream);
                AudioSystem.write(wav, AudioFileFormat.Type.WAVE, new File(Settings.WAV_DIRECTORY + audio_file.getName().replaceAll(".mp3",".wav")));
                conversions++;

            } catch (UnsupportedAudioFileException | IOException e) {
                System.err.println("ERROR! Could not convert " + audio_file.getName() + "...");
                e.printStackTrace();
            }

        }

        System.out.println("[JAVA converter]: Successfully converted " + conversions + " mp3 files in " + stopwatch.stop().elapsed() + " ms");

    }

}
