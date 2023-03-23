package dk.hk.stonebank.sound;

/**
 * Created by Hassan on 23-03-2023
 * Stonebank <https://github.com/Stonebank>
 */

public record SoundData(String name, int bestFrame, double bestTimePosition, double score) {

    @Override
    public double score() {
        return score * 100 >= 100 ? 100 : score * 100;
    }

    @Override
    public String toString() {
        return "SoundData{" +
                "name='" + name + '\'' +
                ", bestFrame=" + bestFrame +
                ", bestTimePosition=" + bestTimePosition +
                ", score=" + score +
                '}';
    }

}
