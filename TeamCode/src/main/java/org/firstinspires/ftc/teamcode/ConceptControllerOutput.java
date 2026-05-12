package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="Concept: Controller Output", group ="Concept")
public class ConceptControllerOutput extends LinearOpMode {

    //ElapsedTime runtime = new ElapsedTime();



    @Override
    public void runOpMode() {

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.circleWasPressed()) {
                telemetry.addLine("rumble");
                gamepad1.rumbleBlips(3);
                telemetry.update();
            }
            //green
            if (gamepad1.crossWasPressed()){
                telemetry.addLine("green");
                gamepad1.setLedColor(0, 255, 0, 1000);
                telemetry.update();
            }
        }
    }
}
