package gameworld.model;

import java.util.List;

/** Owns the objects and the one selected position in their fixed cycling order. */
public final class GameWorld {
    private static final int SPRITE_SIZE = 104;
    private final List<GameObject> objects;
    private int selectedIndex;
    private int maxX;
    private int maxY;

    public GameWorld(int width, int height) {
        int w = Math.max(SPRITE_SIZE, width) - SPRITE_SIZE;
        int h = Math.max(SPRITE_SIZE, height) - SPRITE_SIZE;
        objects = List.of(
                new TypeD(w / 2, (int) (h * 2L / 3)),
                new TypeA(w / 5, h / 4),
                new TypeB(w / 7, h),
                new TypeC(w / 7, h / 6));
        resize(width, height);
    }

    public static int spriteSize() { return SPRITE_SIZE; }

    public List<GameObject> objects() { return objects; }
    public GameObject selected() { return objects.get(selectedIndex); }

    public void selectNext() { selectedIndex = (selectedIndex + 1) % objects.size(); }

    public boolean acceptsDirection(Direction direction) {
        return direction != null && selected().allows(direction);
    }

    public void moveSelected(Direction direction) { selected().advanceManually(direction, maxX, maxY); }

    public void tick() {
        for (GameObject object : objects) {
            if (object != selected()) object.advanceAutomatically(maxX, maxY);
        }
    }

    public void resize(int width, int height) {
        maxX = Math.max(SPRITE_SIZE, width) - SPRITE_SIZE;
        maxY = Math.max(SPRITE_SIZE, height) - SPRITE_SIZE;
        for (GameObject object : objects) object.fit(maxX, maxY);
    }
}
