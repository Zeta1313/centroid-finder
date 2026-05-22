package io.github.Zeta1313.centroidfinder;

import java.util.List;
import java.awt.image.BufferedImage;

public class FileReader {
    private DistanceImageBinarizer b;
    private DfsBinaryGroupFinder gf;

    public FileReader(DistanceImageBinarizer b, DfsBinaryGroupFinder gf) {
        this.b = b;
        this.gf = gf;
    }

    public List<Group> Readfile(BufferedImage image) {
        return gf.findConnectedGroups(b.toBinaryArray(image));
    }
}
