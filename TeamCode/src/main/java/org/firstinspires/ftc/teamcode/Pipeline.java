package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.nio.channels.Pipe;

public class Pipeline extends OpenCvPipeline {
    Mat main = new Mat();
    Mat   leftCrop;
    Mat middleCrop;
    Mat  rightCrop;
    private PropPosition propPos;
    private boolean isOnBlueSide;

    public Pipeline(boolean isOnBlueSide){
        this.isOnBlueSide = isOnBlueSide;
    }

    public Pipeline(){
        this.isOnBlueSide = true;
    }
    @Override
    public Mat processFrame(Mat input) throws CvException {
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2HSV);

        final double blue_hue_low =   ((double)218 / 360) * 179, sat_low   =  70, val_low   = 50;
        final double blue_hue_high  =   ((double) 232 / 360) * 179, sat_high  = 255, val_high  = 255;
        final double red_hue_low = ((double) 340 /360) *179, red_hue_high = ((double) 360 / 360 ) * 179;
//
        final Scalar blueLowHSV = new Scalar(blue_hue_low, sat_low, val_low);
        final Scalar blueHighHSV = new Scalar(blue_hue_high, sat_high, val_high);
        final Scalar redLowHSV = new Scalar(red_hue_low, sat_low, val_low);
        final Scalar redHighHSV = new Scalar(red_hue_high, sat_high, val_high);
//
        if(isOnBlueSide)
            Core.inRange(input, blueLowHSV, blueHighHSV, input);
        else
            Core.inRange(input, redLowHSV, redHighHSV, input);

        Rect leftRect = new Rect(0,100,(640 / 3),260);
        Rect middleRect = new Rect((640 / 3), 100, (640 / 3), 260);
        Rect rightRect = new Rect((640 / 3) * 2,100,(640 / 3),260);

        leftCrop = input.submat(leftRect);
        middleCrop = input.submat(middleRect);
        rightCrop = input.submat(rightRect);

        double leftMean   = Core.sumElems(leftCrop).val[0] / (leftCrop.rows() * leftCrop.cols());
        double middleMean = Core.sumElems(middleCrop).val[0] / (middleCrop.rows() * middleCrop.cols());
        double rightMean  = Core.sumElems(rightCrop).val[0] / (rightCrop.rows() * rightCrop.cols());

        double maxHue = Math.max(Math.max(leftMean, middleMean), rightMean);

        setPropPos(maxHue, leftMean, rightMean);

        Imgproc.rectangle(input, leftRect, new Scalar(255, 255, 255), 10);
        Imgproc.rectangle(input, rightRect, new Scalar(255, 255, 255), 10);
        return input;
    }

    //set prop position based off the color of the prop
    private void setPropPos(double maxHue, double leftMean, double rightMean){
        if(maxHue == leftMean)
            propPos = PropPosition.Left;
        else if(maxHue == rightMean)
            propPos = PropPosition.Right;
        else
            propPos = PropPosition.Middle;
    }

    public PropPosition getPropPos(){
        return propPos;
    }

}