package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class pipeLine  extends OpenCvPipeline {
    
    Mat main = new Mat();
    Mat output = new Mat();
    Mat leftCrop;
    Mat middleCrop;
    Mat rightCrop;
//    Scalar rectColor = new Scalar(255,1,1);

    static Scalar leftMean;
    static Scalar middleMean;
    static Scalar rightMean;

    @Override
    public Mat processFrame(Mat input) throws CvException {
        Imgproc.cvtColor(input, main, Imgproc.COLOR_RGB2BGR);

        Rect leftRect = new Rect(0,0,(640 / 3),360);
        Rect middleRect = new Rect((640 / 3), 0, (640 / 3), 360);
        Rect rightRect = new Rect((640 / 3) * 2,0,(640 / 3),360);

        input = input.submat(new Rect(0,  0, 640 , 360));

//        Imgproc.rectangle(output, leftRect, rectColor, 2);
//        Imgproc.rectangle(output, rightRect, rectColor, 2);

        leftCrop = input.submat(leftRect);
        middleCrop = input.submat(middleRect);
        rightCrop = input.submat(rightRect);

        Core.extractChannel(leftCrop,leftCrop,2);
        Core.extractChannel(middleCrop,middleCrop,2);
        Core.extractChannel(rightCrop,rightCrop,2);

        leftMean = Core.mean(leftCrop);
        middleMean = Core.mean(middleCrop);
        rightMean = Core.mean(rightCrop);

        return input;
    }
}
