package org.firstinspires.ftc.teamcode;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class recordingPipeline extends OpenCvPipeline {

    Mat main = new Mat();
    Mat output = new Mat();

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, main, Imgproc.COLOR_RGB2BGR);

        input.copyTo(output);
        return output;
    }
}
