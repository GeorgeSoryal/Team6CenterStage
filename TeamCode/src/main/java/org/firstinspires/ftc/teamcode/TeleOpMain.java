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
        hw.gyro.resetYaw();

        double roundedClawPosition;
        int armTargetPosition = 0;

        boolean isAPressed = false;
        boolean isBPressed = false;

        telemetry.addData("TeleOp: ", "Ready for start, Initialized");
        telemetry.update();

        waitForStart();

        hw.setMotorsToZero();
        while (opModeIsActive()) {
//            armTargetPosition = (int) (hw.slideArm.getCurrentPosition() - (gamepad2.left_stick_y * (hw.CLAW_ARM_UP_POSITION - hw.CLAW_ARM_DOWN_POSITION)));

            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double strafe = gamepad1.left_stick_x;

            double maxPower = Math.max(Math.abs(drive) + Math.abs(turn) + Math.abs(strafe), 1);

            hw.frontLeft.setPower(((drive - turn - strafe) / maxPower));
            hw.frontRight.setPower(((drive + turn + strafe) / maxPower));
            hw.backRight.setPower(((drive + turn - strafe) / maxPower));
            hw.backLeft.setPower(((drive - turn + strafe) / maxPower));

            double armPower = 0.8;
            if(gamepad2.right_bumper) {
                if (armTargetPosition < hw.CLAW_ARM_UP_POSITION){
                    armTargetPosition += 45;
                    armPower = 0.88;
                }
            } else if(gamepad2.left_bumper){
                if(armTargetPosition > hw.CLAW_ARM_DANGER_POSITION){
                    armTargetPosition -= 35;
                    armPower = 0.55;
                }
            } else {
                armTargetPosition = hw.slideArm.getCurrentPosition();
            }
            hw.slideArm.setTargetPosition(armTargetPosition);
            hw.slideArm.setPower(armPower);

            if(gamepad2.x)
                hw.droneLauncherArm.setTargetPosition(180);
            if(gamepad2.y)
                hw.droneLauncherArm.setTargetPosition(550);
            if(gamepad2.left_bumper)

//            hw.slideArm.setTargetPosition(armTargetPosition);

//            if(armTargetPosition < hw.CLAW_ARM_DANGER_POSITION)
//                hw.slideArm.setPower(0);
//            else if(armTargetPosition < hw.CLAW_ARM_DOWN_POSITION)
//                hw.slideArm.setPower(SLIDE_ARM_SLOW_SPEED);
//            hw.slideArm.setPower(/*hw.slideArm.getCurrentPosition() > armTargetPosition ? -0.8 :*/ 0.8); //
//            else
//                hw.slideArm.setPower(SLIDE_ARM_SPEED);

            if(gamepad2.a) { // open/closes claw
                roundedClawPosition = Math.round((hw.clawRight.getPosition() * 1000)) / 1000.0;
                telemetry.update();
                if(roundedClawPosition == hw.SERVO_RIGHT_OPEN_POSITION && !isAPressed){
                    hw.clawLeft.setPosition(hw.SERVO_LEFT_CLOSED_POSITION);
                    hw.clawRight.setPosition(hw.SERVO_RIGHT_CLOSED_POSITION);
                } else if(!isAPressed){
                    hw.clawLeft.setPosition(hw.SERVO_LEFT_OPEN_POSITION);
                    hw.clawRight.setPosition(hw.SERVO_RIGHT_OPEN_POSITION);
                }
                isAPressed = true;
            } else{
                isAPressed = false;
            }

            if (gamepad2.b){ // tilts claw
                roundedClawPosition = Math.round((hw.clawMove.getPosition() * 1000)) / 1000.0;
                if(roundedClawPosition == hw.SERVO_MIDDLE_LEVEL_POSITION && !isBPressed){
                    hw.clawMove.setPosition(hw.SERVO_MIDDLE_TILTED_POSITION);
                } else if (!isBPressed){
                    hw.clawMove.setPosition(hw.SERVO_MIDDLE_LEVEL_POSITION);
                }

                isBPressed = true;
            } else {
                isBPressed = false;
            }

//            if(gamepad1.right_bumper){  // emergency button
//                hw.slideArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                hw.slideArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }

            hw.telemetryHardware();

            telemetry.update();

            //cycle every 10 milliseconds, to prevent memory death --> 100 cycles/s
            sleep(10);
        }

        // sets all to 0 power
        hw.setMotorsToZero();
    }

    private int linearSlideSpeedCurve(int targetPosition) {
        return (int) (0.258788 * Math.log10(0.0416733 * targetPosition)); // speed, generated with wolfram alpha, hits
        // and uses the danger encoder position values
    }

}
