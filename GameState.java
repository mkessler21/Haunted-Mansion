package core;

import java.util.List;

public class GameState {
    private long seed;
    private int avatarX;
    private int avatarY;
    private int avatarHealth;
    private List<int[]> skullPositions; // Each int[] holds x, y positions for a skull
    private List<int[]> heartPositions; // Each int[] holds x, y positions for a heart

    public GameState(long seed, int avatarX, int avatarY, int avatarHealth,
                     List<int[]> skullPositions, List<int[]> heartPositions) {
        this.seed = seed;
        this.avatarX = avatarX;
        this.avatarY = avatarY;
        this.avatarHealth = avatarHealth;
        this.skullPositions = skullPositions;
        this.heartPositions = heartPositions;
    }

    // Getters and potentially setters if needed
    public long getSeed() {
        return seed;
    }
    public int getAvatarX() {
        return avatarX;
    }
    public int getAvatarY() {
        return avatarY;
    }
    public int getAvatarHealth() {
        return avatarHealth;
    }
    public List<int[]> getHeartPositions() {
        return heartPositions;
    }
    public List<int[]> getSkullPositions() {
        return skullPositions;
    }
}
