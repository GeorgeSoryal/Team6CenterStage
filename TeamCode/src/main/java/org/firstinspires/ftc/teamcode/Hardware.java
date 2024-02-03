package org.firstinspires.ftc.teamcode;

//import com.google.blocks.ftcrobotcontroller.runtime.CRServoAccess;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

/**
 * middle servo port: 0
 * left servo port: 2
 * right servo poprt: 4
 */
public class Hardware {
    static final double TICKS_PER_MOTOR_REV = ((((1+((double)46/17))) * (1+((double)46/11))) * 28);
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
//    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    public final int CLAW_ARM_UP_POSITION = 2290;
    public final int CLAW_ARM_DANGER_POSITION = 15;
    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;
    public DcMotor droneLauncherArm = null;
    public DcMotor slideArm = null;
    public Servo clawLeft = null;
    public Servo clawRight = null;
    public Servo clawMove = null;


    public final double SERVO_LEFT_OPEN_POSITION = 0.322;
    public final double SERVO_RIGHT_OPEN_POSITION = 0.217;
    //close
    public final double SERVO_LEFT_CLOSED_POSITION =  0.211;
    public final double SERVO_RIGHT_CLOSED_POSITION = 0.35;
    public final double SERVO_MIDDLE_LEVEL_POSITION = 0.59;
    public  final  double SERVO_MIDDLE_TILTED_POSITION = 0.4;

//    public AprilTag
    public IMU gyro;

    private static OpMode opMode;

    public Hardware(OpMode opMode1){
        opMode = opMode1;
//        claw = new Claw(opMode);
    }
    public void init(HardwareMap hardwareMap) {
        try {
            frontLeft = hardwareMap.dcMotor.get("fl");
            frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            opMode.telemetry.addData("FrontLeftMotor: ", "Initialized");
        } catch (Exception e) {
            opMode.telemetry.addData("FrontLeftMotor: ", "Error");
        }

        try {
            frontRight = hardwareMap.dcMotor.get("fr");
            frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            opMode.telemetry.addData("FrontRightMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("FrontRightMotor: ", "Error");
        }

        try {
            backRight = hardwareMap.dcMotor.get("br");
            backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            opMode.telemetry.addData("BackRightMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("BackRightMotor: ", "Error");
        }

        try {
            backLeft = hardwareMap.dcMotor.get("bl");
            backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            opMode.telemetry.addData("BackLeftMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("BackLeftMotor: ", "Error");
        }

        try { //slideArm
            slideArm = hardwareMap.get(DcMotor.class, "armClaw");
            slideArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            slideArm.setDirection(DcMotorSimple.Direction.REVERSE);  // Positive is up now, easier
            slideArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slideArm.setPower(0);
        } catch(Exception e){
            opMode.telemetry.addData("slideArm: ", "Error");
        }

        try{
            clawMove = hardwareMap.get(Servo.class, "middleServo");
            clawMove.setPosition(SERVO_MIDDLE_LEVEL_POSITION);
        } catch (Exception e){
            opMode.telemetry.addData("middleServo: ", "error");
        }

        try { //claw servo 1
            clawLeft = hardwareMap.get(Servo.class, "leftServo"); // port 0
            clawLeft.setPosition(SERVO_LEFT_OPEN_POSITION);
        } catch (Exception e){
            opMode.telemetry.addData("clawLeft: ", "Error");
        }

        try { //claw servo 2
            clawRight = hardwareMap.get(Servo.class, "rightServo"); // port 1, on the right relative to the arm side
            clawRight.setPosition(SERVO_RIGHT_OPEN_POSITION);

        } catch (Exception e){
            opMode.telemetry.addData("clawRight: ", "Error");
        }

        try {
            gyro = hardwareMap.get(IMU.class, "imu");
            // might be backwards
            gyro.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                    RevHubOrientationOnRobot.LogoFacingDirection.FORWARD, RevHubOrientationOnRobot.UsbFacingDirection.UP)));
            gyro.resetYaw();
        }
        catch(Exception e){
            opMode.telemetry.addData("Gyro: ", "ERROR");
            opMode.telemetry.update();
        } try {
            droneLauncherArm = hardwareMap.get(DcMotor.class, "droneLauncher");
            droneLauncherArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            droneLauncherArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            droneLauncherArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            droneLauncherArm.setPower(0);
        } catch (Exception e) {
            opMode.telemetry.addData("DroneLauncherArm ", "ERROR");
            opMode.telemetry.update();
        }

        // Have to test this when the drive train is created
//        setMotorsToZero();
        setAllTargets(0);
//
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        opMode.telemetry.addData("Hardware Init: ", "Success.");
        opMode.telemetry.update();
    }


    public void setMotorsToZero(){
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Needs teleop or auto class to update telemetry for it to ensure that those classes can also add
     *  to the telemetry before the feedback is pushed to the driver station.
     */
    public void telemetryHardware(){
        opMode.telemetry.addData("FrontLeftPower: ", frontLeft.getPower());
        opMode.telemetry.addData("FrontRightPower: ", frontRight.getPower());
        opMode.telemetry.addData("backRightPower: ", backRight.getPower());
        opMode.telemetry.addData("backLeftPower: ", backLeft.getPower());

        opMode.telemetry.addData("\nclawLeft position: ", clawLeft.getPosition());
        opMode.telemetry.addData("clawRight position: ", clawRight.getPosition());
        opMode.telemetry.addData("\nslideArm position: ", slideArm.getCurrentPosition());
        opMode.telemetry.addData("slideArm target: ", slideArm.getTargetPosition());

        opMode.telemetry.addData("\n Gyro angle: ", getGyroAngle());
        opMode.telemetry.addData("\n Drone launcher position ", droneLauncherArm.getCurrentPosition());
        opMode.telemetry.addData("Drone launcher target ", droneLauncherArm.getTargetPosition());
//        opMode.telemetry.update();
    }

    public double getGyroAngle(){
        return gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public boolean isNotAtTargetPosition(){
        double currentPos = frontLeft.getCurrentPosition();
        double targetPos = frontLeft.getTargetPosition();
        return Math.abs(currentPos - targetPos) > 5;
    }

    public void setAllTargets(int targetPosition){
        frontLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(targetPosition);
        backLeft.setTargetPosition(targetPosition);
        backRight.setTargetPosition(targetPosition);
        slideArm.setTargetPosition(targetPosition);
    }

}