package gameworld.controller;

import gameworld.model.Direction;
import gameworld.model.GameWorld;
import gameworld.view.GameCanvas;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import javax.swing.Timer;

/** Converts keyboard actions and clock pulses into model updates on the Swing event thread. */
public final class GameController {
    private final GameWorld world;
    private final GameCanvas canvas;
    private final Timer clock;
    private final EnumSet<Direction> pressed = EnumSet.noneOf(Direction.class);
    private final Deque<Direction> held = new ArrayDeque<>();
    private boolean tabDown;

    public GameController(GameWorld world, GameCanvas canvas) {
        this.world = world;
        this.canvas = canvas;
        clock = new Timer(25, e -> step());
        canvas.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getModifiersEx() == 0) handleKey(e, false);
            }
            @Override public void keyReleased(KeyEvent e) {
                // A key pressed on its own may be released while Shift or another modifier is down.
                handleKey(e, true);
            }
        });
        canvas.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { resetKeys(); }
        });
        canvas.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                world.resize(canvas.getWidth(), canvas.getHeight());
                canvas.repaint();
            }
        });
    }

    private void handleKey(KeyEvent event, boolean released) {
        int key = event.getKeyCode();
        if (key == KeyEvent.VK_TAB) {
            if (!released && !tabDown) {
                world.selectNext();
                // Keys already down must be released before they can steer the new selection.
                held.clear();
                canvas.repaint();
            }
            tabDown = !released;
        } else {
            Direction direction = arrowDirection(key);
            if (direction == null) return;
            if (released) {
                pressed.remove(direction);
                held.remove(direction);
            } else if (pressed.add(direction) && world.acceptsDirection(direction)) {
                held.addFirst(direction);
                world.moveSelected(direction);
                canvas.repaint();
            }
        }
        event.consume();
    }

    private static Direction arrowDirection(int key) {
        switch (key) {
            case KeyEvent.VK_UP: return Direction.UP;
            case KeyEvent.VK_RIGHT: return Direction.RIGHT;
            case KeyEvent.VK_DOWN: return Direction.DOWN;
            case KeyEvent.VK_LEFT: return Direction.LEFT;
            default: return null;
        }
    }

    private void resetKeys() {
        pressed.clear();
        held.clear();
        tabDown = false;
    }

    public void step() {
        world.tick();
        Direction direction = held.peekFirst();
        if (direction != null) world.moveSelected(direction);
        canvas.repaint();
    }

    public void start() { clock.start(); }
    public void stop() { clock.stop(); resetKeys(); }
}
