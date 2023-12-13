package org.firstinspires.ftc.teamcode;

import android.util.Pair;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


@Autonomous(name="Autonomous")
public class Auto extends LinearOpMode {
    static final double TICKS_PER_MOTOR_REV = ((((1+((double)46/17))) * (1+((double)46/11))) * 28);
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    //final private double DRIVE_SPEED = 0.6;
    Hardware hw = new Hardware(this);


    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);
        resetEncoders();

        Pair<String, String> autoMode = getAutoMode();

        waitForStart();

        String parking = autoMode.second;
        switch (autoMode.first){
            case "blueLeft":
                autoLB(parking);
                break;

            case "blueRight":
                autoRB(parking);
                break;

            case "redLeft":
                autoLR(parking);
                break;

            case "redRight":
                autoRR(parking);
                break;

            default:
                telemetry.addData("ERROR: ", "MODE NOT FOUND");
                telemetry.update();
                defaultAuto();
                break;
        }

        hw.setMotorsToZero();
    }

    public void drive(double power, double inches){
        resetEncoders();
        int targetPosition = (int)(inches * TICKS_PER_INCH);

        hw.setAllTargets(targetPosition);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(power);
        while(hw.isNotAtTargetPosition() && opModeIsActive());

        hw.setMotorsToZero();

    }
    public void drive(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower, double inches){
        resetEncoders();
        int targetPosition = (int)(inches * TICKS_PER_INCH);

        hw.setAllTargets(targetPosition);

        hw.frontLeft.setPower(frontLeftPower);
        hw.frontRight.setPower(frontRightPower);
        hw.backLeft.setPower(backLeftPower);
        hw.backRight.setPower(backRightPower);
        while(hw.isNotAtTargetPosition() && opModeIsActive());

        hw.setMotorsToZero();


    }
    // for distance: right is negative, left is positive
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

        hw.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    //TODO: simplify while loops into a method
    //TODO: Fix all autoXx methods
    /**
     * @return Pair: first = mode, second = parking
     * **/
    public Pair<String, String> getAutoMode(){
        String mode = "";
        int count = 0;

        telemetry.addData("enter auto mode: ", "\n - red: B\n - blue: X");
        telemetry.update();
        while(count == 0){
            if (gamepad1.x){
                mode += "blue";
                count++;
            } else if(gamepad1.b){
                mode += "red";
                count++;
            }

            sleep(25); //memory saver
        }

        telemetry.addData("L/R: ", "\n - Left: [dpad left]\n - Right: [dpad right]");
        telemetry.update();
        while(count == 1){
            if(gamepad1.dpad_left){
                mode += "Left";
                count++;
            } else if(gamepad1.dpad_right){
                mode += "Right";
                count++;
            }
        }

        telemetry.addData("parking: ", "\n - Left: [dpad left]\n - Right: [dpad right]");
        telemetry.update();
        String parking = "";
        while(count == 2) {
            if (gamepad1.dpad_left) {
                parking = "left";
                count++;
            } else if (gamepad1.dpad_right) {
                parking = "right";
                count++;
            }
        }

        return new Pair<String, String>(mode, parking);
    }

    public void defaultAuto(){
        drive(0.8, 27.5);
        drive(-0.8, -25.5);
    }

    public void autoLB(String parking){
        drive(0.8, 27.5);
        drive(-0.8, -25.5);
        strafe(-46, -0.8);
    }

    public void autoRB(String parking){
        drive(0.8, 27.5);
        drive(-0.8, -25);
        turn(90, 0.8);
        drive(0.8, 50);
    }

    public void autoLR(String parking){
        drive(0.8, 27.5);
        drive(-0.8, -25);
        turn(-90, -0.8);
        drive(0.8, 50);
    }

    public void autoRR(String parking){
        drive(0.8, 27.5);
        drive(-0.8, -25.5);
        strafe(46, 0.8);
    }

}
