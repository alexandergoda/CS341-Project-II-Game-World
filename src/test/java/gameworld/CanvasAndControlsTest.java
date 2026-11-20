package gameworld;

import gameworld.controller.GameController;
import gameworld.model.Direction;
import gameworld.model.GameObject;
import gameworld.model.GameWorld;
import gameworld.view.GameCanvas;
import gameworld.view.SpriteAtlas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CanvasAndControlsTest {
    @Test void allTwelveHeadingsUseTheCorrectCourseArtwork() throws Exception {
        SpriteAtlas atlas = new SpriteAtlas();
        String[][] cases = {
            {"A","UP","Type_A_Up"}, {"A","DOWN","Type_A_Down"},
            {"B","UP","Type_B__Up"}, {"B","DOWN","Type_B_Down"},
            {"B","LEFT","Type_B_Right"}, {"B","RIGHT","Type_B_Left"},
            {"C","LEFT","Type_C_Right"}, {"C","RIGHT","Type_C_Left"},
            {"D","UP","Type_D_Up"}, {"D","DOWN","Type_D_Down"},
            {"D","LEFT","Type_D_Right"}, {"D","RIGHT","Type_D_Left"}
        };
        for (String[] row : cases) {
            String file = "/sprites/" + row[2] + ".png";
            try (InputStream in = getClass().getResourceAsStream(file)) {
                assertNotNull(in, file);
                BufferedImage expected = ImageIO.read(in);
                BufferedImage actual = atlas.imageFor(row[0].charAt(0), Direction.valueOf(row[1]));
                int w = expected.getWidth(), h = expected.getHeight();
                assertEquals(w, actual.getWidth()); assertEquals(h, actual.getHeight());
                assertArrayEquals(expected.getRGB(0, 0, w, h, null, 0, w),
                        actual.getRGB(0, 0, w, h, null, 0, w), file);
            }
        }
    }

    @Test void pressedTabStopsThePreviousArrowAndCyclesOncePerPhysicalPress() throws Exception {
        SpriteAtlas atlas = new SpriteAtlas();
        SwingUtilities.invokeAndWait(() -> {
            GameWorld world = new GameWorld(800, 700);
            GameCanvas canvas = new GameCanvas(world, atlas);
            GameController controller = new GameController(world, canvas);
            key(canvas, KeyEvent.VK_DOWN, false);
            key(canvas, KeyEvent.VK_UP, false);
            key(canvas, KeyEvent.VK_TAB, false);
            key(canvas, KeyEvent.VK_TAB, false);
            GameObject yellow = world.selected();
            assertEquals('A', yellow.spriteType());
            int y = yellow.y();
            key(canvas, KeyEvent.VK_UP, false); // old key repeating after the handoff
            controller.step();
            assertEquals(y, yellow.y());
            key(canvas, KeyEvent.VK_UP, true);
            controller.step();
            assertEquals(y, yellow.y()); // Down is still held from the previous selection.
            key(canvas, KeyEvent.VK_DOWN, true);
            key(canvas, KeyEvent.VK_UP, false);
            assertTrue(yellow.y() < y);
            key(canvas, KeyEvent.VK_TAB, true);
            key(canvas, KeyEvent.VK_TAB, false);
            assertEquals('B', world.selected().spriteType());
        });
    }

    @Test void shortTapHeldArrowReleaseAndFocusLossHaveDistinctEffects() throws Exception {
        SpriteAtlas atlas = new SpriteAtlas();
        SwingUtilities.invokeAndWait(() -> {
            GameWorld world = new GameWorld(800, 700);
            GameCanvas canvas = new GameCanvas(world, atlas);
            GameController controller = new GameController(world, canvas);
            GameObject blue = world.selected();
            int start = blue.x();
            key(canvas, KeyEvent.VK_RIGHT, false);
            assertTrue(blue.x() > start);
            int afterTap = blue.x();
            controller.step();
            assertTrue(blue.x() > afterTap);
            key(canvas, KeyEvent.VK_RIGHT, true);
            int afterRelease = blue.x();
            controller.step();
            assertEquals(afterRelease, blue.x());
            key(canvas, KeyEvent.VK_LEFT, false);
            key(canvas, KeyEvent.VK_UP, false);
            for (FocusListener listener : canvas.getFocusListeners())
                listener.focusLost(new FocusEvent(canvas, FocusEvent.FOCUS_LOST));
            key(canvas, KeyEvent.VK_UP, true);
            int xAfterFocusLoss = blue.x();
            int yAfterFocusLoss = blue.y();
            controller.step();
            assertEquals(xAfterFocusLoss, blue.x());
            assertEquals(yAfterFocusLoss, blue.y());
        });
    }

    @Test void releasingAnArrowWhileShiftIsDownStopsMovement() throws Exception {
        SpriteAtlas atlas = new SpriteAtlas();
        SwingUtilities.invokeAndWait(() -> {
            GameWorld world = new GameWorld(800, 700);
            GameCanvas canvas = new GameCanvas(world, atlas);
            GameController controller = new GameController(world, canvas);
            GameObject blue = world.selected();
            key(canvas, KeyEvent.VK_RIGHT, false);
            key(canvas, KeyEvent.VK_RIGHT, true, InputEvent.SHIFT_DOWN_MASK);
            int releasedX = blue.x();
            controller.step();
            assertEquals(releasedX, blue.x());
            key(canvas, KeyEvent.VK_RIGHT, false);
            assertTrue(blue.x() > releasedX);
        });
    }

    @Test void releasingTabWhileShiftIsDownAllowsTheNextSelection() throws Exception {
        SpriteAtlas atlas = new SpriteAtlas();
        SwingUtilities.invokeAndWait(() -> {
            GameWorld world = new GameWorld(800, 700);
            GameCanvas canvas = new GameCanvas(world, atlas);
            new GameController(world, canvas);
            key(canvas, KeyEvent.VK_TAB, false);
            assertEquals('A', world.selected().spriteType());
            key(canvas, KeyEvent.VK_TAB, true, InputEvent.SHIFT_DOWN_MASK);
            key(canvas, KeyEvent.VK_TAB, false);
            assertEquals('B', world.selected().spriteType());
        });
    }

    @Test void forbiddenArrowDoesNotInterruptAHeldAllowedArrow() throws Exception {
        SpriteAtlas atlas = new SpriteAtlas();
        SwingUtilities.invokeAndWait(() -> {
            GameWorld world = new GameWorld(800, 700);
            GameCanvas canvas = new GameCanvas(world, atlas);
            GameController controller = new GameController(world, canvas);
            key(canvas, KeyEvent.VK_TAB, false);
            key(canvas, KeyEvent.VK_TAB, true);
            GameObject yellow = world.selected();
            key(canvas, KeyEvent.VK_DOWN, false);
            int x = yellow.x();
            int y = yellow.y();
            key(canvas, KeyEvent.VK_LEFT, false);
            controller.step();
            assertTrue(yellow.y() > y);
            assertEquals(x, yellow.x());
            y = yellow.y();
            key(canvas, KeyEvent.VK_LEFT, true);
            controller.step();
            assertTrue(yellow.y() > y);
            assertEquals(Direction.DOWN, yellow.facing());
        });
    }

    @Test void releasingTheNewestArrowResumesTheEarlierHeldDirection() throws Exception {
        SpriteAtlas atlas = new SpriteAtlas();
        SwingUtilities.invokeAndWait(() -> {
            GameWorld world = new GameWorld(800, 700);
            GameCanvas canvas = new GameCanvas(world, atlas);
            GameController controller = new GameController(world, canvas);
            GameObject blue = world.selected();
            key(canvas, KeyEvent.VK_RIGHT, false);
            key(canvas, KeyEvent.VK_DOWN, false);
            int x = blue.x();
            int y = blue.y();
            controller.step();
            assertTrue(blue.y() > y);
            assertEquals(x, blue.x());
            key(canvas, KeyEvent.VK_DOWN, true);
            y = blue.y();
            controller.step();
            assertTrue(blue.x() > x);
            assertEquals(y, blue.y());
            key(canvas, KeyEvent.VK_RIGHT, true);
            x = blue.x();
            controller.step();
            assertEquals(x, blue.x());
        });
    }

    @Test void frameMovesWithSelectionAndPreviousPositionIsRepainted() throws Exception {
        SpriteAtlas atlas = new SpriteAtlas();
        SwingUtilities.invokeAndWait(() -> {
            GameWorld world = new GameWorld(800, 700);
            GameCanvas canvas = new GameCanvas(world, atlas);
            canvas.setSize(800, 700);
            GameObject first = world.selected();
            int border = new Color(200, 43, 52).getRGB();
            assertEquals(border, sampleBorder(canvas, first));
            world.selectNext();
            assertNotEquals(border, sampleBorder(canvas, first));
            assertEquals(border, sampleBorder(canvas, world.selected()));
        });
    }

    private static int sampleBorder(GameCanvas canvas, GameObject object) {
        BufferedImage buffer = new BufferedImage(800, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffer.createGraphics();
        try { canvas.paint(g); } finally { g.dispose(); }
        return buffer.getRGB(object.x() + GameWorld.spriteSize() / 2, object.y() + 2);
    }

    private static void key(GameCanvas canvas, int code, boolean released) {
        key(canvas, code, released, 0);
    }

    private static void key(GameCanvas canvas, int code, boolean released, int modifiers) {
        int type = released ? KeyEvent.KEY_RELEASED : KeyEvent.KEY_PRESSED;
        KeyEvent event = new KeyEvent(canvas, type, System.currentTimeMillis(), modifiers, code, KeyEvent.CHAR_UNDEFINED);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(canvas, event);
    }
}
