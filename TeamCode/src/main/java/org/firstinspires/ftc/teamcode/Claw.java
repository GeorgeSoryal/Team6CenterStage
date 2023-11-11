package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import static org.firstinspires.ftc.teamcode.Hardware.TICKS_PER_INCH;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw extends Hardware{
    private boolean clawIsUp = true;
    private boolean clawIsOpen = true;
    public boolean clawIsMoving = false;
    public boolean clawIsGrabbing = false;
    public DcMotor turnMotor = null;
    public Servo clawServo1 = null;
    public Servo clawServo2 = null;
    private OpMode opMode;

    public Claw(OpMode opMode1) {
        super(opMode1);
    }


    public void init(HardwareMap hardwareMap){
        try{
            turnMotor = hardwareMap.dcMotor.get("turnMotor"); //to do: add turnMotor to hardwareMap config in the driver station
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
        clawIsMoving = true;
        double distance = 1;

        if(clawIsUp){
            turnMotor.setTargetPosition((int) (distance * TICKS_PER_INCH));
            turnMotor.setPower(0.4); //TEST 2: figure out good power for this
        } else {
            turnMotor.setTargetPosition((int) (-distance * TICKS_PER_INCH));
            turnMotor.setPower(-0.4); //TEST 2: this too
        }

        while(turnMotor.getCurrentPosition() <= turnMotor.getTargetPosition()){}

        turnMotor.setPower(0);
        clawIsUp = !clawIsUp;
        clawIsMoving = false;
    }

    public void clawGrab(){
        clawIsGrabbing = true;
        double distance = 0.25;

        if(clawIsOpen){ //close claw
            clawServo1.setPosition(distance);
            clawServo2.setPosition(distance);
        } else { //open claw
            clawServo1.setPosition(0);
            clawServo2.setPosition(0);
        }

        clawIsOpen = !clawIsOpen;
        clawIsGrabbing = false;
    }
}
