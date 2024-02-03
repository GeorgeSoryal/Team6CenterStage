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
@Autonomous(name="Autonomous")
public class Auto extends LinearOpMode {
    static final double TICKS_PER_MOTOR_REV = ((((1 + ((double) 46 / 17))) * (1 + ((double) 46 / 11))) * 28);
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    //final private double DRIVE_SPEED = 0.6;
    final private double DEFAULT_POWER = 0.7;
    // always absolute values since its distances and its less confusing for me even though DISTANCE_BACK_TO_WALL will
    // never be used as a positive
    final private double DISTANCE_TO_SPIKE_MARK = 27.5;
    final double DISTANCE_BACK_TO_WALL = 25.5;
    OpenCvCamera camera = null;
    int cameraMonitorViewId = 0;
    WebcamName webcamName = null;
    Hardware hw = new Hardware(this);
    PropPosition propPos;


    // left or right in the parking area from the robots perspective
    private enum ParkingDirection {
        left,
        right,
        DEFAULT
    }

    // where the robot itself is
    private enum ParkingMode {
        BlueRight,
        BlueLeft,
        RedRight,
        RedLeft,
        DEFAULT;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);
        hw.setMotorsToZero();
        hw.gyro.resetYaw();
        clampDownClaws();

        // ARM init double checking, especially for auto
        hw.slideArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.slideArm.setTargetPosition(0);
        hw.slideArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
         resetEncoders();

        Pair<ParkingMode, ParkingDirection> autoMode;
        autoMode = getAutoMode();   // Can throw InterruptedException

