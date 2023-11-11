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

        hw.drive(0.5, 20);
        while(opModeIsActive()){
            //do stuff


            //cycle every 10 milliseconds, to prevent memory death --> 100 cycles/s
            sleep(10);
        }

        hw.setMotorsToZero();
    }
}
