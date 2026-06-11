Classes needed:
1. Class to split up videos into an array or other structure of frames
2. Class to run a frame through VideoSummaryApp.java
3. Class to compile exports into a CSV file
4. Main class that runs everything

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
try (var channel = NIOUtils.readableChannel(new File(inputPath));
    PrintWriter writer = new PrintWriter(outputCsvPath)) {

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