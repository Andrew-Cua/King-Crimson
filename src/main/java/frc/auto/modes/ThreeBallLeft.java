package frc.auto.modes;

import java.util.Arrays;

import edu.wpi.first.wpilibj.trajectory.Trajectory;
import firelib.auto.actions.ParallelAction;
import firelib.auto.actions.SeriesAction;
import firelib.auto.actions.actionrunners.ActionRunnerBase;
import frc.auto.actions.DriveTrajectoryAction;
import frc.auto.actions.EnableVisionTrackingAction;
import frc.auto.actions.ReadyAimModeAction;
import frc.auto.actions.ShootBallsAction;

public class ThreeBallLeft extends ActionRunnerBase {

    private Trajectory mTraj;
    public ThreeBallLeft(Trajectory traj) {
        mTraj = traj;
    }
    @Override
    public void routine() {
        //back up and aim up to get goal in line of sight
        runAction(new ParallelAction(Arrays.asList(new DriveTrajectoryAction(mTraj),new ReadyAimModeAction(-15, 250000,3000))));
        runAction(new SeriesAction(Arrays.asList(new EnableVisionTrackingAction(), new ShootBallsAction())));

    }
    
}