
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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/** @noinspection FieldMayBeFinal, FieldCanBeLocal , SpellCheckingInspection */


//has launcher and intake
@TeleOp(name="Omni Linear OpMode V2", group="Linear OpMode")
//@Disabled
public class OmniOpmode_Linear_V2 extends LinearOpMode {


    private ElapsedTime runtime = new ElapsedTime();

    // Declare OpMode members for each of the 4 motors.
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;

    //declare launcher motors
    private DcMotor launcher = null;        //launcher motor
    private Servo launcherServo = null;             //launcher angle servo

    //spinny centre thing
    private DcMotorEx mover = null;             //spinner motor
    private TouchSensor touchbutton = null;         //ball sensor

    private VoltageSensor myControlHubVoltageSensor;            //volatge sensing for brownout protection

    //declare intake motor
    private DcMotor intake = null;



    /** @noinspection ConstantValue*/
    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");        //front left motor - P0 CH
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");          //back left motor - P1 CH
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");      //front right motor - P2 CH
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");        //back right motor - P3 CH
        //Launcher init
        launcher = hardwareMap.get(DcMotor.class, "launcher");                      //launcher motor - P0 EH
        launcherServo = hardwareMap.get(Servo.class, "Launcher_Servo");             //launcher servo - S0 CH

        //intake init
        intake = hardwareMap.get(DcMotor.class,"intake");                           //intake motor - P1 EH
        mover = hardwareMap.get(DcMotorEx.class,"mover");                           //mover/spinner - P2 EH

