package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Hardware {
    static final double TICKS_PER_MOTOR_REV = ((((1+((double)46/17))) * (1+((double)46/11))) * 28);
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    final private double DRIVE_SPEED = 0.6;
//    final private double TURN_SPEED = 0.5;
    public final int CLAW_ARM_BACK_POSITION = -1640;
    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;
    //public Arm arm = new Arm();
    public DcMotor clawArm = null;
    public Servo clawLeft = null;
    public Servo clawRight = null;
//    public VisionProcessor visionProcessor;
//    OpenCvCamera camera = null;
//    int cameraMonitorViewId = 0;
//    WebcamName webcamName = null;

    //arm servo values (servo1 = claw right) (servo2 = claw left)
    //open
    // public final double SERVO_OFFSET = 0.25;
    public final double SERVO_1_OPEN_POSITION = 0.417;//0.317; //+ SERVO_OFFSET;//0.97 - 0.11 - 0.12 - 0.07;
    public final double SERVO_2_OPEN_POSITION = 0.465;//0.565; //0.97 - 0.182 +0.05; //claw2 = more movement
    //close
    public final double SERVO_1_CLOSED_POSITION =  0.718;//0.88; //+ SERVO_OFFSET;//SERVO_1_OPEN_POSITION - 0.2;//0.94 - (0.13 + 0.215) - 0.15;
    public final double SERVO_2_CLOSED_POSITION = 0.730;//0.275; //SERVO_2_OPEN_POSITION - 0.2;//0.94- (0.18 + 0.190) ;
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
        } finally{
            opMode.telemetry.update();
        }

        try {
            frontRight = hardwareMap.dcMotor.get("fr");
            frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            opMode.telemetry.addData("FrontRightMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("FrontRightMotor: ", "Error");
        } finally{
            opMode.telemetry.update();
        }

        try {
            backRight = hardwareMap.dcMotor.get("br");
            backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            opMode.telemetry.addData("BackRightMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("BackRightMotor: ", "Error");
        } finally {
            opMode.telemetry.update();
        }

        try {
            backLeft = hardwareMap.dcMotor.get("bl");
            backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            opMode.telemetry.addData("BackLeftMotor: ", "Initialized.");
        } catch (Exception e) {
            opMode.telemetry.addData("BackLeftMotor: ", "Error");
        } finally {
            opMode.telemetry.update();
        }

        /**
         * TODO uncomment when arm is fixed
         */
//        try { //clawArm
//            clawArm = hardwareMap.get(DcMotor.class, "armClaw");
//            clawArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            clawArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            clawArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//            clawArm.setPower(0);
//        } catch(Exception e){
//            opMode.telemetry.addData("clawArm: ", "Error");
//        }

//        try { //claw servo 1
//            clawLeft = hardwareMap.get(Servo.class, "claw1"); // port 0
//            clawLeft.setPosition(0.4);
//        } catch (Exception e){
//            opMode.telemetry.addData("clawLeft: ", "Error");
//        } finally {
//            opMode.telemetry.update();
//        }
//
//        try { //claw servo 2
//            clawRight = hardwareMap.get(Servo.class, "claw2"); // port 1, on the right relative to the arm side
//            clawRight.setDirection(Servo.Direction.REVERSE);
//            clawLeft.getController().pwmEnable();
//            clawRight.setPosition(0.4);
//
//        } catch (Exception e){
//            opMode.telemetry.addData("clawRight: ", "Error");
//        } finally {
//            opMode.telemetry.update();
//        }

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
        }

        try {
//            visionPortal = new VisionPortal.Builder().build();
        } catch (Exception e){
            opMode.telemetry.addData("Camera Error", "ERROR");
            opMode.telemetry.update();
        }

        try {
        } catch (Exception e){
            opMode.telemetry.addData("CV camera error", " ERRRR");
        }

        // Have to test this when the drive train is created
        setMotorsToZero();
        setAllTargets(0);

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
    public void telemetryHardware(){
        opMode.telemetry.addData("FrontLeftPower: ", frontLeft.getPower());
        opMode.telemetry.addData("FrontRightPower: ", frontRight.getPower());
        opMode.telemetry.addData("backRightPower: ", backRight.getPower());
        opMode.telemetry.addData("backLeftPower: ", backLeft.getPower());

        opMode.telemetry.addData("\nclawLeft position: ", clawLeft.getPosition());
        opMode.telemetry.addData("clawRight position: ", clawRight.getPosition());
        opMode.telemetry.addData("\nclawArm position: ", clawArm.getCurrentPosition());
        opMode.telemetry.addData("clawArm target: ", clawArm.getTargetPosition());

        opMode.telemetry.addData("\nGyro angle: ", getGyroAngle()+180.0);
        opMode.telemetry.update();
    }

    public double getGyroAngle(){
        return gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public boolean isNotAtTargetPosition(){
        double currentPos = frontLeft.getCurrentPosition();
        double targetPos = frontLeft.getTargetPosition();
        return !(currentPos >= targetPos);
    }

    public void setAllTargets(int targetPosition){
        frontLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(targetPosition);
        backLeft.setTargetPosition(targetPosition);
        backRight.setTargetPosition(targetPosition);
    }

}