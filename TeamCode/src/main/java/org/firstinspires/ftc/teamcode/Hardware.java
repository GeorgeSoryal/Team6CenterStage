package org.firstinspires.ftc.teamcode;

//import com.google.blocks.ftcrobotcontroller.runtime.CRServoAccess;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

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
    public DcMotor turnMotor = null;
    public Servo clawServo1 = null;
    public Servo clawServo2 = null;
    private static OpMode opMode;

    public Hardware(OpMode opMode1){
        opMode = opMode1;
//        claw = new Claw(opMode);
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

        try { //turnMotor
            turnMotor = hardwareMap.dcMotor.get("turnMotor");
            turnMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            opMode.telemetry.addData("turnMotor: ", "Initialized");
        } catch(Exception e){
            opMode.telemetry.addData("turnMotor: ", "Error");
        }

        try { //claw servo 1
            clawServo1 = hardwareMap.servo.get("clawServo1");
        } catch (Exception e){
            opMode.telemetry.addData("clawServo1: ", "Error");
        } finally {
            opMode.telemetry.update();
        }

        try { //claw servo 2
            clawServo2 = hardwareMap.servo.get("clawServo2");
            clawServo2.setDirection(Servo.Direction.REVERSE);
        } catch (Exception e){
            opMode.telemetry.addData("clawServo2: ", "Error");
        } finally {
            opMode.telemetry.update();
        }

        //arm.init(hardwareMap);

        // Have to test this when the drive train is created
        setMotorsToZero();
        setAllTargets(0);

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

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

//    public void drive(double power, double inches){
//        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        int targetPosition = (int)(inches*TICKS_PER_INCH);
//        opMode.telemetry.addData("targetPosition linear drive: ", targetPosition);
//        setAllTargets(targetPosition);
//
//        frontLeft.setPower(power);
//        frontRight.setPower(power);
//        backLeft.setPower(power);
//        backRight.setPower(power);
//        while(isNotAtTargetPosition()){
//            opMode.telemetry.addData("Moving in drive: ", frontLeft.getCurrentPosition());
//            telemetryMotorPower();
//            opMode.telemetry.update();
//        }
//
//        setMotorsToZero();
//
//        opMode.telemetry.addData("Linear Drive complete.", "");
//        opMode.telemetry.update();
//    }
//    public void drive(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower, double inches){
//        int targetPosition = (int)(inches*TICKS_PER_INCH);
//        opMode.telemetry.addData("targetPosition for linear drive: ", targetPosition);
//        opMode.telemetry.update();
//
//        setAllTargets(targetPosition);
//
//        frontLeft.setPower(frontLeftPower);
//        frontRight.setPower(frontRightPower);
//        backLeft.setPower(backLeftPower);
//        backRight.setPower(backRightPower);
//        while(isNotAtTargetPosition());
//
//        setMotorsToZero();
//
//        opMode.telemetry.addData("Linear Drive complete.", "");
//        opMode.telemetry.update();
//    }
//    // for distance: right is positive, left is negative
//    public void strafe(double distance, double power) {
//        frontLeft.setTargetPosition((int) (distance * TICKS_PER_INCH));
//        frontRight.setTargetPosition((int) (-distance * TICKS_PER_INCH));
//        backLeft.setTargetPosition((int) (distance * TICKS_PER_INCH));
//        backRight.setTargetPosition((int) (-distance * TICKS_PER_INCH));
//
//        frontLeft.setPower(power);
//        frontRight.setPower(-power);
//        backLeft.setPower(power);
//        backRight.setPower(-power);
//
//        while (isNotAtTargetPosition());
//
//        setMotorsToZero();
//    }
//
//    //positive power -> turn right, negative power -> turn left
//    public void turn(double angle, double power){
//        angle = (angle / 360) * TICKS_PER_MOTOR_REV;
//        int targetPosition = (int) (angle * TICKS_PER_INCH);
//
//        frontLeft.setTargetPosition(targetPosition);
//        frontRight.setTargetPosition(-targetPosition);
//        backLeft.setTargetPosition(targetPosition);
//        backRight.setTargetPosition(-targetPosition);
//
//        frontLeft.setPower(power);
//        frontRight.setPower(-power);
//        backLeft.setPower(power);
//        backRight.setPower(-power);
//
//
//        while (isNotAtTargetPosition());
//
//
//        setMotorsToZero();
//    }

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

        opMode.telemetry.addData("\nclawServo1 position: ", clawServo1.getPosition());
        opMode.telemetry.addData("clawServo2 position: ", clawServo2.getPosition());
        opMode.telemetry.addData("\nturnMotor position: ", turnMotor.getCurrentPosition());
        opMode.telemetry.addData("turnMotor target: ", turnMotor.getTargetPosition());
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

//    public static class Arm {
//        private final double TICKS_PER_MOTOR_REV = 28;
//        private boolean armIsUp = false;
//        private boolean clawIsOpen = true;
//        public boolean armIsMoving = false;
//        public boolean clawIsInMotion = false;
//        public DcMotor turnMotor = null;
//        public Servo clawServo1 = null;
//        public Servo clawServo2 = null;
//
//        public void init(HardwareMap hardwareMap){
//            try{ //arm motor
//                turnMotor = hardwareMap.dcMotor.get("turnMotor");
//                turnMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//                turnMotor.setDirection(DcMotorSimple.Direction.REVERSE);
//                turnMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); //prevents motor from falling due to gravity
//
//            } catch (Exception e){
//                opMode.telemetry.addData("turnMotor: ", "Error");
//            } finally {
//                opMode.telemetry.update();
//            }
//
//            try { //claw servo 1
//                clawServo1 = hardwareMap.servo.get("clawServo1");
//                clawServo1.setPosition(0.3);
//            } catch (Exception e){
//                opMode.telemetry.addData("clawServo1: ", "Error");
//            } finally {
//                opMode.telemetry.update();
//            }
//
//            try { //claw servo 2
//                clawServo2 = hardwareMap.servo.get("clawServo2");
//                clawServo2.setDirection(Servo.Direction.REVERSE);
//                clawServo2.setPosition(0.3);
//            } catch (Exception e){
//                opMode.telemetry.addData("clawServo2: ", "Error");
//            } finally {
//                opMode.telemetry.update();
//            }
//
//
//        }
//        public void turnClaw(){
//            armIsMoving = true;
//            double distance = 700;
//            double offset = 0; //default position when arm is down
//
//            if(armIsUp){ //move arm down
//                turnMotor.setTargetPosition((int)(offset));//distsacne * 0.45
//                turnMotor.setPower(0.2);
//
//                while(turnMotor.getCurrentPosition() > turnMotor.getTargetPosition()){
//                    opMode.telemetry.addData("turnMotor pos: ", turnMotor.getCurrentPosition());
//                    opMode.telemetry.addData("turnMotor target: ", turnMotor.getTargetPosition());
//                    opMode.telemetry.update();
//                }
//
//            } else { //move arm up
//                turnMotor.setTargetPosition((int) (distance + offset)); //-distance * 0.2
//                turnMotor.setPower(-0.2);//goes down in position
//
//                while(turnMotor.getCurrentPosition() < turnMotor.getTargetPosition()){
//                    opMode.telemetry.addData("turnMotor pos: ", turnMotor.getCurrentPosition());
//                    opMode.telemetry.addData("turnMotor target: ", turnMotor.getTargetPosition());
//                    opMode.telemetry.update();
//                }
//            }
//
//            turnMotor.setPower(0);
//            armIsUp = !armIsUp;
//            armIsMoving = false;
//        }

        /*public void clawGrab(){
            //clawIsInMotion = true;

            if(clawIsOpen){ //close claw
                clawServo1.setPosition(0.45);
                clawServo2.setPosition(0.45);
                opMode.telemetry.addData("claw: ", "closing claw");
            } else { //open claw
                clawServo1.setPosition(0.33);
                clawServo2.setPosition(0.33);
                opMode.telemetry.addData("claw: ", "opening claw");
            }
            opMode.telemetry.update();

            clawIsOpen = !clawIsOpen;
            //clawIsInMotion = false;
        }*/

//        public void clawGrab(){
//            clawServo1.setPosition(0.4);
//            clawServo2.setPosition(0.4);
//            opMode.telemetry.addData("claw: ", "closing claw");
//            opMode.telemetry.update();
//        }
//
//        public void clawOpen(){
//            clawServo1.setPosition(0.3);
//            clawServo2.setPosition(0.3);
//            opMode.telemetry.addData("claw: ", "closing claw");
//            opMode.telemetry.update();
//        }
//
//
//    }


}