package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.vision.VisionProcessor;


@Autonomous(name="TestAutoDoNotUse")
public class AutoTest extends LinearOpMode {
    static final double TICKS_PER_MOTOR_REV = ((((1+((double)46/17))) * (1+((double)46/11))) * 28);
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    //final private double DRIVE_SPEED = 0.6;
    Hardware hw = new Hardware(this);


    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);

        telemetry.addData("Camera: ", hw.visionPortal);
//        telemetry.addData("Camera: (fps)", hw.visionPortal.getFps());
        telemetry.update();

        hw.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("Auto: ", "ready for start");
        telemetry.update();

        waitForStart();

        drive(0.8, 26.5);
        drive(-0.8,-15);
        turn(360, 0.8);

        hw.setMotorsToZero();
    }

    public void drive(double power, double inches){
        resetEncoders();
        int targetPosition = (int)(inches * TICKS_PER_INCH);
        telemetry.addData("targetPosition drive: ", targetPosition);
        hw.setAllTargets(targetPosition);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(power);
        while(hw.isNotAtTargetPosition() && opModeIsActive()){
            telemetry.addData("drive pos: ", hw.frontLeft.getCurrentPosition());
            telemetry.addData("drive target: ", targetPosition);
            telemetry.update();
        }

        hw.setMotorsToZero();

        telemetry.addData("Linear Drive complete.", "");
        telemetry.update();

    }
    public void drive(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower, double inches){
        resetEncoders();
        int targetPosition = (int)(inches*TICKS_PER_INCH);

        hw.setAllTargets(targetPosition);

        hw.frontLeft.setPower(frontLeftPower);
        hw.frontRight.setPower(frontRightPower);
        hw.backLeft.setPower(backLeftPower);
        hw.backRight.setPower(backRightPower);
        while(hw.isNotAtTargetPosition() && opModeIsActive());

        hw.setMotorsToZero();


    }
    // for distance: right is positive, left is negative
    public void strafe(double distance, double power) {
        resetEncoders();
        int targetPos = (int) (distance * TICKS_PER_INCH);
        hw.frontLeft.setTargetPosition(targetPos);
        hw.frontRight.setTargetPosition(-targetPos);
        hw.backLeft.setTargetPosition(targetPos);
        hw.backRight.setTargetPosition(-targetPos);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(-power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(-power);

        while (hw.isNotAtTargetPosition() && opModeIsActive());

        hw.setMotorsToZero();

    }

    //positive power -> turn left, negative power -> turn right
    public void turn(double angle, double power){
        resetEncoders();
        angle = (angle / 360) * (8 * TICKS_PER_MOTOR_REV); //8 motor revs = 360 degree turn
        int targetPosition = (int)angle;
        telemetry.addData("turn target: ", targetPosition);
        telemetry.addData("current pos: ", hw.frontLeft.getCurrentPosition());
        telemetry.update();

        hw.frontLeft.setTargetPosition(targetPosition);
        hw.frontRight.setTargetPosition(-targetPosition);
        hw.backLeft.setTargetPosition(targetPosition);
        hw.backRight.setTargetPosition(-targetPosition);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(-power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(-power);


        while (hw.isNotAtTargetPosition() && opModeIsActive());


        hw.setMotorsToZero();

    }

    public void resetEncoders(){
        hw.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

}
