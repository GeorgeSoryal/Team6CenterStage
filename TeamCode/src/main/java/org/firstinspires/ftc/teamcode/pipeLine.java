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
    Mat rightCrop;
    Scalar rectColor = new Scalar(255,1,1);

    @Override
    public Mat processFrame(Mat input) throws CvException {
        Imgproc.cvtColor(input, main, Imgproc.COLOR_RGB2BGR);
//
//        Rect leftRect = new Rect(0,0,320,480);
//        Rect rightRect = new Rect(320,0,320,480);
//
//        input = input.submat(new Rect(0,  0, 640 , 360));
//        output = output.submat(new Rect(0, 0, 640, 360));
//        input.copyTo(output);
//
//        Imgproc.rectangle(output, leftRect, rectColor, 2);
//        Imgproc.rectangle(output, rightRect, rectColor, 2);
//
//        leftCrop = main.submat(leftRect);
//        rightCrop = main.submat(rightRect);
//
//        Core.extractChannel(leftCrop,leftCrop,2);
//        Core.extractChannel(rightCrop,rightCrop,2);
        input.copyTo(output);
//
        return output;
    }
}
