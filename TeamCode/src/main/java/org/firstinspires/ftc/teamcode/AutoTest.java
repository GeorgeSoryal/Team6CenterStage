package org.firstinspires.ftc.teamcode;


import android.util.Pair;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;


/**
 * Strafing: right = negative, left = positive
 * Driving: backward = negative, forward = positive
 * turn by encoder: turn left = negative, turn right = positive
 * turn by gyro: idk
 */
@Autonomous(name="AutonomousTestDoNotUse")
public class AutoTest extends LinearOpMode {
    static final double TICKS_PER_MOTOR_REV = ((((1 + ((double) 46 / 17))) * (1 + ((double) 46 / 11))) * 28);
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    //final private double DRIVE_SPEED = 0.6;
    final private double DEFAULT_POWER = 0.8;
    // always absolute values since its distances and its less confusing for me even though DISTANCE_BACK_TO_WALL will
    // never be used as a positive
    final private double DISTANCE_TO_SPIKE_MARK = 27.5;

    OpenCvCamera camera = null;
    int cameraMonitorViewId = 0;
    WebcamName webcamName = null;
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
        hw.setMotorsToZero();
        hw.gyro.resetYaw();
        clampDownClaws();

        Pair<Auto.ParkingMode, Auto.ParkingDirection> autoMode;
        autoMode = getAutoMode();   // Can throw InterruptedException

        webcamName = hardwareMap.get(WebcamName.class, "webcam1");
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()); //USED FOR LIVE PREVIEW
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        BluePipeline bluePipeline = null;
        RedPipeline redPipeline = null;
        switch(autoMode.first){
            case RedLeft:
            case RedRight:
                redPipeline = new RedPipeline();
                camera.setPipeline(redPipeline);
                break;
            case BlueLeft:
            case BlueRight:
                bluePipeline = new BluePipeline();
                camera.setPipeline(bluePipeline);
                break;
        }

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                // Usually this is where you'll want to start streaming from the camera (see section 4)
                camera.startStreaming(640,360, OpenCvCameraRotation.UPRIGHT);

            }
            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });

        telemetry.addData("Prop position: ", redPipeline == null ? bluePipeline.getPropPos() : redPipeline.getPropPos());

        // ARM init double checking, specially for auto
//        hw.clawArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        hw.clawArm.setTargetPosition(0);
//        hw.clawArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        resetEncoders();
//
//
//
//        hw.clawArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        hw.clawArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        hw.clawArm.setTargetPosition(hw.CLAW_ARM_BACK_POSITION);
//        hw.clawArm.setPower(-DEFAULT_POWER);
//        while (hw.clawArm.getCurrentPosition() > hw.CLAW_ARM_BACK_POSITION);
//        hw.clawArm.setPower(0);


        waitForStart();

        // Return to down position
