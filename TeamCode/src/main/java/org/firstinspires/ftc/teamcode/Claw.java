package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;
import static org.firstinspires.ftc.teamcode.Hardware.TICKS_PER_INCH;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Claw{
    private boolean clawIsUp = true;
    private boolean clawIsOpen = true;
    public boolean clawIsMoving = false;
    public boolean clawIsGrabbing = false;
    public DcMotor turnMotor = null;
    public DcMotor clawMotor = null;

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
            clawMotor = hardwareMap.dcMotor.get("clawMotor"); //to do: add clawMotor to hardwareMap config in the driver station
            clawMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } catch (Exception e){
            opMode.telemetry.addData("clawMotor: ", "Error");
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
        double distance = 1; //TEST 1: figure out good value to set this to

        if(clawIsOpen){
            clawMotor.setTargetPosition((int)(distance * TICKS_PER_INCH));
            clawMotor.setPower(0.4); //TEST 2: figure out good power for this
        } else {
            clawMotor.setTargetPosition((int)(-distance * TICKS_PER_INCH));
            clawMotor.setPower(-0.4); //TEST 2: this too
        }

        while (clawMotor.getCurrentPosition() <= clawMotor.getTargetPosition()){}

        clawIsOpen = !clawIsOpen;
        clawMotor.setPower(0);
        clawIsGrabbing = false;
    }
}
