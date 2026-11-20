package gameworld.model;

/** Yellow triangle, restricted to vertical travel. */
public final class TypeA extends AxisGameObject {
    public TypeA(int x, int y) { super('A', x, y, Direction.DOWN); }
}
