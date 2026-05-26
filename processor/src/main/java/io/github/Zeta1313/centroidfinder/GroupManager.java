package io.github.Zeta1313.centroidfinder;

import java.util.List;

public class GroupManager {
    public static Group findLargestGroup(List<Group> groups) {
        Group largest = groups.get(0);
        for (Group group : groups) {

            if (group.size() > largest.size()) {
                largest = group;
            }
        }
        return largest;
    }

    public static Group findClosestGroup(Group previous, List<Group> groups) {
        Group closest = groups.get(0);
        double closestDistance = centroidDistance(previous, closest);
        for (Group group : groups) {
            double distance =centroidDistance(previous, group);
            if (distance < closestDistance) {
                closest = group;
                closestDistance = distance;
            }
        }
        return closest;
    }
    public static double centroidDistance(Group a, Group b) {
        double dx = a.centroid().x() - b.centroid().x();
        double dy = a.centroid().y() - b.centroid().y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
