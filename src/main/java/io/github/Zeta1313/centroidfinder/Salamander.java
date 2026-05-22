package io.github.Zeta1313.centroidfinder;

public class Salamander {
    private Group currentGroup;

    public Salamander(Group initialGroup) {
        this.currentGroup = initialGroup;
    }

    public Group getSalamander() {
        return currentGroup;
    }

    public void update(Group newGroup) {
        this.currentGroup = newGroup;
    }
}
