package core;

import tileengine.TETile;
import tileengine.Tileset;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        GameEngine gameEngine = new GameEngine(false); // false indicates no rendering
        SaveAndLoad saveAndLoad = new SaveAndLoad();
        long seed = 0;
        boolean buildingSeed = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (buildingSeed) {
                if (Character.isDigit(c)) {
                    seed = seed * 10 + Character.getNumericValue(c);
                } else if (c == 'S' || c == 's') {
                    gameEngine.initializeGameWithSeed(seed);
                    buildingSeed = false;
                }
            } else if (c == 'N' || c == 'n') {
                buildingSeed = true;
                seed = 0; // Start fresh with the seed building
            } else if (c == 'L' || c == 'l') {
                GameState loadedState = saveAndLoad.load();
                if (loadedState != null) {
                    gameEngine.initializeGameWithSeed(loadedState.getSeed());
                    gameEngine.getWorld().setAvatarPosition(loadedState.getAvatarX(),
                            loadedState.getAvatarY(), loadedState.getAvatarHealth());
                }
            } else if (c == 'Q' || c == 'q') {
                if (i > 0 && input.charAt(i - 1) == ':') {
                    saveAndLoad.save(gameEngine.getGameState());
                    // Do not break the loop, continue to return the current world state
                }
            } else {
                // Process movement characters
                Avatar.Direction dir = inputToDirection(c);
                if (dir != null) {
                    gameEngine.getWorld().getAvatar().move(dir, gameEngine.getWorld().getWorld());
                }
            }
        }
        // Return the final world state
        return gameEngine.getWorld().getWorld();
    }

    private static Avatar.Direction inputToDirection(char c) {
        switch (Character.toUpperCase(c)) {
            case 'W': return Avatar.Direction.UP;
            case 'A': return Avatar.Direction.LEFT;
            case 'S': return Avatar.Direction.DOWN;
            case 'D': return Avatar.Direction.RIGHT;
            default: return null;
        }
    }

    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