        //drive direction
        frontLeftDrive .setDirection(DcMotor.Direction.FORWARD);                                 //front left motor - Forward
        backLeftDrive  .setDirection(DcMotor.Direction.FORWARD);                                  //back left motor - Forward
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);                                //front right motor - Backward
        backRightDrive .setDirection(DcMotor.Direction.REVERSE);                                 //back right motor - Backward
        //drive brake
        frontLeftDrive .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);                   //front left motor - brake
        backLeftDrive  .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);                   //back left motor - brake
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);                  //front right motor - brake
        backRightDrive .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);                   //back right motor - brake

        //Launcher direction runusingencoder and coast/Float
        launcher.setDirection(DcMotor.Direction.FORWARD);                                       //launcher motor - Forward
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);                                    //launcher motor - run based on encoder speed
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);                         //launcher motor - freewheel when not powered

        //intake direction and brake
        //intake.setDirection(DcMotor.Direction.REVERSE);                                         //intake motor - Reverse
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);                           //intake motor - brake when not powered

        mover.setDirection(DcMotor.Direction.REVERSE);                                          //mover/spinner - reverse
        mover.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);                            //mover/spinner - brake when not powered
        //mover.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);                                  //mover/spinner - run on encoder, stop and reset
        //mover.setTargetPosition((mover.getCurrentPosition()));        //set to starting position
        //mover.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        touchbutton = hardwareMap.get(TouchSensor.class, "touch_button");      //touch button

        myControlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");            //Voltage detection



        // toggle init
        boolean intakeToggle = false;       //toggle for intake
        boolean launcherToggle = false;     //toggle for launcher
        boolean boostToggle = false;        //toggle for speed boost

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();
        //launcher var init
        double launcher_power = 0.5;        //power of launcher - depreciated
        int desired_speed = 280;            //current desired speed of launcher - init
        final int low_desired_speed = 1300;        //low value for launcher speed - set value
        final int high_desired_speed = 1600;       //high value for launcher speed - set value
        //drive speed init
        double drive_speed = 0.75;      //normal drive speed
        double boost_speed = 1;         //boost drive speed
        double boosttotal = 4;           //time on boost
        double boosttime = 0;            //boosttime init
        //launching init
        int balls = 0;                   //no. of balls in system
        //boolean touchcooldown = false;        //cooldown test for ball button
        //intake init
        double intake_power = 1;         //intake var init
        //voltage init
        double presentVoltage;          //init for voltage readout
        //boolean  voltagestop = true;           //toggle for voltage pausing
        //temporary message init
        boolean messageon = false;
        int messagetime = 0;
        String message = "";
        //donothing init
        boolean donothing = false;          //for when we need an if statement to do nothing
        //final double quarterturn = ((18.9*28) * 0.247);
        //int cooldowncount = 0;
        //for spinner wiggling
        //int movertemp = 0;
        //double movertarget = 0;
        //int wiggle = 75;
        //boolean reversetoggle = false;



        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            presentVoltage = myControlHubVoltageSensor.getVoltage();        //gets the voltage for this loop


            //-----------------------------------DRIVE CODE-----------------------------------------{
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            //drive speed controls
            if (gamepad1.dpadUpWasPressed()){      //increase by 0.05
                drive_speed += 0.05;
            }
            if (gamepad1.dpadDownWasPressed()){      //decrease by 0.05
                drive_speed -= 0.05;
            }
            if (gamepad1.dpadRightWasPressed()){      //max drive speed
                drive_speed = 0.75;
            }
            if (gamepad1.dpadLeftWasPressed()){      //min drive speed
                drive_speed = 0.3;
            }

            //drive speed normalising
            if (drive_speed>1){
                drive_speed = 1;
            } else if(drive_speed<0.3){
                drive_speed = 0.3;
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
            if (boostToggle){
                frontLeftPower *= boost_speed;
                frontRightPower *= boost_speed;
                backLeftPower *= boost_speed;
                backRightPower *= boost_speed;
            } else {
                frontLeftPower *= drive_speed;
                frontRightPower *= drive_speed;
                backLeftPower *= drive_speed;
                backRightPower *= drive_speed;
            }

            //normalising the lower values for drive motor power
            if ((frontLeftPower<0.2)&(frontLeftPower>-0.2)){frontLeftPower = 0;}
            if ((backLeftPower<0.2)&(backLeftPower>-0.2)){backLeftPower = 0;}
            if ((frontRightPower<0.2)&(frontRightPower>-0.2)){frontRightPower = 0;}
            if ((backRightPower<0.2)&(backRightPower>-0.2)){backRightPower = 0;}


            //----------------------------BOOST CODE------------------------------------------------{
            //boost start
            if (gamepad1.rightBumperWasPressed()){
                if (runtime.milliseconds() < boosttime){
                    boosttime = runtime.milliseconds();                                                 //turns off pbefore timer
                } else{
                    boosttime = runtime.milliseconds() + (1000*boosttotal);                             //sets timer as current time + set time
                }
            }
            //boost toggle
            if (runtime.milliseconds()<boosttime){
                telemetry.addLine("Boost Active");
                double boostleft = ((3000-(runtime.milliseconds()))/1000);                              //calculates the remaining time on boost
                telemetry.addData("Boost time left(s/ms)",boostleft);
                boostToggle = true;
            }else {
                boostToggle = false;
            }
            //--------------------------------------------------------------------------------------}
            //--------------------------------------------------------------------------------------}


            //----------------------------LAUNCHER CODE---------------------------------------------{
            //launcher power controls
            if (gamepad2.dpadUpWasPressed()){desired_speed += 280;      } //+10rps/+600rpm
            if (gamepad2.dpadDownWasPressed()){desired_speed -= 280;    } //-10rps/-600rpm
            //launcher power normalising
            if (launcher_power>1){launcher_power = 1;} else if(launcher_power<0){launcher_power = 0;}       //probably depreciated

            //launcher on/off
            if (gamepad2.circleWasPressed()) {launcherToggle = !launcherToggle;}
            //intake on/off
            if (gamepad2.crossWasPressed()) {intakeToggle = !intakeToggle;}


            //send power to launcher motor
            if (gamepad2.squareWasPressed()) {
                if (desired_speed == low_desired_speed){
                    desired_speed = high_desired_speed;
                } else {
                    desired_speed = low_desired_speed;
                }
            }

            //--------------------------------------------------------------------------------------}

            //---------------------------------LAUNCH ANGLE-----------------------------------------{
            if (gamepad2.triangleWasPressed()){
                if (launcherServo.getPosition()==0.5){
                    launcherServo.setPosition(0.6);
                } else if (launcherServo.getPosition()==0.6){
                    launcherServo.setPosition(0.4);
                } else if (launcherServo.getPosition()==0.4){
                    launcherServo.setPosition(0.5);
                } else {
                    launcherServo.setPosition(0.5);
                }
            }
            //--------------------------------------------------------------------------------------}

            //--------------------------VOLTAGE STOP TOGGLE-----------------------------------------{
            //if (gamepad1.crossWasPressed()) {voltagestop = !voltagestop;}
            //--------------------------------------------------------------------------------------}

            //-------------------------------POWER DELIVERY-----------------------------------------{
            if (intakeToggle) {intake.setPower(intake_power);} else {intake.setPower(0);}       //intake on/off
            mover.setPower(gamepad2.right_trigger);

            if (launcherToggle) {((DcMotorEx) launcher).setVelocity(desired_speed);} else {launcher.setPower(0);}       //launcher motor on/off

            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            //voltage check
            //checks voltage, if voltage is lower than 10V - stop spinner motor

            if (presentVoltage < 10){
                telemetry.addData("VOLTAGE LOW", presentVoltage);
                }


            //--------------------------------------------------------------------------------------}

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            //telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            //telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.addData("Drive Speed", drive_speed);
            telemetry.addData("Launcher Servo Location", launcherServo.getPosition());
            telemetry.addData("number of balls:", balls);
            telemetry.addData("Voltage", presentVoltage);

            if (intakeToggle){
                telemetry.addData("intake on",intake_power);
            }
            if (launcherToggle){
                telemetry.addData("Launcher Velocity", (((DcMotorEx) launcher).getVelocity())/28);
                telemetry.addData("Launcher Power", launcher.getPower());
            }
            if (messageon){
                messagetime+=1;
                telemetry.addLine(message);
                if (messagetime >= 50*3 ){    //each loop is ~50Hz/20ms
                    messageon = false;
                }
            }

            telemetry.update();
        }

    }

}
