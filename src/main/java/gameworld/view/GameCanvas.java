package gameworld.view;

import gameworld.model.GameObject;
import gameworld.model.GameWorld;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/** Paints each object and puts the active object's frame above overlapping sprites. */
public final class GameCanvas extends JPanel {
    private static final long serialVersionUID = 1L;
    private final GameWorld world;
    private final SpriteAtlas sprites;
    private final SelectionFrame frame = new SelectionFrame();

    public GameCanvas(GameWorld world, SpriteAtlas sprites) {
        this.world = world;
        this.sprites = sprites;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 700));
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        getAccessibleContext().setAccessibleName("Game World canvas");
        getAccessibleContext().setAccessibleDescription("Tab selects an object. Arrow keys move it.");
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D drawing = (Graphics2D) g.create();
        try {
            drawing.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            GameObject selected = world.selected();
            for (GameObject object : world.objects()) {
                if (object != selected) drawSprite(drawing, object);
            }
            drawSprite(drawing, selected);
            frame.draw(drawing, selected.x(), selected.y(), GameWorld.spriteSize());
        } finally {
            drawing.dispose();
        }
    }

    private void drawSprite(Graphics2D g, GameObject object) {
        BufferedImage image = sprites.imageFor(object.spriteType(), object.facing());
        int size = GameWorld.spriteSize();
        double scale = (size - 8.0) / Math.max(image.getWidth(), image.getHeight());
        int width = (int) Math.round(image.getWidth() * scale);
        int height = (int) Math.round(image.getHeight() * scale);
        g.drawImage(image, object.x() + (size - width) / 2, object.y() + (size - height) / 2, width, height, null);
    }
}
