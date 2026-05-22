package io.github.Zeta1313.centroidfinder;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

public class Grabber {

    private FrameGrab grab;

    public Grabber(String path) throws Exception {
        grab = FrameGrab.createFrameGrab(
                NIOUtils.readableChannel(new File(path))
        );
    }

    public BufferedImage nextFrame() throws Exception {
        Picture picture = grab.getNativeFrame();
        if (picture == null) {
            return null;
        }
        return AWTUtil.toBufferedImage(picture);
    }
}
