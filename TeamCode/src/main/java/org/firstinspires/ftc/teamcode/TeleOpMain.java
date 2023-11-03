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

        while (opModeIsActive()) {
            //temporary code
            telemetry.addData("TeleOp: ", "opModeActive");

            double drive = -curveInput(gamepad1.left_stick_y);
            double turn = curveInput(gamepad1.right_stick_x);
            double strafe = curveInput(gamepad1.left_stick_x);

//            double drive = -gamepad1.left_stick_y;
//            double turn = gamepad1.right_stick_x;
//            double strafe = gamepad1.left_stick_x;

            double maxPower = Math.max(Math.abs(drive) + Math.abs(turn) + Math.abs(strafe), 1);


            //to do: verify this works
            telemetry.addData("TeleOp:", "Driving");

            hw.frontLeft.setPower((drive + turn ) / maxPower);
            telemetry.addData("frontLeft: ", hw.frontLeft.getPower());

            hw.frontRight.setPower((drive - turn) / maxPower);
            telemetry.addData("frontRight: ", hw.frontRight.getPower());

            hw.backRight.setPower((drive + turn) / maxPower);
            telemetry.addData("backRight: ", hw.backRight.getPower());

            hw.backLeft.setPower((drive - turn) / maxPower);
            telemetry.addData("backLeft: ", hw.backLeft.getPower());

            telemetry.update();

            //cycle every 10 milliseconds, to prevent memory death --> 100 cycles/s
            sleep(10);
        }

        // sets all to 0 power
        hw.setMotorsToZero();
    }

    public double curveInput(double input) {
        /*
            curve function:
            y = (-2.09 / (1+e^4x)) + 1.04
        */

        return ((-2.09 / (1 + Math.pow(Math.E, 4 * input))) + 1.04);
    }
}
