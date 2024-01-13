package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="TeleOpFR")
public class TeleOpMain extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hw = new Hardware(this);
        hw.init(hardwareMap);

        boolean isAPressed = false;

        //arm values
        final double armDown = 0;
        final double armUp = -900;

        telemetry.addData("Arm position", hw.clawArm.getCurrentPosition());
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
                double roundedClawPosition = Math.round((hw.clawLeft.getPosition() * 1000)) / 1000.0;
                if(roundedClawPosition == hw.SERVO_1_OPEN_POSITION && !isAPressed){
                    telemetry.addData("ARM OPENING: ", "A PRESSED");
                    hw.clawLeft.setPosition(hw.SERVO_1_CLOSED_POSITION);
                    hw.clawRight.setPosition(hw.SERVO_1_CLOSED_POSITION);
                }else if(!isAPressed){
                    hw.clawLeft.setPosition(hw.SERVO_1_OPEN_POSITION);
                    hw.clawRight.setPosition(hw.SERVO_2_OPEN_POSITION);
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

            if(gamepad1.right_bumper){
                hw.clawArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                hw.clawArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }

//            if(gamepad1.right_trigger > 0.5){
//                if(hw.clawArm.getCurrentPosition() < 0){
//                    hw.clawArm.setPower(0.8);
//                    while(hw.clawArm.getCurrentPosition() < 0);
//                } else {
//                    hw.clawArm.setPower(-0.8);
//                    while(hw.clawArm.getCurrentPosition() > 0);
//                }
//            }

            hw.telemetryHardware();
            telemetry.addData("\nArm position", hw.clawArm.getCurrentPosition());

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
