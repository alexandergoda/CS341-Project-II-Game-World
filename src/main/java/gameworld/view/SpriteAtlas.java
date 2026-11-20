package gameworld.view;

import gameworld.model.Direction;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/** Loads the course PNGs once and translates logical headings to the actual artwork. */
public final class SpriteAtlas {
    private final Map<Character, Map<Direction, BufferedImage>> images = new HashMap<>();

    public SpriteAtlas() throws IOException {
        load('A', Direction.UP, Direction.DOWN);
        load('B', Direction.values());
        load('C', Direction.LEFT, Direction.RIGHT);
        load('D', Direction.values());
    }

    private void load(char type, Direction... directions) throws IOException {
        Map<Direction, BufferedImage> faces = new EnumMap<>(Direction.class);
        for (Direction dir : directions) {
            String suffix;
            switch (dir) {
                case UP: suffix = type == 'B' ? "_Up" : "Up"; break;
                case DOWN: suffix = "Down"; break;
                // The original horizontal filenames are opposite the visible artwork.
                case LEFT: suffix = "Right"; break;
                case RIGHT: suffix = "Left"; break;
                default: throw new AssertionError(dir);
            }
            String path = "/sprites/Type_" + type + "_" + suffix + ".png";
            try (InputStream stream = SpriteAtlas.class.getResourceAsStream(path)) {
                if (stream == null) throw new IOException("Missing course sprite " + path);
                BufferedImage image = ImageIO.read(stream);
                if (image == null) throw new IOException("Unreadable course sprite " + path);
                faces.put(dir, image);
            }
        }
        images.put(type, faces);
    }

    public BufferedImage imageFor(char type, Direction direction) {
        Map<Direction, BufferedImage> faces = images.get(type);
        if (faces == null || !faces.containsKey(direction)) {
            throw new IllegalArgumentException("Unsupported sprite " + type + " facing " + direction);
        }
        return faces.get(direction);
    }
}
