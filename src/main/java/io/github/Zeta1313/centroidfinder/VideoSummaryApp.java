package io.github.Zeta1313.centroidfinder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import javax.imageio.ImageIO;

public class VideoSummaryApp {
    public static void main(String[] args) {
        try {
            if (args.length < 4) {
                System.out.println("Usage: java java -jar videoprocessor.jar inputPath outputCsv targetColor threshold");
                return;
            }
            String inputImagePath = args[0];
            String outputCSVPath = args[1];
            String hexTargetColor = args[2];
            int threshold = 0;
            try {
                threshold = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.err.println("Threshold must be an integer.");
                return;
            }
        int targetColor = 0;
        
        try {
            targetColor = Integer.parseInt(hexTargetColor, 16);
        } catch (NumberFormatException e) {
            System.err.println("Invalid hex target color. Please provide a color in RRGGBB format.");
            return;
        }
            
    }
    catch (Exception e) {
            e.printStackTrace();
    }
    }

    public Group findLargestGroup(List<Group> groups) {
        Group largest = groups.get(0);
        for (Group group : groups) {

            if (group.size() > largest.size()) {
                largest = group;
            }
        }
        return largest;
    }
}
