import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DfsBinaryGroupFinder implements BinaryGroupFinder {
   /**
    * Finds connected pixel groups of 1s in an integer array representing a binary image.
    * 
    * The input is a non-empty rectangular 2D array containing only 1s and 0s.
    * If the array or any of its subarrays are null, a NullPointerException
    * is thrown. If the array is otherwise invalid, an IllegalArgumentException
    * is thrown.
    *
    * Pixels are considered connected vertically and horizontally, NOT diagonally.
    * The top-left cell of the array (row:0, column:0) is considered to be coordinate
    * (x:0, y:0). Y increases downward and X increases to the right. For example,
    * (row:4, column:7) corresponds to (x:7, y:4).
    *
    * The method returns a list of sorted groups. The group's size is the number 
    * of pixels in the group. The centroid of the group
    * is computed as the average of each of the pixel locations across each dimension.
    * For example, the x coordinate of the centroid is the sum of all the x
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * Similarly, the y coordinate of the centroid is the sum of all the y
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * The division should be done as INTEGER DIVISION.
    *
    * The groups are sorted in DESCENDING order according to Group's compareTo method.
    * 
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */
    @Override
    public List<Group> findConnectedGroups(int[][] image) {
        // image variable is a 2d array that has a bunch of 1's and 0's
        // if image is null, throw NullPointerException
        // if array is invalid, throw IllegalArgumentException.

        // the group datatype contains int size, coordinates centroid.
            // the coordinates datatype contains int x, int y.
        //int size: should contain all the number of pixels in the current iteration cell
        //coordinates centroid: should contain basically the center coordinates of the current iteration cell
        List<Group> result = new ArrayList<>();
        if (image == null) {
            throw new NullPointerException();
        }
        for (int[] row : image) {
            if (row == null || row.length != image[0].length) {
                throw new IllegalArgumentException("Array must be rectangular and non-null.");
            }
        }
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int r = 0; r < image.length; r++) {
            for (int c = 0; c < image[0].length; c++) {
                if (image[r][c] == 1) {
                    // List<Group> current = findConnectedGroups(image, r, c, directions);
                    // result.addAll()
                    int[] currentInfo = dfs(image, r, c, directions);
                    // we need to create a coordinate class using the avg of sumx and sumy, 
                    // create a group class using int size and coordinate class
                    //once we created a group, add that group into the List of Group (result)
                    int size = currentInfo[0];
                    int avgX = currentInfo[1] / size;
                    int avgY = currentInfo[2] / size;
                    Coordinate centroid = new Coordinate(avgX, avgY);
                    Group currentGroup = new Group(size, centroid);
                    result.add(currentGroup);
                    
                }
            }
        }
        result.sort(Collections.reverseOrder());
        return result;
    }
    // int size: a counter for size
    // int sumx: a sum of all the x coordinate
    // int sumy: a sum of all the y coordinate
    //int[] {size, sumx, sumy}.
    public int[] dfs(int[][] image, int r, int c, int[][] moves) {
        if (r < 0 || r >= image.length ||
            c < 0 || c >= image[0].length ||
            image[r][c] == 0) {
                return new int[]{0,0,0};
        }
        int[] result = new int[]{1, c, r};
        image[r][c] = 0;

        for (int[] move : moves) {
            int curR = move[0] + r;
            int curC = move[1] + c;

            int[] current = dfs(image, curR, curC, moves);

            result[0] += current[0];
            result[1] += current[1];
            result[2] += current[2];
            
        }
        return result;
    }
    
}
