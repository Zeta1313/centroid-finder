public class EuclideanColorDistance implements ColorDistanceFinder {
    /**
     * Returns the euclidean color distance between two hex RGB colors.
     * 
     * Each color is represented as a 24-bit integer in the form 0xRRGGBB, where
     * RR is the red component, GG is the green component, and BB is the blue component,
     * each ranging from 0 to 255.
     * 
     * The Euclidean color distance is calculated by treating each color as a point
     * in 3D space (red, green, blue) and applying the Euclidean distance formula:
     * 
     * sqrt((r1 - r2)^2 + (g1 - g2)^2 + (b1 - b2)^2)
     * 
     * This gives a measure of how visually different the two colors are.
     * 
     * @param colorA the first color as a 24-bit hex RGB integer
     * @param colorB the second color as a 24-bit hex RGB integer
     * @return the Euclidean distance between the two colors
     */
    @Override
    public double distance(int colorA, int colorB) {
        int r1 = findHex(colorA, 0);
        int r2 = findHex(colorB, 0);
        int g1 = findHex(colorA, 4);
        int g2 = findHex(colorB, 4);
        int b1 = findHex(colorA, 8);
        int b2 = findHex(colorB, 8);

        return Math.sqrt((r1-r2)*(r1-r2)+(g1-g2)*(g1-g2)+(b1-b2)*(b1-b2));
    }

    private int findHex(int color, int start) {
        int output = 0;
        for (int i = 1; i < 5; i++) {
            String temp = String.valueOf(color);
            output = output*10 + (temp.charAt(start+i));
        }
        return output;
    }
}
