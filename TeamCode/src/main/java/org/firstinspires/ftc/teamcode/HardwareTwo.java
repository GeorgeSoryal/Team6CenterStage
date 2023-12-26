package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

public class HardwareTwo {

    //wheels
    public DcMotor rf;
    public DcMotor lf;
    public DcMotor rr;
    public DcMotor lr;
    public DcMotor clawArm;
    public DcMotor spool;
    public Servo clawRight;
    public Servo clawLeft;
    public Servo wrist;
    public Servo dropper;

    //other things

    public BNO055IMU gyro;
    //public RevColorSensorV3 color;

    public static double maxSpeed = 1;

    private static HardwareTwo myInstance = null;

    public static HardwareTwo getInstance() {
        if (myInstance == null) {
            myInstance = new HardwareTwo();
        }
        return myInstance;
    }

    public void init(HardwareMap hwMap) {

        try{
            rf = hwMap.get(DcMotor.class, "fr");
            rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rf.setPower(0);
        } catch (Exception p_exception) {
            rf = null;
        }

        try{
            lf = hwMap.get(DcMotor.class, "fl");
            lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lf.setDirection(DcMotorSimple.Direction.REVERSE);
            lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lf.setPower(0);
        } catch (Exception p_exception) {
            lf = null;
        }

        try{
            rr = hwMap.get(DcMotor.class, "br");
            rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rr.setPower(0);
        } catch (Exception p_exception) {
            rr = null;
        }

        try{
            lr = hwMap.get(DcMotor.class, "bl");
            lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lr.setDirection(DcMotorSimple.Direction.REVERSE);
            lr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lr.setPower(0);
        } catch (Exception p_exception) {
            lr = null;
        }

        try{
            clawArm = hwMap.get(DcMotor.class, "armClaw");
            clawArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            clawArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            clawArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            clawArm.setPower(0);
        } catch (Exception p_exception) {
            clawArm = null;
        }

        try{
            spool = hwMap.get(DcMotor.class, "em3");
            spool.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            spool.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            spool.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            spool.setPower(0);
        } catch (Exception p_exception) {
            spool = null;
        }

        try{
            clawLeft = hwMap.get(Servo.class, "claw1");
        } catch (Exception p_exception) {
            clawLeft = null;
        }

        try{
            clawRight = hwMap.get(Servo.class, "claw2");
        } catch (Exception p_exception) {
            clawRight = null;
        }

        try{
            wrist = hwMap.get(Servo.class, "cs0");
        } catch (Exception p_exception) {
            wrist = null;
        }

        try{
            dropper= hwMap.get(Servo.class, "es0");
        } catch (Exception p_exception) {
            dropper = null;
        }


        /*try {
            servo1 = hwMap.get(Servo.class, "Servo1");
        } catch (Exception p_exception) {
            servo1 = null;
        }

        try {
            color = hwMap.get(RevColorSensorV3.class, "Color");
        } catch (Exception p_exception) {
            color = null;
        }*/


        try {
            gyro = hwMap.get(BNO055IMU.class, "gyro");
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.loggingEnabled = true;
            parameters.loggingTag = "gyro";
            parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
            gyro.initialize(parameters);
        } catch (Exception p_exception) {
            gyro = null;
        }




    }

    public void setPower(double  rightFront, double rightBack, double leftFront, double leftBack) {
        if (rf != null) {
            rf.setPower(Range.clip(rightFront, -maxSpeed, maxSpeed));
        }
        if (rr != null) {
            rr.setPower(Range.clip(rightBack, -maxSpeed, maxSpeed));
        }
        if (lf != null) {
            lf.setPower(Range.clip(leftFront, -maxSpeed, maxSpeed));
        }
        if (lr != null) {
            lr.setPower(Range.clip(leftBack, -maxSpeed, maxSpeed));
        }

    }


}
