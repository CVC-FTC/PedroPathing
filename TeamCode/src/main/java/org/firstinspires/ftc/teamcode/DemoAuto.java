package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


@Autonomous
public class DemoAuto extends OpMode{

    private Follower follower;
    private Timer pathtimer, opmodetimer;

    public enum pathstate{
        drive_to_shoot,
        shoot
    }

    pathstate pathstate;

    private final Pose startpose = new Pose(20,122,Math.toRadians(138));

    private final Pose shootpose = new Pose(46.415,96.9,Math.toRadians(138+180));

    private PathChain drivestartshoot;


    public void buildPaths(){

    }

    @Override
    public void init(){

    }

    @Override
    public void loop(){

    }

}
