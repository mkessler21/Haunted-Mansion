package music;

import edu.princeton.cs.algs4.StdAudio;


// Allows background music to play when game is started and loops music
public class SoundPlayer extends Thread {
    private String filepath;
    private volatile boolean loop;

    public SoundPlayer(String filepath) {
        this.filepath = filepath;
        this.loop = true;
    }

    @Override
    public void run() {
        while (loop) {
            StdAudio.play(filepath);

            try {
                Thread.sleep(120 * 1000); //delays loop 2 minutes so it can restart
            } catch (InterruptedException e) {
                // Catches if sound is interrupted during playing
                Thread.currentThread().interrupt();
            }
        }
    }

    //Stops looping sound
    public void stopLooping() {
        loop = false;
        StdAudio.close();
    }
}
