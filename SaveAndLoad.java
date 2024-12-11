package core;

import utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class SaveAndLoad {
    private static final String SAVE_FILE = "game_save.txt"; // Path to the save file

    public boolean save(GameState gameState) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(gameState.getSeed()).append("\n");
        contentBuilder.append(gameState.getAvatarX()).append(" ").
                append(gameState.getAvatarY()).append(" ").append(gameState.getAvatarHealth()).append("\n");
        for (int[] pos : gameState.getHeartPositions()) {
            contentBuilder.append("H ").append(pos[0]).append(" ").append(pos[1]).append("\n");
        }

        for (int[] pos : gameState.getSkullPositions()) {
            contentBuilder.append("S ").append(pos[0]).append(" ").append(pos[1]).append("\n");
        }

        FileUtils.writeFile(SAVE_FILE, contentBuilder.toString());
        return true;
    }

    public GameState load() {
        if (!FileUtils.fileExists(SAVE_FILE)) {
            System.err.println("Save file does not exist.");
            return null;
        }
        String data = FileUtils.readFile(SAVE_FILE);
        String[] lines = data.split("\n");
        long seed = Long.parseLong(lines[0]);
        String[] avatarInfo = lines[1].split(" ");
        int avatarX = Integer.parseInt(avatarInfo[0]);
        int avatarY = Integer.parseInt(avatarInfo[1]);
        int avatarHealth = Integer.parseInt(avatarInfo[2]);
        List<int[]> heartPositions = new ArrayList<>();
        List<int[]> skullPositions = new ArrayList<>();

        for (int i = 2; i < lines.length; i++) {
            String[] line = lines[i].split(" ");
            int[] position = {Integer.parseInt(line[1]), Integer.parseInt(line[2])};
            if (line[0].equals("H")) {
                heartPositions.add(position);
            } else if (line[0].equals("S")) {
                skullPositions.add(position);
            }
        }
        return new GameState(seed, avatarX, avatarY, avatarHealth, heartPositions, skullPositions);
    }
}
