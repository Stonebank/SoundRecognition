package hk.sound;

public class Sound {

    private final String name;

    private final double score;

    private final int bestFrame;

    public Sound(String name, double score, int bestFrame) {
        this.name = name;
        this.score = score;
        this.bestFrame = bestFrame;
    }

    public String getName() {
        return name;
    }

    public double getConfidence() {
        double confidence = 100 * score;
        if (confidence >= 100)
            confidence = 100;
        return confidence;
    }

    public int getBestFrame() {
        return bestFrame;
    }

    @Override
    public String toString() {
        return getName() + ": [confidence=" + getConfidence() + "%, best frame=" + getBestFrame() + "]";
    }

}
