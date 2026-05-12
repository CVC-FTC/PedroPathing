
/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

/** @noinspection FieldMayBeFinal, FieldCanBeLocal , SpellCheckingInspection */ /*
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 *
 * This particular OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
 * This code will work with either a Mecanum-Drive or an X-Drive train.
 * Both of these drives are illustrated at https://gm0.org/en/latest/docs/robot-design/drivetrains/holonomic.html
 * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
 *
 * Also note that it is critical to set the correct rotation direction for each motor.  See details below.
 *
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * Each motion axis is controlled by one Joystick axis.
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 * This code is written assuming that the right-side motors need to be reversed for the robot to drive forward.
 * When you first test your robot, if it moves backward when you push the left stick forward, then you must flip
 * the direction of all 4 motors (see code below).
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */
//has launcher and intake
@TeleOp(name="Omni Linear OpMode", group="Linear OpMode")
//@Disabled
public class OmniOpMode_Linear extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    //declare launcher motors
    private DcMotor Launcher = null;




    //declare intake motor
    private DcMotor intake = null;

    /** @noinspection ConstantValue*/
    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        //Launcher init
        Launcher = hardwareMap.get(DcMotor.class, "launcher");

        //intake init
        intake = hardwareMap.get(DcMotor.class,"intake");
        //drive direction
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        //Launcher direction
        Launcher.setDirection(DcMotor.Direction.FORWARD);
        Launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        //intake direction
        intake.setDirection(DcMotor.Direction.REVERSE);

        //gamepad data init
        Gamepad currentGamepad1 = new Gamepad();
        Gamepad currentGamepad2 = new Gamepad();
        Gamepad previousGamepad1 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();
        //intake and launcher toggle init
        boolean intakeToggle = false;
        boolean launcherToggle = false;

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();
        //launcher var init
        double launcher_power = 0.5;
        double launcher_yaw = 0;
        //drive speed init
        double drive_speed = 0.75;
        int desired_speed = 1000;


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //gamepad data
            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);
            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);

            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            //intake var init
            double intake_power = 0.75;

            //drive speed controls
            if (currentGamepad1.dpad_up && !previousGamepad1.dpad_up){      //increase by 0.05
                drive_speed += 0.05;
            }
            if (currentGamepad1.dpad_down && !previousGamepad1.dpad_down){      //decrease by 0.05
                drive_speed -= 0.05;
            }
            if (currentGamepad1.dpad_right && !previousGamepad1.dpad_right){      //max drive speed
                drive_speed = 1;
            }
            if (currentGamepad1.dpad_left && !previousGamepad1.dpad_left){      //min drive speed
                drive_speed = 0.1;
            }

            //drive speed normalising
            if (drive_speed>1){
                drive_speed = 1;
            } else if(drive_speed<0.3){
                drive_speed = 0.3;
            }

            //launcher power controls
            if (currentGamepad2.dpad_up && !previousGamepad2.dpad_up){
                launcher_power += 0.1;
            }
            if (currentGamepad2.dpad_down && !previousGamepad2.dpad_down){
                launcher_power -= 0.1;
            }
            //launcher power normalising
            if (launcher_power>1){
                launcher_power = 1;
            } else if(launcher_power<0){
                launcher_power = 0;
            }

            //spin controln nhhb
            if (gamepad2.dpad_right){
                launcher_yaw += 0.1;
            }

            //launcher on/off
            if (currentGamepad2.circle && !previousGamepad2.circle) {
                launcherToggle = !launcherToggle;
            }
            //intake on/off
            if (currentGamepad2.cross && !previousGamepad2.cross) {
                intakeToggle = !intakeToggle;
            }



            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double frontLeftPower  = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                frontRightPower /= max;
                backLeftPower   /= max;
                backRightPower  /= max;
            }

            //multiply by power multiplier
            frontLeftPower  *= drive_speed;
            frontRightPower *= drive_speed;
            backLeftPower   *= drive_speed;
            backRightPower  *= drive_speed;

            //normalising the lower values for drive motor power
            if ((frontLeftPower<0.2)&(frontLeftPower>-0.2)){frontLeftPower = 0;}
            if ((backLeftPower<0.2)&(backLeftPower>-0.2)){backLeftPower = 0;}
            if ((frontRightPower<0.2)&(frontRightPower>-0.2)){frontRightPower = 0;}
            if ((backRightPower<0.2)&(backRightPower>-0.2)){backRightPower = 0;}

            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            //send power to launcher motors
            if (launcherToggle){
                Launcher.setPower(launcher_power);
            }else {
                Launcher.setPower(0);
            }

            if (intakeToggle) {
                intake.setPower(intake_power);
            }
            else {
                intake.setPower(0);
            }


            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.addData("Drive Speed", drive_speed);
            telemetry.addData("Launcher Power", launcher_power);
            telemetry.addData("Launcher Yaw",launcher_yaw);

            if (intakeToggle){
                telemetry.addData("intake on",intake_power);
            }
            if (launcherToggle){
                telemetry.addData("Launcher Velocity", ((DcMotorEx) Launcher).getVelocity());
                telemetry.addData("Launcher Power", Launcher.getPower());
            }

            telemetry.update();
        }

    }
}
