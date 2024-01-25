package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TestTeleOp")
public class TeleOpTwo extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        HardwareTwo hw = HardwareTwo.getInstance();
        hw.init(hardwareMap);

        boolean isAPressed = false;

        boolean isYPressed = false;

        waitForStart();

        //arm servo values (claw 1 = claw left)
        //open
        final double servo1open = 0.533;
        final double servo2open = 0.405;
        //close
        final double servo1close = 0.267;
        final double servo2close = 0.659;

        //arm values
        final double armDown = 0;
        final double armUp = -900;

        //0 = stop, 1 = goUp, 2 = goDown
        int armGoMode = 0;
        int priorMode = 2;

        while(opModeIsActive()){


            //code stolen from game manual 1, probably will work, untested
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            hw.setPower(frontRightPower,backRightPower,frontLeftPower,backLeftPower);

            telemetry.addData("Arm Power: ", gamepad2.left_stick_y);
            telemetry.addData("Arm Position: ", hw.clawArm.getCurrentPosition());

            if(gamepad2.a){
                telemetry.addData("servo pos: ",((int)(hw.clawLeft.getPosition() * 1000))/1000.0);
                telemetry.addData("servo target: ", servo1open);
                if(Math.round((hw.clawLeft.getPosition() * 1000))/1000.0 == servo1open){
                    if(!isAPressed){
                        hw.clawLeft.setPosition(servo1close);
                        hw.clawRight.setPosition(servo2close);
                        telemetry.addData("Opening servo", "");
                        telemetry.addData("Servo 1: ", hw.clawLeft.getPosition());
                        telemetry.addData("Servo 2: ", hw.clawRight.getPosition());
                    }
                }else{
                    if(!isAPressed){
                        hw.clawLeft.setPosition(servo1open);
                        hw.clawRight.setPosition(servo2open);
                        telemetry.addData("Closing servo", "");
                        telemetry.addData("Servo 1: ", hw.clawLeft.getPosition());
                        telemetry.addData("Servo 2: ", hw.clawRight.getPosition());
                    }
                }
                isAPressed = true;
            }else{
                isAPressed = false;
            }

            if(gamepad2.y){ //unused code - redundant
                if(!isYPressed){
                    if(armGoMode == 0){
                        if(priorMode == 2){
                            armGoMode = 1;
                        }else{
                            armGoMode = 2;
                        }
                    }else if(armGoMode == 1){
                        armGoMode = 0;
                        priorMode = 1;
                    }else{
                        armGoMode = 0;
                        priorMode = 2;
                    }
                }
                isYPressed = true;
            }else{
                isYPressed = false;
            }

            telemetry.addData("arm pos", armGoMode);
            telemetry.addData("left stick y", gamepad2.left_stick_y);
            telemetry.addData("assocated boolean", gamepad2.left_stick_y != 0);

            if(armGoMode == 1 && hw.clawArm.getCurrentPosition() > armUp){ //unused code - redundant
                hw.clawArm.setPower(-0.5);
            }else if(armGoMode == 2 && hw.clawArm.getCurrentPosition() < armDown){ //unused code - redundant
                hw.clawArm.setPower(0.5);
            }else if(gamepad2.left_stick_y == 0){
                hw.clawArm.setPower(0);
            }else{
                hw.clawArm.setPower(gamepad2.left_stick_y);
            }

            telemetry.update();


        }
    }
}
