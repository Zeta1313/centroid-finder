import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.awt.image.BufferedImage;

public class CentroidTesting {

    @Test
    void testNullImageThrowsException() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        assertThrows(NullPointerException.class, () -> {
            finder.findConnectedGroups(null);
        });
    }

    @Test
    void testInvalidArrayThrowsException() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        int[][] invalidImage = {
            {1, 0, 1},
            {1, 0},
            {0, 1, 1}
        };

        assertThrows(IllegalArgumentException.class, () -> {
            finder.findConnectedGroups(invalidImage);
        });
    }

    @Test
    void testSinglePixelGroup() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        int[][] image = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(1, groups.size());

        Group result = groups.get(0);

        assertEquals(1, result.size());
        assertEquals(1, result.centroid().x());
        assertEquals(1, result.centroid().y());
    }

    @Test
    void testMultiPixelGroupCentroidAndSize() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();
        int[][] image = {
            {1, 1, 0},
            {1, 1, 0},
            {0, 0, 0}
        };

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(1, groups.size());

        Group result = groups.get(0);

        assertEquals(4, result.size());
        assertEquals(0, result.centroid().x());
        assertEquals(0, result.centroid().y());
    }

    @Test
    void testLargeImageWithMultipleGroups() {
        DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();

        int[][] image = new int[50][50];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                image[i][j] = 1;
            }
        }

        for (int i = 20; i < 30; i++) {
            for (int j = 20; j < 30; j++) {
                image[i][j] = 1;
            }
        }

        for (int i = 40; i < 45; i++) {
            for (int j = 40; j < 45; j++) {
                image[i][j] = 1;
            }
        }

        List<Group> groups = finder.findConnectedGroups(image);

        assertEquals(3, groups.size());

        groups.sort(null);

        assertEquals(25, groups.get(0).size());
        assertEquals(42, groups.get(0).centroid().x());
        assertEquals(42, groups.get(0).centroid().y());

        assertEquals(100, groups.get(1).size());
        assertEquals(4, groups.get(1).centroid().x());
        assertEquals(4, groups.get(1).centroid().y());

        assertEquals(100, groups.get(2).size());
        assertEquals(24, groups.get(2).centroid().x());
        assertEquals(24, groups.get(2).centroid().y());
    }


    @Test
    public void testSameColorDistanceZero() {
        EuclideanColorDistance calculator = new EuclideanColorDistance();
        assertEquals(0.0, calculator.distance(0xFFFFFF, 0xFFFFFF), 0.0001);
    }

    @Test
    public void testBlackToWhiteDistance() {
        EuclideanColorDistance calculator = new EuclideanColorDistance();
        double expected = Math.sqrt((255 * 255) + (255 * 255) + (255 * 255));
        assertEquals(expected, calculator.distance(0x000000, 0xFFFFFF), 0.0001);
    }

    @Test
    public void testRedToGreenDistance() {
        EuclideanColorDistance calculator = new EuclideanColorDistance();
        double expected = Math.sqrt((255 * 255) + (255 * 255));
        assertEquals(expected, calculator.distance(0xFF0000, 0x00FF00), 0.0001);
    }

    @Test
    public void testBlueToYellowDistance() {
        EuclideanColorDistance calculator = new EuclideanColorDistance();
        double expected = Math.sqrt((255 * 255) + (255 * 255) + (255 * 255));
        assertEquals(expected, calculator.distance(0x0000FF, 0xFFFF00), 0.0001);
    }

    @Test
    public void testSymmetry() {
        EuclideanColorDistance calculator = new EuclideanColorDistance();
        double forward = calculator.distance(0x123456, 0x654321);
        double backward = calculator.distance(0x654321, 0x123456);
        assertEquals(forward, backward, 0.0001);
    }
    @Test
    void testToBinaryArray_allWithinThreshold() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 10);
        image.setRGB(1, 0, 12);
        image.setRGB(0, 1, 9);
        image.setRGB(1, 1, 11);

        DistanceImageBinarizer binarizer =
                new DistanceImageBinarizer(new EuclideanColorDistance(), 10, 5);

        int[][] result = binarizer.toBinaryArray(image);

        assertArrayEquals(new int[][]{
                {1, 1},
                {1, 1}
        }, result);
    }

    @Test
    void testToBinaryArray_allOutsideThreshold() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 100);
        image.setRGB(1, 0, 120);
        image.setRGB(0, 1, 90);
        image.setRGB(1, 1, 110);

        DistanceImageBinarizer binarizer =
                new DistanceImageBinarizer(new EuclideanColorDistance(), 10, 5);

        int[][] result = binarizer.toBinaryArray(image);

        assertArrayEquals(new int[][]{
                {0, 0},
                {0, 0}
        }, result);
    }

    @Test
    void testToBinaryArray_mixedValues() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 10);
        image.setRGB(1, 0, 20);
        image.setRGB(0, 1, 8);
        image.setRGB(1, 1, 30);

        DistanceImageBinarizer binarizer =
                new DistanceImageBinarizer(new EuclideanColorDistance(), 10, 5);

        int[][] result = binarizer.toBinaryArray(image);

        assertArrayEquals(new int[][]{
                {1, 0},
                {1, 0}
        }, result);
    }

    @Test
    void testToBufferedImage_allWhite() {
        int[][] input = {
                {1, 1},
                {1, 1}
        };

        DistanceImageBinarizer binarizer =
                new DistanceImageBinarizer(new EuclideanColorDistance(), 0, 0);

        BufferedImage result = binarizer.toBufferedImage(input);

        assertEquals(0xFFFFFF, result.getRGB(0, 0) & 0xFFFFFF);
        assertEquals(0xFFFFFF, result.getRGB(1, 0) & 0xFFFFFF);
        assertEquals(0xFFFFFF, result.getRGB(0, 1) & 0xFFFFFF);
        assertEquals(0xFFFFFF, result.getRGB(1, 1) & 0xFFFFFF);
    }

    @Test
    void testToBufferedImage_allBlack() {
        int[][] input = {
                {0, 0},
                {0, 0}
        };

        DistanceImageBinarizer binarizer =
                new DistanceImageBinarizer(new EuclideanColorDistance(), 0, 0);

        BufferedImage result = binarizer.toBufferedImage(input);

        assertEquals(0x000000, result.getRGB(0, 0) & 0xFFFFFF);
        assertEquals(0x000000, result.getRGB(1, 0) & 0xFFFFFF);
        assertEquals(0x000000, result.getRGB(0, 1) & 0xFFFFFF);
        assertEquals(0x000000, result.getRGB(1, 1) & 0xFFFFFF);
    }

    @Test
    void testToBufferedImage_mixedValues() {
        int[][] input = {
                {1, 0},
                {0, 1}
        };

        DistanceImageBinarizer binarizer =
                new DistanceImageBinarizer(new EuclideanColorDistance(), 0, 0);

        BufferedImage result = binarizer.toBufferedImage(input);

        assertEquals(0xFFFFFF, result.getRGB(0, 0) & 0xFFFFFF);
        assertEquals(0x000000, result.getRGB(1, 0) & 0xFFFFFF);
        assertEquals(0x000000, result.getRGB(0, 1) & 0xFFFFFF);
        assertEquals(0xFFFFFF, result.getRGB(1, 1) & 0xFFFFFF);
    }
}