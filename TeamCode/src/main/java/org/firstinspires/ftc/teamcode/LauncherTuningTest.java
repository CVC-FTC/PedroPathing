package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;


@TeleOp

public class LauncherTuningTest extends OpMode {
    public DcMotorEx launcher;

    public double highspeed = 1500;
    public double lowspeed = 1000;

    double currentTargetSpeed = highspeed;
    double F = 0;
    double P = 0;

    double[] stepsizes = {10.0, 1.0, 0.1, 0.001, 0.0001};

    int stepindex = 1;


    @Override
    public void init(){
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);

        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        telemetry.addLine("init complete");

    }

    @Override
    public void loop(){
        //get all gamepad commands
        //set target velocity
        //update tele
        if (gamepad2.circleWasPressed()){
            if (currentTargetSpeed == highspeed){
                currentTargetSpeed = lowspeed;
            } else{
                currentTargetSpeed = highspeed;
            }
        }
        if (gamepad2.crossWasPressed()){
            stepindex = (stepindex+1) % (stepsizes.length);
        }
        if (gamepad2.dpadLeftWasPressed()){
            F += stepsizes[stepindex];
        }
        if (gamepad2.dpadRightWasPressed()){
            F -= stepsizes[stepindex];
        }
        if (gamepad1.dpadDownWasPressed()){
            P += stepsizes[stepindex];
        }
        if (gamepad1.dpadUpWasPressed()){
            P -= stepsizes[stepindex];
        }
        //set new pidf coefficients
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        launcher.setVelocity(currentTargetSpeed);

        double currentspeed = launcher.getVelocity();
        double error = currentTargetSpeed - currentspeed;

        telemetry.addData("target speed", currentTargetSpeed);
        telemetry.addData("current speed", currentspeed);
        telemetry.addData("error","%.2f", error);
        telemetry.addData("tuning P", "%.4f (D-Pad U/D)", P);
        telemetry.addData("tuning F", "%.4f (D-Pad L/R)", F);
        telemetry.addData("step size", "%.4f (triangle)", stepsizes[stepindex]);




    }
}
