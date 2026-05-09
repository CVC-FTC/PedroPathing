package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(10);   //mass in kg

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(0.75)        //max power
            .rightFrontMotorName("front_right_drive")       //drive motor names in config
            .rightRearMotorName("back_right_drive")
            .leftRearMotorName("back_left_drive")
            .leftFrontMotorName("front_left_drive")
            .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)       //direction of drive motors in teleop code
            .leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE);

    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
            //https://pedropathing.com/docs/pathing/tuning/localization/two-wheel
            .forwardEncoder_HardwareMapName("front_left_drive")
            .strafeEncoder_HardwareMapName("back_right_drive")
            .IMU_HardwareMapName("imu")
            .forwardPodY(5)         //location of forward pod(inches)
            .strafePodX(6.5)          //location of sideways pod(inches)
            .forwardEncoderDirection(Encoder.REVERSE)     //reverse direction of forwards pod
            // and/or:
            .strafeEncoderDirection(Encoder.REVERSE)      //reverse direction of sideways pod
            .forwardTicksToInches(0.00066863)       //forwards tuning
            .strafeTicksToInches(0.0006159)        //sideways tuning
            .IMU_Orientation(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                    )
            );

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .twoWheelLocalizer(localizerConstants)  //two wheel odo
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
