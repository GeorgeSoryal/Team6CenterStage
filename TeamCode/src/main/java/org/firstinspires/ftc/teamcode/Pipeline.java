package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class Pipeline extends OpenCvPipeline {
    Mat main = new Mat();
    Mat leftCropBlue = new Mat();
    Mat middleCropBlue = new Mat();
    Mat rightCropBlue = new Mat();

    Mat leftCropRed = new Mat();
    Mat middleCropRed = new Mat();
    Mat  rightCropRed = new Mat();
    private PropPosition propPos;
    double leftMeanRed;
    double middleMeanRed;
    double rightMeanRed;
    double leftMeanBlue;
    double middleMeanBlue;
    double rightMeanBlue;

    @Override
    public Mat processFrame(Mat input) throws CvException {
//        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2HSV);

        Rect leftRect = new Rect(0,100,(640 / 3),260);
        Rect middleRect = new Rect((640 / 3), 100, (640 / 3), 260);
        Rect rightRect = new Rect((640 / 3) * 2,100,(640 / 3),260);

        input = input.submat(new Rect(0,  0, 640 , 360));

        leftCropBlue = input.submat(leftRect);
        middleCropBlue = input.submat(middleRect);
        rightCropBlue = input.submat(rightRect);

        leftCropRed = input.submat(leftRect);
        middleCropRed = input.submat(middleRect);
        rightCropRed = input.submat(rightRect);


        Core.extractChannel(leftCropBlue, leftCropRed, 0);
        Core.extractChannel(middleCropBlue, middleCropRed, 0);
        Core.extractChannel(rightCropBlue, rightCropRed, 0);

        Core.extractChannel(leftCropBlue, leftCropBlue,2);
        Core.extractChannel(middleCropBlue, middleCropBlue,2);
        Core.extractChannel(rightCropBlue, rightCropBlue,2);

//        final double red_high =   /*((double)80 / 360) * 255*/ .53 * 255, blue_high   =  0.07 * 255, val_low   = .58 * 255;
//        final double red_low  =   /*((double) 208 / 360) * 255*/ .28 * 255, sat_high  = 0.75 * 255, val_high  = .89 * 255;
//
//        final Scalar lowRGB = new Scalar(red_low, sat_low, val_low);
//        final Scalar highRGB = new Scalar(red_high, sat_high, val_high);
//
//        Core.inRange(input, lowRGB, highRGB, input);

        leftMeanRed   = Core.sumElems(leftCropRed).val[0] / (leftCropRed.rows() * leftCropRed.cols());
        middleMeanRed = Core.sumElems(middleCropRed).val[0] / (middleCropRed.rows() * middleCropRed.cols());
        rightMeanRed  = Core.sumElems(rightCropRed).val[0] / (rightCropRed.rows() * rightCropRed.cols());

        leftMeanBlue   = Core.sumElems(leftCropBlue).val[0] / (leftCropBlue.rows() * leftCropBlue.cols());
        middleMeanBlue = Core.sumElems(middleCropBlue).val[0] / (middleCropBlue.rows() * middleCropBlue.cols());
        rightMeanBlue  = Core.sumElems(rightCropBlue).val[0] / (rightCropBlue.rows() * rightCropBlue.cols());

        double maxRedMean = Math.max(Math.max(leftMeanRed, middleMeanRed), rightMeanRed);
        double maxBlueMean = Math.max(Math.max(leftMeanBlue, middleMeanBlue), rightMeanBlue);

        if(maxRedMean > maxBlueMean){
            setPropPos(maxRedMean, leftMeanRed, rightMeanRed);
        } else {
            setPropPos(maxBlueMean, leftMeanBlue, rightMeanBlue);
        }

        Imgproc.rectangle(input, leftRect, new Scalar(20, 20, 255), 7);
        Imgproc.rectangle(input, rightRect, new Scalar(20, 20, 255), 7);
        return input;
    }

    //set prop position based off the color of the prop
    private void setPropPos(double maxValue, double leftMean, double rightMean){
        if(maxValue == leftMean)
            propPos = PropPosition.Left;
        else if(maxValue == rightMean)
            propPos = PropPosition.Right;
        else
            propPos = PropPosition.Middle;
    }

    public PropPosition getPropPos(){
        return propPos;
    }

    public String toString(){
        return "\nleft mean red: " + leftMeanRed +
                "\nmiddle mean red: " + middleMeanRed +
                "\nright mean red: " + rightMeanRed +
                "\n\nleft mean blue: " + leftMeanBlue +
                "\nmiddle mean blue: " + middleMeanBlue +
                "\nright mean blue" + rightMeanBlue;
    }
}