package hk.utility;

import hk.Settings;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AudioTrimmer {

    /*
     * TODO: CODE IS STILL IN PROGRESS AND MAY NOT WORK
     */

    public static void main(String[] args) {
        AudioTrimmer audioTrimmer = new AudioTrimmer(new File(Settings.WAV_DIRECTORY));
        audioTrimmer.trim(2,1);
    }

    private final File directory;

    public AudioTrimmer(File directory) {
        this.directory = directory;
    }

    public void trim(int startSeconds, int secondsToCopy) {

        if (directory == null || !directory.exists() || directory.listFiles() == null) {
            System.err.println("ERROR! Directory was not found.");
            return;
        }

        if (Objects.requireNonNull(directory.listFiles()).length == 0) {
            System.err.println("ERROR! Directory is empty.");
            return;
        }

        for (File audio_file : Objects.requireNonNull(directory.listFiles())) {

            if (audio_file == null || !audio_file.getName().endsWith(".wav"))
                continue;

            try {

                AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(audio_file);
                AudioFormat format = fileFormat.getFormat();

                AudioInputStream inputStream = AudioSystem.getAudioInputStream(audio_file);

                int bytesPerSecond = (int) (format.getFrameSize() * format.getFrameRate());

                long skip = inputStream.skip((long) startSeconds * bytesPerSecond);
                long framesOfAudio = (long) (secondsToCopy * format.getFrameRate());

                AudioInputStream shortenedStream = new AudioInputStream(inputStream, format, framesOfAudio);
                File new_audio_file = new File(audio_file.getPath());

                AudioSystem.write(shortenedStream, fileFormat.getType(), new_audio_file);

                System.out.println("Successfully trimmed " + audio_file.getName() + ": [start_second=" + startSeconds + ", seconds_to_copy=" + secondsToCopy + ", skip=" + skip + "]");

                inputStream.close();
                shortenedStream.close();

            } catch (UnsupportedAudioFileException | IOException e) {
                System.err.println("ERROR! An error occurred while trimming audio...");
                e.printStackTrace();
            }

        }

    }

}
