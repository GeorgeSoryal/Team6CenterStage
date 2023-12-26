package org.firstinspires.ftc.teamcode;

import android.util.Pair;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


@Autonomous(name="Autonomous")
public class Auto extends LinearOpMode {
    static final double TICKS_PER_MOTOR_REV = ((((1 + ((double) 46 / 17))) * (1 + ((double) 46 / 11))) * 28);
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    //final private double DRIVE_SPEED = 0.6;
    final private double DEFAULT_POWER = 0.8;
    // always absolute values since its distances and its less confusing for me even though DISTANCE_BACK_TO_WALL will
    // never be used as a positive
    final private double DISTANCE_TO_SPIKE_MARK = 27.5;
    Hardware hw = new Hardware(this);

    // left or right in the parking area from the robots perspective 
    private enum ParkingDirection {
        left,
        right;
    }

    // where the robot itself is
    private enum ParkingMode {
        BlueRight,
        BlueLeft,
        RedRight,
        RedLeft;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);
        resetEncoders();

        Pair<ParkingMode, ParkingDirection> autoMode = getAutoMode();

        waitForStart();

        ParkingDirection parking = autoMode.second;
        switch (autoMode.first) {
            case BlueLeft:
                autoLB(parking);
                break;

            case BlueRight:
                autoRB(parking);
                break;

            case RedLeft:
                autoLR(parking);
                break;

            case RedRight:
                autoRR(parking);
                break;

            default:
                telemetry.addData("ERROR: ", "MODE NOT FOUND");
                telemetry.update();
                defaultAutoBackToWall();
                break;
        }

        hw.setMotorsToZero();
    }

    public void drive(double inches, double power) {
        resetEncoders();
        int targetPosition = (int) (inches * TICKS_PER_INCH);

        hw.setAllTargets(targetPosition);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(power);
        while (hw.isNotAtTargetPosition() && opModeIsActive()) ;

        hw.setMotorsToZero();

    }

    public void drive(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower, double inches) {
        resetEncoders();
        int targetPosition = (int) (inches * TICKS_PER_INCH);

        hw.setAllTargets(targetPosition);

        hw.frontLeft.setPower(frontLeftPower);
        hw.frontRight.setPower(frontRightPower);
        hw.backLeft.setPower(backLeftPower);
        hw.backRight.setPower(backRightPower);
        while (hw.isNotAtTargetPosition() && opModeIsActive()) ;

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

        while (hw.isNotAtTargetPosition() && opModeIsActive()) ;

        hw.setMotorsToZero();

    }

    //positive power -> turn left, negative power -> turn right
    public void turn(double angle, double power) {
        resetEncoders();
        angle = (angle / 360) * (8 * TICKS_PER_MOTOR_REV); //8 motor revs = 360 degree turn
        int targetPosition = (int) angle;

        hw.frontLeft.setTargetPosition(targetPosition);
        hw.frontRight.setTargetPosition(-targetPosition);
        hw.backLeft.setTargetPosition(targetPosition);
        hw.backRight.setTargetPosition(-targetPosition);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(-power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(-power);


        while (hw.isNotAtTargetPosition() && opModeIsActive()) ;


        hw.setMotorsToZero();

    }

    public void resetEncoders() {
        hw.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hw.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /**
     * @return Pair: first = mode, second = parking
     **/
    public Pair<ParkingMode, ParkingDirection> getAutoMode() {
        String mode = "";
        int count = 0;

        telemetry.addData("enter auto mode: ", "\n - blue [dpad left]\n - red: [dpad right]");
        telemetry.update();
        mode += getInput("blue", "right");

        telemetry.addData("L/R: ", "\n - Left: [dpad left]\n - Right: [dpad right]");
        telemetry.update();
        ParkingMode parkingMode = mode.equals("blue") ? getInput(ParkingMode.BlueLeft, ParkingMode.BlueRight)
                : getInput(ParkingMode.RedLeft, ParkingMode.RedRight);

        telemetry.addData("parking: ", "\n - Left: [dpad left]\n - Right: [dpad right]");
        telemetry.update();
        ParkingDirection parking = getInput(ParkingDirection.left, ParkingDirection.right);


        return new Pair<ParkingMode, ParkingDirection>(parkingMode, parking);
    }

    // any type T
    public <T> T getInput(T mode1, T mode2) {
        while (true) {
            if (gamepad1.dpad_left) {
                return mode1;
            } else if (gamepad1.dpad_right) {
                return mode2;
            }
            sleep(30);
        }
    }

    public void defaultAutoBackToWall() {
        drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
        double DISTANCE_BACK_TO_WALL = 25.5;
        drive(-DISTANCE_BACK_TO_WALL, -DEFAULT_POWER);
    }

    public void autoLB(ParkingDirection parking) {
        switch (parking) {
            case right:
                // forward then strafe
                drive(45, DEFAULT_POWER);
            case left:
                // back to wall then strafe
                defaultAutoBackToWall();
        }
        strafe(-46, -DEFAULT_POWER);
    }

    public void autoRB(ParkingDirection parking) {
        switch (parking) {
            case right:
                // TODO
                break;
            case left:
                drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
                drive(-25, -DEFAULT_POWER);
                turn(90, DEFAULT_POWER);
                drive(50, DEFAULT_POWER);
                break;

        }
    }

    public void autoLR(ParkingDirection parking) {
        switch (parking) {
            case right:
                defaultAutoBackToWall();
                turn(-90, -DEFAULT_POWER);
                drive(50, DEFAULT_POWER);
                break;
            case left:
                //TODO
                break;
        }
    }

    public void autoRR(ParkingDirection parking) {
        switch (parking) {
            case right:
                defaultAutoBackToWall();
                strafe(46, DEFAULT_POWER);
                break;
            case left:
                //TODO
                break;

        }
    }
}