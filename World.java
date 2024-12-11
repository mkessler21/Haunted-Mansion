package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// World generations for rooms, hallways, enemies(skulls), hearts, and the key

public class World {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private TETile[][] world;
    private Random random;
    private Avatar avatar;
    private long seed;
    private int skullCount;
    private GameEngine gameEngine;
    private int[] keyPos;
    public World(GameEngine gameEngine, long seed) {
        this.gameEngine = gameEngine;
        this.random = new Random(seed);
        this.world = new TETile[WIDTH][HEIGHT];
        this.seed = seed;
        keyPos = new int[2];
        fillWorldWithNothing();
        generateRoomsAndHallways();
        addWalls();
        placeHeartTiles(10);
        placeSkullTiles(40);
    }

    public void moveSkullTiles() {
        List<int[]> newPositions = new ArrayList<>();
        List<int[]> skullPositions = getSkullPositions();

        for (int[] skull : skullPositions) {
            List<int[]> potentialPositions = getValidMoves(skull);
            Collections.shuffle(potentialPositions, random); // @source this code was generated by ChatGPT for random positions

            for (int[] pos : potentialPositions) {
                if (!containsPosition(newPositions, pos)) {
                    newPositions.add(pos);
                    if (world[pos[0]][pos[1]] == Tileset.AVATAR) {
                        world[skull[0]][skull[1]] = Tileset.FLOOR;
                        avatar.decreaseHealth();
                        return;
                    }
                    world[pos[0]][pos[1]] = Tileset.SKULL;
                    world[skull[0]][skull[1]] = Tileset.FLOOR;
                    break;
                }
            }
        }
    }

