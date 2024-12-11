package core;
import tileengine.TETile;
import tileengine.Tileset;

// Avatar (player) attributes and movement logic
public class Avatar {
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    private int x, y;
    private int health = 5;
    private final int maxHealth = 5;
    private GameEngine gameEngine;
    private TETile avatarTile = Tileset.AVATAR;

    public Avatar(int startX, int startY, GameEngine gameEngine) {
        this.x = startX;
        this.y = startY;
        this.gameEngine = gameEngine;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void move(Direction direction, TETile[][] world) {
        int newX = x;
        int newY = y;

        // @source This code was generated by ChatGPT. It contains the cases for
        // the Avatar moving up, down, left, or right.
        switch (direction) {
            case UP:
                newY += 1;
                break;
            case DOWN:
                newY -= 1;
                break;
            case LEFT:
                newX -= 1;
                break;
            case RIGHT:
                newX += 1;
                break;
            default:
                break;
        }

        if (canMoveTo(newX, newY, world)) {
            TETile currentTile = world[newX][newY];
            interactWithTile(world[newX][newY]);
            updatePosition(newX, newY, world);
            gameEngine.updateHUD(currentTile.description());
        }
    }

    public void increaseHealth() {
        if (health < maxHealth) {
            health++;
        }
    }

    public void decreaseHealth() {
        if (health > 0) {
            health--;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    private void updatePosition(int newX, int newY, TETile[][] world) {
        world[x][y] = Tileset.FLOOR;
        x = newX;
        y = newY;
        world[x][y] = Tileset.AVATAR;
    }

    private void interactWithTile(TETile tile) {
        if (tile.description().equals("king boo")) {
            this.decreaseHealth();
        } else if (tile.description().equals("heart")) {
            this.increaseHealth();
        }
    }

    private boolean canMoveTo(int newX, int newY, TETile[][] world) {
        return newX >= 0 && newX < World.WIDTH
                && newY >= 0 && newY < World.HEIGHT
                && (world[newX][newY].equals(Tileset.FLOOR)
                || world[newX][newY].equals(Tileset.HEART)
                || world[newX][newY].equals(Tileset.SKULL)
                || world[newX][newY].equals(Tileset.KEY));
    }
}