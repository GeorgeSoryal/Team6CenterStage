package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Autonomous")
public class Auto extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hw = new Hardware(this);
        hw.init(hardwareMap);

        telemetry.addData("Auto: ", "ready for start");
        telemetry.update();

        waitForStart();

        hw.drive(0.8, 26.5);
        //hw.arm.turnMotor.setPower(0.25);
        while(opModeIsActive()){
            //do stuff
            telemetry.addData("FrontLeft Power: ", hw.frontLeft.getPower());
            telemetry.addData("FrontRight Power: ", hw.frontRight.getPower());
            telemetry.addData("BackLeft Power: ", hw.backLeft.getPower());
            telemetry.addData("BackRight Power: ", hw.backRight.getPower());

            //cycle every 10 milliseconds, to prevent memory death --> 100 cycles/s
            sleep(10);
        }

        hw.setMotorsToZero();
    }
}