    public List<int[]> getSkullPositions() {
        List<int[]> positions = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j] == Tileset.SKULL) {
                    positions.add(new int[]{i, j});
                }
            }
        }
        return positions;
    }

    public List<int[]> getHeartPositions() {
        List<int[]> positions = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j] == Tileset.HEART) {
                    positions.add(new int[]{i, j});
                }
            }
        }
        return positions;
    }

    private List<int[]> getValidMoves(int[] skull) {
        List<int[]> moves = new ArrayList<>();
        int[] directions = {-2, -1, 0, 1, 2};
        for (int dx : directions) {
            for (int dy : directions) {
                if (dx != 0 || dy != 0) {
                    int nx = skull[0] + dx;
                    int ny = skull[1] + dy;
                    if (isFloorTileOrAvatar(nx, ny)) {
                        moves.add(new int[]{nx, ny});
                    }
                }
            }
        }
        return moves;
    }

    private boolean containsPosition(List<int[]> list, int[] pos) {
        for (int[] item : list) {
            if (item[0] == pos[0] && item[1] == pos[1]) {
                return true;
            }
        }
        return false;
    }

    private boolean isFloorTileOrAvatar(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT
                && (world[x][y] == Tileset.FLOOR
                || world[x][y] == Tileset.AVATAR);
    }

    private void placeSkullTiles(int numberOfSkulls) {
        skullCount = 0;
        while (skullCount < numberOfSkulls) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            if (world[x][y].equals(Tileset.FLOOR)) {
                world[x][y] = Tileset.SKULL;
                skullCount++;
            }
        }
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void initializeAvatar() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y].equals(Tileset.FLOOR)) {
                    avatar = new Avatar(x, y, gameEngine);
                    world[x][y] = Tileset.AVATAR;
                    return; // Stop after placing the avatar
                }
            }
        }
    }

    private void fillWorldWithNothing() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    // @source This code was refined by ChatGPT. It generates rooms
    // and connects them through hallways.
    private void generateRoomsAndHallways() {
        int numberOfRooms = RandomUtils.uniform(random, 90, 100);

        int lastRoomCenterX = -1;
        int lastRoomCenterY = -1;

        for (int i = 0; i < numberOfRooms; i++) {
            int roomWidth = RandomUtils.uniform(random, 3, 10);
            int roomHeight = RandomUtils.uniform(random, 3, 10);
            int roomX = RandomUtils.uniform(random, 1, WIDTH - roomWidth - 2);
            int roomY = RandomUtils.uniform(random, 1, HEIGHT - roomHeight - 2);

            if (canPlaceRoom(roomX, roomY, roomWidth, roomHeight)) {
                for (int x = roomX; x < roomX + roomWidth; x++) {
                    for (int y = roomY; y < roomY + roomHeight; y++) {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
                int currentRoomCenterX = roomX + roomWidth / 2;
                int currentRoomCenterY = roomY + roomHeight / 2;

                if (lastRoomCenterX != -1 && lastRoomCenterY != -1) {
                    connectRooms(lastRoomCenterX, lastRoomCenterY, currentRoomCenterX, currentRoomCenterY);
                }

                lastRoomCenterX = currentRoomCenterX;
                lastRoomCenterY = currentRoomCenterY;
            }
        }
    }



    private void connectRooms(int startX, int startY, int endX, int endY) {
        int horizontalDirection = Integer.compare(endX, startX);
        for (int x = startX; x != endX; x += horizontalDirection) {
            world[x][startY] = Tileset.FLOOR;
        }
        int verticalDirection = Integer.compare(endY, startY);
        for (int y = startY; y != endY; y += verticalDirection) {
            world[endX][y] = Tileset.FLOOR;
        }
    }

    private boolean canPlaceRoom(int x, int y, int width, int height) {
        for (int i = x - 1; i <= x + width; i++) {
            for (int j = y - 1; j <= y + height; j++) {
                if (!isInBounds(i, j) || !world[i][j].equals(Tileset.NOTHING)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void addWalls() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y].equals(Tileset.FLOOR)) {
                    addWallsForTile(x, y);
                }
            }
        }
    }

    private void addWallsForTile(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isInBounds(x + i, y + j) && world[x + i][y + j].equals(Tileset.NOTHING)) {
                    world[x + i][y + j] = Tileset.WALL;
                }
            }
        }
    }
    // In the World class
    public void setAvatarPosition(int x, int y, int health) {
        if (isInBounds(x, y) && world[x][y].equals(Tileset.FLOOR)) {
            if (avatar != null) { // Clear the old avatar position
                world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            }
            avatar = new Avatar(x, y, gameEngine);
            avatar.setHealth(health); // Restore the avatar's health
            world[x][y] = Tileset.AVATAR;
        }
    }

    public void placeHeartsAndSkulls(List<int[]> heartPositions, List<int[]> skullPositions) {
        // Clear all hearts and skulls first
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y].equals(Tileset.HEART) || world[x][y].equals(Tileset.SKULL)) {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }

        for (int[] pos : heartPositions) {
            int x = pos[0];
            int y = pos[1];
            world[x][y] = Tileset.HEART;
        }

        for (int[] pos : skullPositions) {
            int x = pos[0];
            int y = pos[1];
            world[x][y] = Tileset.SKULL;
        }
    }

    private void placeHeartTiles(int numberOfHearts) {
        int placedHearts = 0;
        while (placedHearts < numberOfHearts) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);

            // Check if the selected tile is a floor and not already a heart
            if (world[x][y].equals(Tileset.FLOOR)) {
                world[x][y] = Tileset.HEART; // Set the tile at position (x, y) to a heart
                placedHearts++;
            }
        }
    }

    private boolean isInBounds(int x, int y) {
        return x > 0 && x < WIDTH - 1 && y > 0 && y < HEIGHT - 1;
    }

    // @source This code was generated by ChatGPT. It contains
    // logic to place the key  far away from the avatar.
    public void placeKey() {
        if (avatar == null) {
            return;
        }
        int avatarX = avatar.getX();
        int avatarY = avatar.getY();
        double maxDistance = -1;
        int keyX = -1;
        int keyY = -1;

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y].equals(Tileset.FLOOR)) {
                    double distance = Math.sqrt(Math.pow(x - avatarX, 2) + Math.pow(y - avatarY, 2));
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        keyX = x;
                        keyY = y;
                    }
                }
            }
        }

        if (keyX != -1 && keyY != -1) {
            world[keyX][keyY] = Tileset.KEY;
        }
        keyPos[0] = keyX;
        keyPos[1] = keyY;
    }

    public int[] getKeyPosition() {
        return keyPos;
    }

    public TETile[][] getWorld() {
        return world;
    }
    public long getSeed() {
        return this.seed;
    }
}