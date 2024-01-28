package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class RedPipeline extends OpenCvPipeline {
//    Mat main = new Mat();
//    Mat output = new Mat();
    Mat leftCrop;
    Mat middleCrop;
    Mat rightCrop;
    Mat redChannel = new Mat();

    PropPosition propPos;

    @Override
    public Mat processFrame(Mat input) throws CvException {
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2HSV);

        Rect leftRect = new Rect(0,0,(640 / 3),360);
        Rect middleRect = new Rect((640 / 3), 0, (640 / 3), 360);
        Rect rightRect = new Rect((640 / 3) * 2,0,(640 / 3),360);

        input = input.submat(new Rect(0,  0, 640 , 360));

        final double red_hue_low   =   0, red_sat_low   =  90, red_val_low   =  80;
        final double red_hue_high  =   4, red_sat_high  = 255, red_val_high  = 255;

        final Scalar lowRGBred = new Scalar(red_hue_low, red_sat_low, red_val_low);
        final Scalar highRGBred = new Scalar(red_hue_high, red_sat_high, red_val_high);

        Core.inRange(input, lowRGBred, highRGBred, redChannel); //90 80 175 50 isolated prop

        leftCrop = redChannel.submat(leftRect);
        middleCrop = redChannel.submat(middleRect);
        rightCrop = redChannel.submat(rightRect);

        double leftMean   = Core.sumElems(redChannel).val[0] / (redChannel.rows() * redChannel.cols());
        double middleMean = Core.sumElems(redChannel).val[0] / (redChannel.rows() * redChannel.cols());
        double rightMean  = Core.sumElems(redChannel).val[0] / (redChannel.rows() * redChannel.cols());

        double maxHue = Math.max(Math.max(leftMean, middleMean), rightMean);
        if(maxHue == leftMean)
            propPos = PropPosition.Left;
        else if(maxHue == rightMean)
            propPos = PropPosition.Right;
        else
            propPos = PropPosition.Middle;


        Imgproc.rectangle(input, new Point(0,0), new Point(640 / 3, 360), new Scalar(255,0,0), 5);
        Imgproc.rectangle(input, new Point(640 / 3,0), new Point((640 / 3) * 2, 360), new Scalar(255,0,0), 5);
        Imgproc.rectangle(input, new Point((640 / 3) * 2,0), new Point(640, 360), new Scalar(255,0,0), 5);

        return input;
    }

    public PropPosition getPropPos(){
        return propPos;
    }
}