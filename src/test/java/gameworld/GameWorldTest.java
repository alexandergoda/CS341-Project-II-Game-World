package gameworld;

import gameworld.model.*;
import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class GameWorldTest {
    private final GameWorld world = new GameWorld(800, 700);

    @Test void firstCreatedObjectIsSelectedAndTabWrapsInCreationOrder() {
        assertSame(world.objects().get(0), world.selected());
        for (char expected : new char[] {'D', 'A', 'B', 'C', 'D'}) {
            assertEquals(expected, world.selected().spriteType());
            world.selectNext();
        }
        assertThrows(UnsupportedOperationException.class, () -> world.objects().clear());
    }

    @ParameterizedTest @EnumSource(Direction.class)
    void blueCanMoveInEveryCardinalDirection(Direction direction) {
        GameObject blue = world.selected();
        int x = blue.x(), y = blue.y();
        world.moveSelected(direction);
        assertEquals(direction.dx(), Integer.signum(blue.x() - x));
        assertEquals(direction.dy(), Integer.signum(blue.y() - y));
        assertEquals(direction, blue.facing());
    }

    @Test void blueIsIdleWithoutInputWhetherSelectedOrNot() {
        GameObject blue = world.selected();
        int x = blue.x(), y = blue.y();
        world.tick();
        world.selectNext();
        for (int i = 0; i < 80; i++) world.tick();
        assertEquals(x, blue.x());
        assertEquals(y, blue.y());
    }

    @Test void yellowPausesUnderControlRejectsHorizontalAndResumesVerticalTravel() {
        world.selectNext();
        GameObject yellow = world.selected();
        int x = yellow.x(), y = yellow.y();
        world.tick();
        world.moveSelected(Direction.LEFT);
        assertEquals(x, yellow.x());
        assertEquals(y, yellow.y());
        assertEquals(Direction.DOWN, yellow.facing());
        world.moveSelected(Direction.UP);
        assertTrue(yellow.y() < y);
        assertEquals(Direction.UP, yellow.facing());
        int releasedAt = yellow.y();
        world.selectNext();
        world.tick();
        assertTrue(yellow.y() > releasedAt);
        assertEquals(x, yellow.x());
    }

    @Test void pinkRejectsVerticalArrowsAndKeepsItsHeading() {
        for (int i = 0; i < 3; i++) world.selectNext();
        GameObject pink = world.selected();
        world.moveSelected(Direction.LEFT);
        int x = pink.x(), y = pink.y();
        world.moveSelected(Direction.DOWN);
        world.moveSelected(Direction.UP);
        assertEquals(x, pink.x());
        assertEquals(y, pink.y());
        assertEquals(Direction.LEFT, pink.facing());
        world.moveSelected(Direction.RIGHT);
        assertTrue(pink.x() > x);
    }

    @ParameterizedTest @EnumSource(Direction.class)
    void redAcceptsAllFourArrowsWhileSelected(Direction direction) {
        world.selectNext();
        world.selectNext();
        GameObject red = world.selected();
        world.moveSelected(Direction.UP); // Leave room for the downward test at the bottom edge.
        int x = red.x(), y = red.y();
        world.tick();
        assertEquals(x, red.x()); assertEquals(y, red.y());
        world.moveSelected(direction);
        assertEquals(direction.dx(), Integer.signum(red.x() - x));
        assertEquals(direction.dy(), Integer.signum(red.y() - y));
        assertEquals(direction, red.facing());
    }

    @Test void redPatrolsEachEdgeInOrderAndTurnsAtAllFourCorners() {
        int maxX = 47, maxY = 25;
        int size = GameWorld.spriteSize();
        GameWorld small = new GameWorld(size + maxX, size + maxY);
        GameObject red = small.objects().get(2);
        assertEquals(maxY, red.y());
        assertEquals(Direction.RIGHT, red.facing());
        EnumSet<Direction> corners = EnumSet.noneOf(Direction.class);
        for (int i = 0; i < 110; i++) {
            int x = red.x(), y = red.y();
            Direction expected;
            if (y == maxY && x < maxX) expected = Direction.RIGHT;
            else if (x == maxX && y > 0) expected = Direction.UP;
            else if (y == 0 && x > 0) expected = Direction.LEFT;
            else {
                assertEquals(0, x, "Red cut through the interior");
                expected = Direction.DOWN;
            }
            if ((x == 0 || x == maxX) && (y == 0 || y == maxY)) corners.add(expected);
            small.tick();
            assertEquals(expected, red.facing());
            assertEquals(expected.dx(), Integer.signum(red.x() - x));
            assertEquals(expected.dy(), Integer.signum(red.y() - y));
            assertTrue(Math.abs(red.x() - x) + Math.abs(red.y() - y) <= 3);
        }
        assertEquals(EnumSet.allOf(Direction.class), corners);
    }

    @Test void redRejoinsTheNearestEdgeAfterManualMovementAndResize() {
        GameObject red = world.objects().get(2);
        int bottom = red.y();
        world.selectNext();
        world.selectNext();
        world.moveSelected(Direction.UP);
        world.moveSelected(Direction.UP);
        int x = red.x(), y = red.y();
        world.tick();
        assertEquals(y, red.y());
        world.selectNext();
        for (int i = 0; i < 4; i++) {
            world.tick();
            assertEquals(x, red.x());
            assertEquals(y + 3 * (i + 1), red.y());
        }
        assertEquals(bottom, red.y());
        world.tick();
        assertEquals(x + 3, red.x());
        assertEquals(bottom, red.y());

        world.resize(800, 707);
        world.tick();
        assertEquals(x + 3, red.x());
        assertEquals(bottom + 3, red.y());
        world.tick();
        world.tick();
        assertEquals(bottom + 7, red.y());
        world.tick();
        assertEquals(x + 6, red.x());
    }

    @Test void redPatrolsBackAndForthWhenOnlyOneAxisHasRoom() {
        int size = GameWorld.spriteSize();
        for (boolean vertical : new boolean[] {true, false}) {
            int maxX = vertical ? 0 : 11, maxY = vertical ? 11 : 0;
            GameWorld line = new GameWorld(size + maxX, size + maxY);
            GameObject red = line.objects().get(2);
            EnumSet<Direction> directions = EnumSet.noneOf(Direction.class);
            for (int i = 0; i < 16; i++) {
                int x = red.x(), y = red.y();
                line.tick();
                assertEquals(red.facing().dx(), Integer.signum(red.x() - x));
                assertEquals(red.facing().dy(), Integer.signum(red.y() - y));
                assertTrue(red.x() >= 0 && red.x() <= maxX);
                assertTrue(red.y() >= 0 && red.y() <= maxY);
                directions.add(red.facing());
            }
            assertEquals(vertical ? EnumSet.of(Direction.UP, Direction.DOWN)
                    : EnumSet.of(Direction.LEFT, Direction.RIGHT), directions);
        }
    }

    @Test void yellowAndPurpleFollowTheLeftAndUpperLanes() {
        GameObject yellow = world.objects().get(1), purple = world.objects().get(3);
        int halfSprite = GameWorld.spriteSize() / 2;
        int laneX = yellow.x(), laneY = purple.y();
        assertEquals(0.24, (laneX + halfSprite) / 800.0, 0.015);
        assertEquals(0.22, (laneY + halfSprite) / 700.0, 0.015);
        for (int i = 0; i < 400; i++) world.tick();
        assertEquals(laneX, yellow.x());
        assertEquals(laneY, purple.y());
    }

    @Test void automaticTrianglesReverseAtTheirEdgesOnOnlyOneAxis() {
        int size = GameWorld.spriteSize();
        GameWorld small = new GameWorld(size + 9, size + 6);
        List<GameObject> objects = small.objects();
        GameObject yellow = objects.get(1), pink = objects.get(3);
        int fixedX = yellow.x(), fixedY = pink.y();
        EnumSet<Direction> yellowFaces = EnumSet.noneOf(Direction.class);
        EnumSet<Direction> pinkFaces = EnumSet.noneOf(Direction.class);
        for (int i = 0; i < 20; i++) {
            small.tick();
            assertEquals(fixedX, yellow.x()); assertEquals(fixedY, pink.y());
            assertTrue(yellow.y() >= 0 && yellow.y() <= 6);
            assertTrue(pink.x() >= 0 && pink.x() <= 9);
            yellowFaces.add(yellow.facing()); pinkFaces.add(pink.facing());
        }
        assertEquals(EnumSet.of(Direction.UP, Direction.DOWN), yellowFaces);
        assertEquals(EnumSet.of(Direction.LEFT, Direction.RIGHT), pinkFaces);
    }

    @Test void manualMovementClampsToAllFourCanvasEdges() {
        GameObject blue = world.selected();
        for (Direction direction : Direction.values()) {
            for (int i = 0; i < 200; i++) world.moveSelected(direction);
            switch (direction) {
                case UP: assertEquals(0, blue.y()); break;
                case RIGHT: assertEquals(800 - GameWorld.spriteSize(), blue.x()); break;
                case DOWN: assertEquals(700 - GameWorld.spriteSize(), blue.y()); break;
                case LEFT: assertEquals(0, blue.x()); break;
            }
        }
    }

    @Test void resizingClampsEveryObjectAndTravelRestartsWhenSpaceReturns() {
        GameObject red = world.objects().get(2);
        world.resize(1, 0);
        for (int i = 0; i < 30; i++) world.tick();
        for (GameObject object : world.objects()) {
            assertEquals(0, object.x()); assertEquals(0, object.y());
        }
        world.resize(800, 700);
        world.tick();
        assertTrue(red.x() != 0 || red.y() != 0);
    }
}
