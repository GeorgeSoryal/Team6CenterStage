package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class PipeLine extends OpenCvPipeline {
    Mat main = new Mat();
    Mat   leftBlueCrop;
    Mat middleBlueCrop;
    Mat  rightBlueCrop;

    Mat   leftRedCrop;
    Mat middleRedCrop;
    Mat  rightRedCrop;

    Mat blueChannel = new Mat();
    Mat redChannel = new Mat();
    private PropPosition propPos;
    private String propColor = "calculating...";

    @Override
    public Mat processFrame(Mat input) throws CvException {
        Imgproc.cvtColor(input, main, Imgproc.COLOR_RGB2HSV);

        Rect leftRect = new Rect(0,0,(640 / 3),360);
        Rect middleRect = new Rect((640 / 3), 0, (640 / 3), 360);
        Rect rightRect = new Rect((640 / 3) * 2,0,(640 / 3),360);

        input = input.submat(new Rect(0,  0, 640 , 360));

        leftBlueCrop = input.submat(leftRect);
        middleBlueCrop = input.submat(middleRect);
        rightBlueCrop = input.submat(rightRect);

        leftRedCrop = input.submat(leftRect);
        middleRedCrop = input.submat(middleRect);
        rightRedCrop = input.submat(rightRect);

        Core.extractChannel(leftRedCrop,leftRedCrop,2);
        Core.extractChannel(middleRedCrop,middleRedCrop,2);
        Core.extractChannel(rightRedCrop,rightRedCrop,2);

        Core.extractChannel(leftBlueCrop,leftBlueCrop,2);
        Core.extractChannel(middleBlueCrop,middleBlueCrop,2);
        Core.extractChannel(rightBlueCrop,rightBlueCrop,2);

        final double blue_hue_low  = 104, blueSatLow  = 175, bLue_val_Low  =  50;
        final double blue_hue_high = 150, blue_sat_high = 255, blueValHigh = 180;
        final double red_hue_low   =   0, red_sat_low   =  90, red_val_low   =  80;
        final double red_hue_high  =   4, red_sat_high  = 255, red_val_high  = 255;

        final Scalar lowRGBred = new Scalar(red_hue_low, red_sat_low, red_val_low);
        final Scalar highRGBred = new Scalar(red_hue_high, red_sat_high, red_val_high);
        final Scalar lowRGBBlue = new Scalar(blue_hue_low, blueSatLow, bLue_val_Low);
        final Scalar highRGBBlue = new Scalar(blue_hue_high, blue_sat_high, blueValHigh);

        Core.inRange(input, lowRGBBlue, highRGBBlue, blueChannel);
        Core.inRange(input, lowRGBred, highRGBred, redChannel); //90 80 175 50 isolated prop

        leftRedCrop = redChannel.submat(leftRect);
        middleRedCrop = redChannel.submat(middleRect);
        rightRedCrop = redChannel.submat(rightRect);

        leftBlueCrop = blueChannel.submat(leftRect);
        middleBlueCrop = blueChannel.submat(middleRect);
        rightBlueCrop = blueChannel.submat(rightRect);

        double leftBlueMean   = Core.sumElems(leftBlueCrop).val[0] / (leftBlueCrop.rows() * leftBlueCrop.cols());
        double middleBlueMean = Core.sumElems(middleBlueCrop).val[0] / (middleBlueCrop.rows() * middleBlueCrop.cols());
        double rightBlueMean  = Core.sumElems(rightBlueCrop).val[0] / (rightBlueCrop.rows() * rightBlueCrop.cols());

        double leftRedMean   = Core.sumElems(leftRedCrop).val[0] / (leftRedCrop.rows() * leftRedCrop.cols());
        double middleRedMean = Core.sumElems(middleRedCrop).val[0] / (middleRedCrop.rows() * middleRedCrop.cols());
        double rightRedMean  = Core.sumElems(rightRedCrop).val[0] / (rightRedCrop.rows() * rightRedCrop.cols());

        double maxBlueHue = Math.max(Math.max(leftBlueMean, middleBlueMean), rightBlueMean);
        double maxRedHue = Math.max(Math.max(leftRedMean, middleRedMean), rightRedMean);

        if(maxBlueHue > maxRedHue){
            setPropPos(maxBlueHue, leftBlueMean, rightBlueMean);
            propColor = "blue";
        } else {
            setPropPos(maxRedHue, leftRedMean, rightRedMean);
            propColor = "red";
        }


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

    public String getPropColor(){
        return propColor;
    }
}