package org.firstinspires.ftc.teamcode;

//import com.google.blocks.ftcrobotcontroller.runtime.CRServoAccess;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.vision.VisionPortal;

public class Hardware {
    static final double TICKS_PER_MOTOR_REV = ((((1+((double)46/17))) * (1+((double)46/11))) * 28);
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 3.78;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    final private double DRIVE_SPEED = 0.6;
//    final private double TURN_SPEED = 0.5;
    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;
    //public Arm arm = new Arm();
    public DcMotor clawArm = null;
    public Servo clawLeft = null;
    public Servo clawRight = null;
    public VisionPortal visionPortal;

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

        try { //clawArm
            clawArm = hardwareMap.get(DcMotor.class, "armClaw");
            clawArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            clawArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            clawArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            clawArm.setPower(0);
        } catch(Exception e){
            opMode.telemetry.addData("clawArm: ", "Error");
        }

        try { //claw servo 1
            clawLeft = hardwareMap.servo.get("claw1"); // port 0
        } catch (Exception e){
            opMode.telemetry.addData("clawLeft: ", "Error");
        } finally {
            opMode.telemetry.update();
        }

        try { //claw servo 2
            clawRight = hardwareMap.servo.get("claw2"); // port 1, on the right relative to the arm side
            clawRight.setDirection(Servo.Direction.REVERSE);
        } catch (Exception e){
            opMode.telemetry.addData("clawRight: ", "Error");
        } finally {
            opMode.telemetry.update();
        }

        try {
            visionPortal = new VisionPortal.Builder().build();
        } catch (Exception e){
            opMode.telemetry.addData("Camera Error", "ERROR");
            opMode.telemetry.update();
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
    }
    public boolean isNotAtTargetPosition(){
        double currentPos = frontLeft.getCurrentPosition();
        double targetPos = frontLeft.getTargetPosition();
        if(currentPos >= targetPos){
            return currentPos > targetPos;
        } else {
            return currentPos < targetPos;
        }
    }

    public void setAllTargets(int targetPosition){
        frontLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(targetPosition);
        backLeft.setTargetPosition(targetPosition);
        backRight.setTargetPosition(targetPosition);
    }

}