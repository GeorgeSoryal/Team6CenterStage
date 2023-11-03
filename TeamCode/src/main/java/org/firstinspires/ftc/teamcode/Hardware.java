package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Hardware {
    final private double TICKS_PER_MOTOR_REV = ((((1+((double)46/17))) * (1+((double)46/11))) * 28);
    final private double DRIVE_GEAR_REDUCTION = 1.0;
    final private double WHEEL_DIAMETER_INCHES = 3.78;
    final private double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION)/ (WHEEL_DIAMETER_INCHES * 3.1415);
    final private double DRIVE_SPEED = 0.6;
//    final private double TURN_SPEED = 0.5;
    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;
    public DcMotor axleMotor = null;
    private final OpMode opMode;
    public Hardware(OpMode opMode1){
        opMode = opMode1;
    }
    public void init(HardwareMap hardwareMap) {
        try {
            frontLeft = hardwareMap.dcMotor.get("frontLeftMotor");
            frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            opMode.telemetry.addData("FrontLeftMotor: ", "Initialized");
        } catch (Exception e) {
            opMode.telemetry.addData("FrontLeftMotor: ", "Error");
        } finally{
            opMode.telemetry.update();
        }
        try {
            frontRight = hardwareMap.dcMotor.get("frontRightMotor");
            frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            opMode.telemetry.addData("FrontRightMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("FrontRightMotor: ", "Error");
        } finally{
            opMode.telemetry.update();
        }
        try {
            backRight = hardwareMap.dcMotor.get("backRightMotor");
            backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            opMode.telemetry.addData("BackRightMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("BackRightMotor: ", "Error");
        } finally {
            opMode.telemetry.update();
        }
        try {
            backLeft = hardwareMap.dcMotor.get("backLeftMotor");
            backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            opMode.telemetry.addData("BackLeftMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("BackLeftMotor: ", "Error");
        } finally {
            opMode.telemetry.update();
        }
        try {
            axleMotor = hardwareMap.dcMotor.get("axleMotor");
            axleMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            opMode.telemetry.addData("axleMotor: ", "Success");
        } catch(Exception e) {
            opMode.telemetry.addData("axleMotor: ", "Error");
        } finally {
            opMode.telemetry.update();
        }
        // Have to test this when the drive train is created
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        opMode.telemetry.addData("Hardware Init: ", "Success.");
        opMode.telemetry.update();
    }

    public void drive(double power, double inches){
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        int targetPosition = (int)(inches*TICKS_PER_INCH);
        opMode.telemetry.addData("targetPosition linear drive: ", targetPosition);
        frontLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(targetPosition);
        backLeft.setTargetPosition(targetPosition);
        backRight.setTargetPosition(targetPosition);

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
        while(frontLeft.getCurrentPosition() <= frontRight.getCurrentPosition() &&
                frontRight.getCurrentPosition() <= backRight.getCurrentPosition() &&
                backLeft.getCurrentPosition() <= frontRight.getCurrentPosition() &&
                frontLeft.getCurrentPosition() <= frontRight.getCurrentPosition()){
            opMode.telemetry.addData("Moving in drive: ", frontLeft.getCurrentPosition());
            opMode.telemetry.update();
        }

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        opMode.telemetry.addData("Linear Drive complete.", "");
        opMode.telemetry.update();
    }
    public void drive(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower, double inches){
        int targetPosition = (int)(inches*TICKS_PER_INCH);
        opMode.telemetry.addData("targetPosition for linear drive: ", targetPosition);
        opMode.telemetry.update();

        frontLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(targetPosition);
        backLeft.setTargetPosition(targetPosition);
        backRight.setTargetPosition(targetPosition);

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
        while(isNotAtTargetPosition());

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        opMode.telemetry.addData("Linear Drive complete.", "");
        opMode.telemetry.update();
    }
    // for distance: right is positive, left is negative
    public void strafe(double distance, double power) {
        frontLeft.setTargetPosition((int) (distance * TICKS_PER_INCH));
        frontRight.setTargetPosition((int) (-distance * TICKS_PER_INCH));
        backLeft.setTargetPosition((int) (distance * TICKS_PER_INCH));
        backRight.setTargetPosition((int) (-distance * TICKS_PER_INCH));

        frontLeft.setPower(power);
        frontRight.setPower(-power);
        backLeft.setPower(power);
        backRight.setPower(-power);

        while (isNotAtTargetPosition());

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    //positive power -> turn right, negative power -> turn left
    public void turn(double distance, double power){
        if(power > 0){
            frontLeft.setPower(power);
            backLeft.setPower(power);
            frontLeft.setTargetPosition((int) (distance * TICKS_PER_INCH));
            backLeft.setTargetPosition((int) (distance * TICKS_PER_INCH));
        } else {
            frontRight.setPower(power);
            backRight.setPower(power);
            frontRight.setTargetPosition((int) (distance * TICKS_PER_INCH));
            backRight.setTargetPosition((int) (distance * TICKS_PER_INCH));
        }

        while (isNotAtTargetPosition());


        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
    }

    public void setMotorsToZero(){
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    private boolean isNotAtTargetPosition(){
        return frontLeft.getCurrentPosition() <= frontLeft.getTargetPosition() &&
                backLeft.getCurrentPosition() <= backLeft.getTargetPosition() &&
                frontRight.getCurrentPosition() <= frontRight.getTargetPosition() &&
                backRight.getCurrentPosition() <= backRight.getTargetPosition();
    }
}