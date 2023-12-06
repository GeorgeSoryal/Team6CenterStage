package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="TeleOp")
public class TeleOpMain extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hw = new Hardware(this);
        hw.init(hardwareMap);

        boolean isAPressed = false;

        //arm servo values (servo1 = claw left) (servo2 = claw right)
        //open
        final double servo1open = 0.13;
        final double servo2open = 0.133; //claw2 = more movement
        //close
        final double servo1close = servo1open + 0.185;
        final double servo2close = servo2open + 0.160;


        //arm values
        final double armDown = 0;
        final double armUp = -900;

        telemetry.addData("TeleOp: ", "Ready for start, Initialized");
        telemetry.update();

        waitForStart();

        telemetry.addData("TeleOp: ", "Starting...");
        telemetry.update();




        hw.setMotorsToZero();
        while (opModeIsActive()) {
            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double strafe = gamepad1.left_stick_x;

            double maxPower = Math.max(Math.abs(drive) + Math.abs(turn) + Math.abs(strafe), 1);

            hw.frontLeft.setPower(((drive - turn - strafe) / maxPower));
            hw.frontRight.setPower(((drive + turn + strafe) / maxPower));
            hw.backRight.setPower(((drive + turn - strafe) / maxPower));
            hw.backLeft.setPower(((drive - turn + strafe) / maxPower));


            if(gamepad2.a){
                if(Math.round((hw.clawLeft.getPosition() * 1000))/1000.0 == servo1open){
                    if(!isAPressed){
                        hw.clawLeft.setPosition(servo1close);
                        hw.clawRight.setPosition(servo2close);
                    }
                }else{
                    if(!isAPressed){
                        hw.clawLeft.setPosition(servo1open);
                        hw.clawRight.setPosition(servo2open);
                    }
                }
                isAPressed = true;
            }else{
                isAPressed = false;
            }


            if(gamepad2.left_stick_y == 0){
                hw.clawArm.setPower(0);
            }else{
                hw.clawArm.setPower(gamepad2.left_stick_y);
            }

            hw.telemetryHardware();
            telemetry.update();
            //cycle every 10 milliseconds, to prevent memory death --> 100 cycles/s
            sleep(10);
        }

        // sets all to 0 power
        hw.setMotorsToZero();
    }

    public double curveInput(double input) { //NOTE: new function needed, causes drift
        /*
            curve function:
            y = (-2.09 / (1+e^4x)) + 1.04
        */

        return ((-2.09 / (1 + Math.pow(Math.E, 4 * input))) + 1.04);
    }

}
