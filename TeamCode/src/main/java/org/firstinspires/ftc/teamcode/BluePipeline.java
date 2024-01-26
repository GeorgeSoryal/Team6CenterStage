package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class BluePipeline extends OpenCvPipeline {
    Mat main = new Mat();
//    Mat output = new Mat();
    Mat leftCrop;
    Mat middleCrop;
    Mat rightCrop;
    Mat blueChannel = new Mat();
    private PropPosition propPos;

    @Override
    public Mat processFrame(Mat input) throws CvException {
        Imgproc.cvtColor(input, main, Imgproc.COLOR_RGB2HSV);

        Rect leftRect = new Rect(0,0,(640 / 3),360);
        Rect middleRect = new Rect((640 / 3), 0, (640 / 3), 360);
        Rect rightRect = new Rect((640 / 3) * 2,0,(640 / 3),360);

        input = input.submat(new Rect(0,  0, 640 , 360));

        leftCrop = input.submat(leftRect);
        middleCrop = input.submat(middleRect);
        rightCrop = input.submat(rightRect);

        Core.extractChannel(leftCrop,leftCrop,2);
        Core.extractChannel(middleCrop,middleCrop,2);
        Core.extractChannel(rightCrop,rightCrop,2);

        final double blue_hue_low  = 104, blueSatLow  = 175, bLue_val_Low  =  50;
        final double blue_hue_high = 150, blue_sat_high = 255, blueValHigh = 180;

        final Scalar lowRGBBlue = new Scalar(blue_hue_low, blueSatLow, bLue_val_Low);
        final Scalar highRGBBlue = new Scalar(blue_hue_high, blue_sat_high, blueValHigh);

        Core.inRange(leftCrop, lowRGBBlue, highRGBBlue, blueChannel);

        leftCrop = blueChannel.submat(leftRect);
        middleCrop = blueChannel.submat(middleRect);
        rightCrop = blueChannel.submat(rightRect);

        double leftMean   = Core.sumElems(blueChannel).val[0] / (blueChannel.rows() * blueChannel.cols());
        double middleMean = Core.sumElems(blueChannel).val[0] / (blueChannel.rows() * blueChannel.cols());
        double rightMean  = Core.sumElems(blueChannel).val[0] / (blueChannel.rows() * blueChannel.cols());

        double maxHue = Math.max(Math.max(leftMean, middleMean), rightMean);
        if(maxHue == leftMean)
            propPos = PropPosition.Left;
        else if(maxHue == rightMean)
            propPos = PropPosition.Right;
        else
            propPos = PropPosition.Middle;

        return input;
    }

    public PropPosition getPropPos(){
        return propPos;
    }
}