        webcamName = hardwareMap.get(WebcamName.class, "webcam1");
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()); //USED FOR LIVE PREVIEW
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        Pipeline pipeline = new Pipeline(autoMode.first == ParkingMode.BlueLeft ||
                autoMode.first == ParkingMode.BlueRight);  // default runs red
        camera.setPipeline(pipeline);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(640, 360, OpenCvCameraRotation.UPRIGHT);

            }

            @Override
            public void onError(int errorCode) {
            }

            ;
        });


        hw.slideArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.slideArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        moveArmUp();

        hw.clawMove.setPosition(hw.SERVO_MIDDLE_TILTED_POSITION);
        moveArm(hw.CLAW_ARM_UP_POSITION * 0.75, 0.7);
        while (opModeInInit()) {
            telemetry.addData("Prop position: ", pipeline.getPropPos());
            telemetry.update();
        }

        propPos = pipeline.getPropPos();
        waitForStart();

        // Return to down position
        moveArmDown();

        //  Doesn't run if autoMode is default/ null, IDE doesn't recognize but the while loop is infinite
        //  for the course of when the program will be run so all of Auto
        ParkingDirection parking = autoMode.second;
        boolean hasRun = false;

        while (opModeIsActive() && !hasRun) {
            hasRun = true;

            switch (autoMode.first) {
                // blueLeft and redLeft spike placing is identical
                case BlueLeft:
                    if(propPos == PropPosition.Left)
                        farSpikePlace(true);
                    else if (propPos == PropPosition.Right)
                        nearSpikePlace(false);
                    else if (propPos == PropPosition.Middle)
                        defaultAutoBackToWall();
                    nearParking(parking, true);
                    break;

                case BlueRight:
                    if(propPos == PropPosition.Left)
                        nearSpikePlace(true);
                    else if (propPos == PropPosition.Right)
                        farSpikePlace(false);
                    else if (propPos == PropPosition.Middle)
                        defaultAutoBackToWall();
                    farParking(parking, true);
                    break;

                case RedLeft:
                    if(propPos == PropPosition.Left)
                        farSpikePlace(true);
                    else if (propPos == PropPosition.Right)
                        nearSpikePlace(false);
                    else if (propPos == PropPosition.Middle)
                        defaultAutoBackToWall();
                    farParking(parking, false);
                    break;

                case RedRight:
                    if(propPos == PropPosition.Left)
                        nearSpikePlace(true);
                    else if (propPos == PropPosition.Right)
                        farSpikePlace(false);
                    else if (propPos == PropPosition.Middle)
                        defaultAutoBackToWall();
                    nearParking(parking, false);
                    break;

                default:  // test values
                    strafe(-25, DEFAULT_POWER);
                    strafe(25, -DEFAULT_POWER);  // doesn't stop
                    strafe(25, DEFAULT_POWER);
                    strafe(-25, -DEFAULT_POWER);

                    hw.telemetryHardware();
                    telemetry.update();
                    sleep(2000);
                    break;

            }
        }
    }


    public void drive(double inches, double power) {
        resetEncoders();
        int targetPosition = (int) (inches * TICKS_PER_INCH);

        hw.minPositionDelta = 4;
        hw.setAllTargets(targetPosition);

        hw.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(power);

        while (hw.isNotAtTargetPosition() && opModeIsActive()) ;

        hw.setMotorsToZero();
        resetEncoders();
    }


    public void drive(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower, double inches) {
        resetEncoders();
        int targetPosition = (int) (inches * TICKS_PER_INCH);

        hw.setAllTargets(targetPosition);

        hw.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hw.frontLeft.setPower(frontLeftPower);
        hw.frontRight.setPower(frontRightPower);
        hw.backLeft.setPower(backLeftPower);
        hw.backRight.setPower(backRightPower);

        while (hw.isNotAtTargetPosition() && opModeIsActive()){
            hw.telemetryHardware();
            telemetry.update();
            sleep(2000);
        }

        hw.setMotorsToZero();
        resetEncoders();

    }

    /**
     * for distance: right is negative, left is positive
     */
    public void strafe(double distance, double power) {
        resetEncoders();
        int targetPos = (int) (distance * TICKS_PER_INCH);
        hw.minPositionDelta = 4;

        hw.frontLeft.setTargetPosition(targetPos);
        hw.frontRight.setTargetPosition(-targetPos);
        hw.backLeft.setTargetPosition(-targetPos);
        hw.backRight.setTargetPosition(targetPos);

        hw.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hw.frontLeft.setPower(power);
        hw.frontRight.setPower(power);
        hw.backLeft.setPower(power);
        hw.backRight.setPower(power);

        while (hw.isNotAtTargetPosition() && opModeIsActive()) ;

        hw.setMotorsToZero();
        resetEncoders();
    }

    /**
     * positive angle -> turn left, negative angle -> turn right
     */
    public void turnByEncoder(double angle, double power) {
        resetEncoders();
        angle = (angle / 360) * (8 * TICKS_PER_MOTOR_REV); //8 motor revs = 360 degree turn
        int targetPosition = (int) angle;

        hw.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hw.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hw.minPositionDelta = 4;
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
        resetEncoders();
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
            // Course correction
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
        hw.clawLeft.setPosition(hw.SERVO_LEFT_CLOSED_POSITION);
        hw.clawRight.setPosition(hw.SERVO_RIGHT_CLOSED_POSITION);
    }

    private void clampOpenClaws(){
        hw.clawLeft.setPosition(hw.SERVO_LEFT_OPEN_POSITION);
        hw.clawRight.setPosition(hw.SERVO_RIGHT_OPEN_POSITION);
    }

    /**
     * Drivetrain motors Stop and reset and then sets the motors to using encoders at the end.
     */
    public void resetEncoders() {
        hw.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hw.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }



    /**
     * @return Pair: first = mode, second = parking
     **/
    public Pair<ParkingMode, ParkingDirection> getAutoMode() throws InterruptedException {
//        hw.slideArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        String defaultYN = "";
        ParkingMode parkingMode = null;
        String robotPosition = "";

        telemetry.addData("RUN DEFAULT? Y (left) / N (RIGHT)", "");
        telemetry.update();
        defaultYN += getInput(new String[]{"Y", "N"});

        if(defaultYN.equals("Y")){
            telemetry.addData("Starting", "");
            telemetry.update();
            return new Pair<>(ParkingMode.DEFAULT, ParkingDirection.DEFAULT);
        }
        telemetry.addData("Enter robot position: ",
                "\n - RedLeft: [dpad left]\n" +
                        " - RedRight: [dpad right]\n" +
                        " - BlueLeft: [dpad up]\n" +
                        " - BlueRight: [dpad down]");
        telemetry.update();

        parkingMode = getInput(new ParkingMode[]{
                ParkingMode.RedLeft,
                ParkingMode.RedRight,
                ParkingMode.BlueLeft,
                ParkingMode.BlueRight});

        telemetry.addData("Enter Parking Position: ",
                "\n - Left: [dpad left]" +
                "\n - Right: [dpad right]");
        telemetry.update();
        ParkingDirection parking = getInput(new ParkingDirection[] {ParkingDirection.left, ParkingDirection.right});

        telemetry.addData("Set ", "to go");
        telemetry.update();


        return new Pair<ParkingMode, ParkingDirection>(parkingMode, parking);
    }

    // any type T
    public <T> T getInput(T[] modes) throws InterruptedException {
        while (opModeInInit()) {
            if (gamepad1.dpad_left && modes[0] != null) {
                sleep(500);
                return modes[0];
            } else if (gamepad1.dpad_right && modes[1] != null) {
                sleep(500);
                return modes[1];
            } else if (gamepad1.dpad_up && modes[2] != null) {
                sleep(500);
                return modes[2];
            } else if (gamepad1.dpad_down && modes[3] != null) {
                sleep(500);
                return modes[3];
            }
            sleep(30);
        }
        throw new InterruptedException("Op Mode not meant to be init or this is not meant to be running.");
    }

    public void defaultAutoBackToWall() {
        drive(DISTANCE_TO_SPIKE_MARK, DEFAULT_POWER);
        drive(-DISTANCE_BACK_TO_WALL, -DEFAULT_POWER);
    }

    public void moveArm(double targetPosition, double power){
        hw.slideArm.setTargetPosition((int)targetPosition);
        if(targetPosition > hw.slideArm.getCurrentPosition()){
            hw.slideArm.setPower(power);
            while(hw.slideArm.getCurrentPosition() < targetPosition && opModeIsActive());
        } else {
            hw.slideArm.setPower(-power);
            while(hw.slideArm.getCurrentPosition() > targetPosition && opModeIsActive());
        }

        hw.slideArm.setPower(0);
    }

    public void moveArmUp(){
        hw.slideArm.setTargetPosition(hw.CLAW_ARM_UP_POSITION);
        hw.slideArm.setPower(DEFAULT_POWER);
    }
    public void moveArmDown(){
        hw.slideArm.setTargetPosition(0);
        hw.slideArm.setPower(DEFAULT_POWER);
    }

    /**
     * Used on left blue, left red, right red:
     */
    public void farSpikePlace(boolean isOnLeftSpikeMark){
        int turnDirection = isOnLeftSpikeMark ? 1 : -1;
        drive(DISTANCE_TO_SPIKE_MARK - 5, DEFAULT_POWER);
        strafe(turnDirection * 15, DEFAULT_POWER);
        drive(-DISTANCE_BACK_TO_WALL + 5, DEFAULT_POWER);
        strafe(turnDirection * -15, DEFAULT_POWER);
        moveArmDown();
    }

    /**
     * used on right blue, right red
     */
    public void nearSpikePlace(boolean isOnLeftSpikeMark){
        int turnDirection = isOnLeftSpikeMark ? 1 : -1;
        drive(DISTANCE_TO_SPIKE_MARK - 5, DEFAULT_POWER);
        turnByEncoder(turnDirection * 90, DEFAULT_POWER);
        // Pushes spike pixel
        drive(10, DEFAULT_POWER);  // TODO: TWEAK value 10
        drive(-10, DEFAULT_POWER);
        turnByEncoder(turnDirection * -90, DEFAULT_POWER);
        drive(-DISTANCE_BACK_TO_WALL + 5,DEFAULT_POWER);
        moveArmDown();
    }


    public void nearParking(ParkingDirection parking, boolean isBlue) {
        int turnDirection = isBlue ? 1 : -1;
        switch (parking) {
            case right:
                strafe(turnDirection * -18, DEFAULT_POWER);
                drive(28, DEFAULT_POWER);
                turnByEncoder(turnDirection * -90, DEFAULT_POWER);
                drive(26, DEFAULT_POWER);
                moveArmUp();
                clampOpenClaws();
                moveArmDown();
                strafe(turnDirection * 28, -DEFAULT_POWER);
            case left:
                strafe(turnDirection * -46, DEFAULT_POWER);
        }
    }

    public void farParking(ParkingDirection parking, boolean isBlue) {
        int turnDirection = isBlue ? 1 : -1;
        switch (parking) {
            case right: // done parking
                strafe(turnDirection * -18, DEFAULT_POWER);
                drive(35.5, DEFAULT_POWER);
                strafe(turnDirection * 130, -DEFAULT_POWER);
                drive(-12, -DEFAULT_POWER);
                break;
            case left: //done parking
                turnByEncoder(turnDirection * 90, DEFAULT_POWER);
                drive(80, DEFAULT_POWER);
                break;
        }
    }
}