package hk;

import hk.sound.SoundComparator;
import hk.utility.ConverterType;

import java.io.File;

public class Launch {

    public static void main(String[] args) {
        SoundComparator comparator = new SoundComparator(new File(Settings.WAV_DIRECTORY), ConverterType.LIBRARY);
        comparator.compare();
    }

}
