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
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION)/ (WHEEL_DIAMETER_INCHES * 3.1415);
    final private double DRIVE_SPEED = 0.6;
//    final private double TURN_SPEED = 0.5;
    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;
    public Arm arm = new Arm();
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

        arm.init(hardwareMap);

        // Have to test this when the drive train is created
        setMotorsToZero();
        frontLeft.setTargetPosition(0);
        frontRight.setTargetPosition(0);
        backLeft.setTargetPosition(0);
        backRight.setTargetPosition(0);

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

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
        while(isNotAtTargetPosition()){
            opMode.telemetry.addData("Moving in drive: ", frontLeft.getCurrentPosition());
            telemetryMotorPower();
            opMode.telemetry.update();
        }

        setMotorsToZero();

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

        setMotorsToZero();

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

        setMotorsToZero();
    }

    //positive power -> turn right, negative power -> turn left
    public void turn(double distance, double power){
        int targetPosition = (int) (distance * TICKS_PER_INCH);

        frontLeft.setTargetPosition(targetPosition);
        frontRight.setTargetPosition(-targetPosition);
        backLeft.setTargetPosition(targetPosition);
        backRight.setTargetPosition(-targetPosition);

        frontLeft.setPower(power);
        frontRight.setPower(-power);
        backLeft.setPower(power);
        backRight.setPower(-power);


        while (isNotAtTargetPosition());


        setMotorsToZero();
    }

    public void setMotorsToZero(){
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    public void telemetryMotorPower(){
        opMode.telemetry.addData("FrontLeftPower: ", frontLeft.getPower());
        opMode.telemetry.addData("FrontRightPower: ", frontRight.getPower());
        opMode.telemetry.addData("backRightPower: ", backRight.getPower());
        opMode.telemetry.addData("backLeftPower: ", backLeft.getPower());
    }
    private boolean isNotAtTargetPosition(){
        return frontLeft.getCurrentPosition() < frontLeft.getTargetPosition() /*&&
                backLeft.getCurrentPosition() < backLeft.getTargetPosition() &&
                frontRight.getCurrentPosition() < frontRight.getTargetPosition() &&
                backRight.getCurrentPosition() < backRight.getTargetPosition()*/;
    }


    public static class Arm {
        private boolean armIsUp = false;
        private boolean clawIsOpen = true;
        public boolean armIsMoving = false;
        public boolean clawIsInMotion = false;
        public DcMotor turnMotor = null;
        public Servo clawServo1 = null;
        public Servo clawServo2 = null;

        public void init(HardwareMap hardwareMap){
            try{
                turnMotor = hardwareMap.dcMotor.get("turnMotor");
                turnMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            } catch (Exception e){
                opMode.telemetry.addData("turnMotor: ", "Error");
            } finally {
                opMode.telemetry.update();
            }

            try {
                clawServo1 = hardwareMap.servo.get("clawServo1");
                clawServo1.setPosition(0);
            } catch (Exception e){
                opMode.telemetry.addData("clawServo1: ", "Error");
            } finally {
                opMode.telemetry.update();
            }

            try {
                clawServo2 = hardwareMap.servo.get("clawServo2");
                clawServo2.setDirection(Servo.Direction.REVERSE);
                clawServo2.setPosition(0);
            } catch (Exception e){
                opMode.telemetry.addData("clawServo2: ", "Error");
            } finally {
                opMode.telemetry.update();
            }
        }

        //TEST 1: figure out good distance values for these two methods
        //TEST 2: figure out good power values for these two methods
        public void turnClaw(){
            armIsMoving = true;
            double distance = 1;

            if(armIsUp){
                turnMotor.setTargetPosition((int) (distance * TICKS_PER_INCH));
                turnMotor.setPower(0.4); //TEST 2: figure out good power for this
            } else {
                turnMotor.setTargetPosition((int) (-distance * TICKS_PER_INCH));
                turnMotor.setPower(-0.4); //TEST 2: this too
            }

            while(turnMotor.getCurrentPosition() < turnMotor.getTargetPosition()){
                opMode.telemetry.addData("Turning: ", turnMotor.getCurrentPosition());
            }

            turnMotor.setPower(0);
            armIsUp = !armIsUp;
            armIsMoving = false;
        }

        public void clawGrab(){
            clawIsInMotion = true;
            double distance = 0.25;

            if(clawIsOpen){ //close claw
                clawServo1.setPosition(distance);
                clawServo2.setPosition(distance);
            } else { //open claw
                clawServo1.setPosition(0);
                clawServo2.setPosition(0);
            }

            clawIsOpen = !clawIsOpen;
            clawIsInMotion = false;
        }
    }


}