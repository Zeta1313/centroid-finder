package io.github.Zeta1313.centroidfinder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.imageio.ImageIO;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class VideoSummaryApp {
    public static void main(String[] args) {
        try {
            if (args.length < 4) {
                System.out.println("Usage: java java -jar videoprocessor.jar inputPath outputCsv targetColor threshold");
                return;
            }
            String inputPath = args[0];
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

        ImageGroupFinder groupFinder = new BinarizingImageGroupFinder(
            new DistanceImageBinarizer(new EuclideanColorDistance(), targetColor, threshold),
            new DfsBinaryGroupFinder()
        );

        // File file = new File(inputPath);
        // FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(file));
        // Picture picture;
        // while (null != (picture = grab.getNativeFrame())) {
        //     System.out.println(picture.getWidth() + "x" + picture.getHeight() + " " + picture.getColor());
        // }
        //THIS IS A WORKING TEST, rebuild your maven, then run "java -jar target/videoprocessor.jar video.mp4 output.csv FFA200 164"
        try (var channel = NIOUtils.readableChannel(new File(inputPath));
            PrintWriter writer = new PrintWriter(outputCSVPath)) {

            FrameGrab grab = FrameGrab.createFrameGrab(channel);
            // ... process frames ...
            int frameIndex = 0;
            Picture picture;
            while ((picture = grab.getNativeFrame()) != null) {
                BufferedImage frame = AWTUtil.toBufferedImage(picture);

                // quick console check
                System.out.println("Read frame " + frameIndex + " size=" + frame.getWidth() + "x" + frame.getHeight());

                // save first 5 frames as PNG to inspect visually
                if (frameIndex < 5) {
                    try {
                        ImageIO.write(frame, "png", new File(String.format("debug-frame-%04d.png", frameIndex)));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                // your processing
                List<Group> groups = groupFinder.findConnectedGroups(frame);
                frameIndex++;
            }

        } catch (Exception e) {
            e.printStackTrace();
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
