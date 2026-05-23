package io.github.Zeta1313.centroidfinder;

import java.awt.image.BufferedImage;
import java.util.List;

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

        //This works by taking the color BufferedImage and binarizing it, and turning it back into BufferedImage but black and white
        //THIS IS A WORKING TEST, rebuild your maven, then run "java -jar target/videoprocessor.jar video.mp4 output.csv FFA200 164"
            Grabber grabber = new Grabber(inputPath);
            CSVExport exporter = new CSVExport(outputCSVPath);
            Salamander salamander = null;
            BufferedImage frame;
            int frameIndex = 0;
            while ((frame = grabber.nextFrame()) != null) {
                List<Group> groups = groupFinder.findConnectedGroups(frame);
                if (groups.isEmpty()) {
                    frameIndex++;
                    continue;
                }
                if (salamander == null) {
                    salamander = new Salamander(GroupManager.findLargestGroup(groups));
                } 
                else {
                    Group closest = GroupManager.findClosestGroup(salamander.getSalamander(), groups);
                    salamander.update(closest);
                }
                exporter.writeFrame(frameIndex, salamander.getSalamander());
                frameIndex++;
            }
            exporter.close();
            grabber.close();
        }     
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
