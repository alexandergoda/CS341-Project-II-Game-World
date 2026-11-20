package gameworld;

import gameworld.controller.GameController;
import gameworld.model.GameWorld;
import gameworld.view.GameCanvas;
import gameworld.view.SpriteAtlas;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Assembles the model, view and controller for one desktop window. */
public final class GameWorldApp {
    private GameWorldApp() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GameWorld world = new GameWorld(800, 700);
                GameCanvas canvas = new GameCanvas(world, new SpriteAtlas());
                GameController controller = new GameController(world, canvas);
                JFrame window = new JFrame("Animation Canvas · Game World");
                window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                window.setContentPane(canvas);
                window.setMinimumSize(new Dimension(320, 280));
                window.pack();
                window.setLocationRelativeTo(null);
                window.addWindowListener(new WindowAdapter() {
                    @Override public void windowOpened(WindowEvent e) {
                        canvas.requestFocusInWindow();
                        controller.start();
                    }
                    @Override public void windowClosed(WindowEvent e) { controller.stop(); }
                });
                window.setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Game World artwork", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
