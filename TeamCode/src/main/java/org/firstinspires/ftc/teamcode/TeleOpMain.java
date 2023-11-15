package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="TeleOp")
public class TeleOpMain extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hw = new Hardware(this);
        hw.init(hardwareMap);

        telemetry.addData("TeleOp: ", "Ready for start, Initialized");
        telemetry.update();

        waitForStart();

        telemetry.addData("TeleOp: ", "Starting...");
        telemetry.update();

        hw.setMotorsToZero();
        while (opModeIsActive()) {
            //temporary code
            //telemetry.addData("TeleOp: ", "opModeActive");

//            double drive = -curveInput(gamepad1.left_stick_y);
//            double turn = curveInput(gamepad1.right_stick_x);
//            double strafe = curveInput(gamepad1.left_stick_x);

            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double strafe = gamepad1.left_stick_x;

            double maxPower = Math.max(Math.abs(drive) + Math.abs(turn) + Math.abs(strafe), 1);


            //driving code
            //telemetry.addData("TeleOp:", "Driving");

            hw.frontLeft.setPower(((drive + turn + strafe) / maxPower));

            hw.frontRight.setPower(((drive - turn - strafe) / maxPower));

            hw.backRight.setPower(((drive - turn + strafe) / maxPower));

            hw.backLeft.setPower(((drive + turn - strafe) / maxPower));

            //hw.telemetryMotorPower();

            //claw code
            boolean clawOpen = gamepad2.right_bumper;
            boolean clawGrab = gamepad2.left_bumper;
            boolean armMoveUp = gamepad2.x;
            boolean armMoveDown = gamepad2.b;
            /*if(armMove && !hw.arm.armIsMoving){ //set claw into position
                hw.arm.turnClaw();
            }
            if(clawPower && !hw.arm.clawIsInMotion){ //grab/let go of pixel
                hw.arm.clawGrab();
            }*/
            if(clawGrab){
                hw.arm.clawGrab();
            } else if(clawOpen){
                hw.arm.clawOpen();
            }

            if(armMoveUp){
                hw.arm.turnMotor.setPower(0.05);
                telemetry.addData("arm: ", "GOING UP");
            } else if(armMoveDown){
                hw.arm.turnMotor.setPower(-0.05 /  6);
                telemetry.addData("arm: ", "going down");
            } else {
                hw.arm.turnMotor.setPower(0);
            }


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