//        hw.clawArm.setTargetPosition(0);
//        hw.clawArm.setPower(DEFAULT_POWER);
//        while (hw.clawArm.getCurrentPosition() < 0);
//        hw.clawArm.setPower(DEFAULT_POWER);

        boolean hasRun = false;
        while(opModeIsActive() && !hasRun) {
            hasRun = true;
            //currently: forward then back
//            hw.clawArm.setTargetPosition();
//                if (leftMean.val[0] > 150){
//                    telemetry.addData("CV: ", "left");
//                    telemetry.update();
//                } else if(middleMean.val[0] > 150) {
//                    telemetry.addData("CV: ", "middle");
//                    telemetry.update();
//                } else if(rightMean.val[0] > 150){
//                    telemetry.addData("CV: ", "right");
//                    telemetry.update();
//                }
        }
    }

    public void initCamera(){
        webcamName = hardwareMap.get(WebcamName.class, "webcam1");
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()); //USED FOR LIVE PREVIEW
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        BluePipeline pipeline = new BluePipeline();
        camera.setPipeline(pipeline);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                // Usually this is where you'll want to start streaming from the camera (see section 4)
                camera.startStreaming(640,360, OpenCvCameraRotation.UPRIGHT);

            }
            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });
    }


    /**
     * filler arc below
     * @param inches
     * @param power
     */
    public void drive(double inches, double power) {
        resetEncoders();
        int targetPosition = (int) (inches * TICKS_PER_INCH);
        hw.setAllTargets(targetPosition);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(power);
        while (hw.isNotAtTargetPosition() && opModeIsActive()){
            telemetry.addData("Driving: ", "Telemetry");
            hw.telemetryHardware();
        }

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
        hw.backLeft.setTargetPosition(-targetPos);
        hw.backRight.setTargetPosition(targetPos);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(-power);
        hw.backLeft.setPower(-power);
        hw.backRight.setPower(power);

        while (hw.isNotAtTargetPosition() && opModeIsActive()) ;

        hw.setMotorsToZero();

    }

    //positive power -> turn right, negative power -> turn left
    public void turnByEncoder(double angle, double power) {
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

    /**
     * @param angle from -180 to 180 degrees
     * positive = turn left
     * negative = turn right
     */
    private void turnByGyro(double angle, double power){
        // this will always the same way
        int turnDirection = angle > 0 ? -1 : 1;
        angle = Math.abs(angle);
        double headingError;

        resetEncoders();
        hw.frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hw.backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        while (opModeIsActive() && Math.abs(hw.getGyroAngle()) < angle) {
            telemetry.addData("GyroTurning: ", "Telemetry");

            // Determine the heading current error
            headingError = angle - hw.getGyroAngle();
            headingError = headingError < 0 ? -headingError : headingError;
            telemetry.addData("Heading Error: ", headingError);

            // turnDirection instead of -1
            double rightWheelsPower = -turnDirection * (headingError);
            double leftWheelsPower = turnDirection * (headingError);

            hw.frontLeft.setPower(leftWheelsPower);
            hw.frontRight.setPower(rightWheelsPower);
            hw.backLeft.setPower(leftWheelsPower);
            hw.backRight.setPower(rightWheelsPower);


            hw.telemetryHardware();
        }

        hw.setMotorsToZero();
        resetEncoders(); // Test
    }

    private void clampDownClaws(){
        hw.clawLeft.setPosition(hw.SERVO_1_CLOSED_POSITION);
        hw.clawRight.setPosition(hw.SERVO_2_CLOSED_POSITION);
    }

    private void clampOpenClaws(){
        hw.clawLeft.setPosition(hw.SERVO_1_OPEN_POSITION);
        hw.clawRight.setPosition(hw.SERVO_2_OPEN_POSITION);
    }

    /**
     * Also sets the motors to run to position at the end.
     */
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
    public Pair<ParkingMode, ParkingDirection> getAutoMode() throws Exception {
        hw.clawArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        String mode = "";

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

        telemetry.addData("Set ", "to go");
        telemetry.update();


        return new Pair<ParkingMode, ParkingDirection>(parkingMode, parking);
    }

    // any type T
    public <T> T getInput(T mode1, T mode2) throws Exception {
        while (opModeInInit()) {
            if (gamepad1.dpad_left) {
                sleep(500);
                return mode1;
            } else if (gamepad1.dpad_right) {
                sleep(500);
                return mode2;
            }
            sleep(30);
        }
        throw new Exception("Op Mode not meant to be init or this is not meant to be running.");
    }

    public void defaultAutoBackToWall() {
        drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
        double DISTANCE_BACK_TO_WALL = 25.5;
        drive(-DISTANCE_BACK_TO_WALL, -DEFAULT_POWER);
    }

    public void moveArm(double targetPosition, double power){
        hw.clawArm.setTargetPosition((int)targetPosition);


        if(targetPosition > hw.clawArm.getCurrentPosition()){
            hw.clawArm.setPower(power);
            while(hw.clawArm.getCurrentPosition() < targetPosition && opModeIsActive());
        } else {
            hw.clawArm.setPower(-power);
            while(hw.clawArm.getCurrentPosition() > targetPosition && opModeIsActive());
        }

        hw.clawArm.setPower(0);
    }



    public void autoLB(ParkingDirection parking) {
        switch (parking) {
            case right: //done parking
                // forward then strafe
                drive(45, DEFAULT_POWER);
                drive(-7, -DEFAULT_POWER);
                strafe(-18, -DEFAULT_POWER);
                drive(28, DEFAULT_POWER);
                strafe(-28, -DEFAULT_POWER);
            case left: // done parking
                // back to wall then strafe
                defaultAutoBackToWall();
                strafe(-46, -DEFAULT_POWER);
        }
    }

    public void autoRB(ParkingDirection parking) {
        switch (parking) {
            case right: // done parking
                drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
                drive(-7, -DEFAULT_POWER);
                strafe(18, DEFAULT_POWER);
                drive(35.5, DEFAULT_POWER);
                strafe(-130, -DEFAULT_POWER);
                drive(-12, -DEFAULT_POWER);
                break;
            case left: //done parking
                drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
                drive(-25, -DEFAULT_POWER);
                moveArm(0, DEFAULT_POWER);
                turnByGyro(90, DEFAULT_POWER);
                drive(80, DEFAULT_POWER);
                break;

        }
    }

    public void autoLR(ParkingDirection parking) {
        // DONE
        switch (parking) {
            case right: /** test it **/
                drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
                drive(-25, -DEFAULT_POWER);
                moveArm(0, DEFAULT_POWER);
                turnByGyro(90, DEFAULT_POWER);
                drive(-80, -DEFAULT_POWER);
                break;
            case left: // done pakeinf
                drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
                drive(-7, -DEFAULT_POWER);
                strafe(18, DEFAULT_POWER);
                drive(35.5, DEFAULT_POWER);
                strafe(-130, -DEFAULT_POWER);
                drive(-12, -DEFAULT_POWER);
                break;
        }
    }

    public void autoRR(ParkingDirection parking) {
        switch (parking) {
            case right: // done parking
                defaultAutoBackToWall();
                strafe(-46, -DEFAULT_POWER);
                break;
            case left: //done parking
                drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
                drive(-7, -DEFAULT_POWER);
                strafe(-18, DEFAULT_POWER);
                drive(29, DEFAULT_POWER);
                strafe(-30, -DEFAULT_POWER);
                break;

        }
    }
}