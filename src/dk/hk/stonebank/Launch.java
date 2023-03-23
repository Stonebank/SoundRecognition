package dk.hk.stonebank;

import dk.hk.stonebank.sound.SoundRecognition;
import dk.hk.stonebank.utility.Utils;
import dk.hk.stonebank.utility.converter.ConverterMode;
import dk.hk.stonebank.utility.converter.MP3Converter;

/**
 * Created by Hassan on 23-03-2023
 * Stonebank <https://github.com/Stonebank>
 */
public class Launch {

    public static void main(String[] args) {

        // If the number of mp3 files is not equal to the number of wav files, convert the mp3 files to wav files
        if (!Utils.hasIdenticalLength())
            new MP3Converter(ConverterMode.JLLAYER);

        var soundRecognition = new SoundRecognition();
        soundRecognition.printMenu();

    }

}
