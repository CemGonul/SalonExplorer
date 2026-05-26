package cem.gonul;

public class Main {

    private static final int DEFAULT_TARGET_COUNT = 120;
    private static final int MINIMUM_TARGET_COUNT = 100;

    public static void main(String[] args) throws Exception {
        int targetCount = DEFAULT_TARGET_COUNT;

        if (args.length > 0) {
            targetCount = Integer.parseInt(args[0]);
        }

        if (targetCount < MINIMUM_TARGET_COUNT) {
            throw new IllegalArgumentException("Target count must be at least " + MINIMUM_TARGET_COUNT
                    + ". Received: " + targetCount);
        }

        SalonDataCollector collector = new SalonDataCollector();
        collector.collect(targetCount);
    }
}
