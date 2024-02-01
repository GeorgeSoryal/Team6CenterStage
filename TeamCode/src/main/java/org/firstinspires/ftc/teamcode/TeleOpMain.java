package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;


@TeleOp(name="TeleOpFR")
public class TeleOpMain extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hw = new Hardware(this);
        hw.init(hardwareMap);

        boolean isAPressed = false;
        boolean isBPressed = false;
        boolean isClawOpened = true;

        //arm values
        final double armDown = 0;
        final double armUp = -900;

        telemetry.addData("TeleOp: ", "Ready for start, Initialized");
        telemetry.update();

        waitForStart();


        //hw.camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
//        {
//            @Override
//            public void onOpened()
//            {
//                // Usually this is where you'll want to start streaming from the camera (see section 4)
//                //hw.camera.startStreaming(1280,720, OpenCvCameraRotation.UPRIGHT);
//                //pipeLine.addTelemetry(telemetry);
//                //hw.camera.setPipeline(pipeLine);
//            }
//            @Override
//            public void onError(int errorCode)
//            {
//                /*
//                 * This will be called if the camera could not be opened
//                 */
//            }
//        });

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


            if(gamepad2.a){ // open/closes claw
                double roundedClawPosition = Math.round((hw.clawLeft.getPosition() * 1000)) / 1000.0;
                if(roundedClawPosition == hw.SERVO_RIGHT_OPEN_POSITION && !isAPressed){
                    hw.clawLeft.setPosition(hw.SERVO_LEFT_CLOSED_POSITION);
                    hw.clawRight.setPosition(hw.SERVO_RIGHT_CLOSED_POSITION);
                }else if(!isAPressed){
                    hw.clawLeft.setPosition(hw.SERVO_LEFT_OPEN_POSITION);
                    hw.clawRight.setPosition(hw.SERVO_RIGHT_OPEN_POSITION);
                }

                isAPressed = true;
            }else{
                isAPressed = false;
            }

//            if(gamepad2.a && !isAPressed){ // open/closes claw
//                //double roundedClawPosition = Math.round((hw.clawLeft.getPosition() * 1000)) / 1000.0;
////                if(hw.c == hw.SERVO_RIGHT_OPEN_POSITION){
//
//                hw.clawLeft.setPosition(hw.SERVO_LEFT_CLOSED_POSITION);
//                hw.clawRight.setPosition(hw.SERVO_RIGHT_CLOSED_POSITION);
//                isAPressed = true;
//            }else {
//                hw.clawLeft.setPosition(hw.SERVO_LEFT_OPEN_POSITION);
//                hw.clawRight.setPosition(hw.SERVO_RIGHT_OPEN_POSITION);
//                isAPressed = false;
//            }



            if(gamepad2.left_stick_y == 0){
                hw.slideArm.setPower(0);
            }else if(hw.slideArm.getCurrentPosition() < hw.CLAW_ARM_UP_POSITION){
                if(hw.slideArm.getCurrentPosition() < 50){
                    hw.slideArm.setPower(gamepad2.left_stick_y / 3.5);
                }
                hw.slideArm.setPower(gamepad2.left_stick_y / 2);
            }

            if (gamepad2.b){ // tilts claw
                double roundedClawPosition = Math.round((hw.clawMove.getPosition() * 1000)) / 1000.0;
                if(roundedClawPosition == hw.SERVO_MIDDLE_LEVEL_POSITION && !isBPressed){
                    hw.clawMove.setPosition(hw.SERVO_MIDDLE_TILTED_POSITION);
                } else if (!isBPressed){
                    hw.clawMove.setPosition(hw.SERVO_MIDDLE_LEVEL_POSITION);
                }

                isBPressed = true;
            } else {
                isBPressed = false;
            }

            if(gamepad1.right_bumper){
                hw.slideArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                hw.slideArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }


            hw.telemetryHardware();


            telemetry.update();
            //cycle every 10 milliseconds, to prevent memory death --> 100 cycles/s
            sleep(10);
        }

        // sets all to 0 power
        hw.setMotorsToZero();
    }

}
