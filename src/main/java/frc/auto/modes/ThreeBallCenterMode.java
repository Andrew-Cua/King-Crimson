package frc.auto.modes;

import java.util.Arrays;

import edu.wpi.first.wpilibj.trajectory.Trajectory;
import firelib.auto.actions.ParallelAction;
import firelib.auto.actions.SeriesAction;
import firelib.auto.actions.actionrunners.ActionRunnerBase;
import frc.auto.actions.DriveTrajectoryAction;
import frc.auto.actions.EnableVisionTrackingAction;
import frc.auto.actions.InstantAction;
import frc.auto.actions.ReadyAimModeAction;
import frc.auto.actions.ShootBallsAction;
import frc.auto.actions.interfaces.InstantActionRunner;
import frc.subsystems.Superstructure;

public class ThreeBallCenterMode extends ActionRunnerBase {

    private Trajectory mTraj;
    public ThreeBallCenterMode(Trajectory traj) {
        mTraj = traj;
    }
    @Override
    public void routine() {
        //back up and aim up to get goal in line of sight
        runAction(new ParallelAction(Arrays.asList(new DriveTrajectoryAction(mTraj),new ReadyAimModeAction(0, 250000,2700))));
        runAction(new SeriesAction(Arrays.asList(new EnableVisionTrackingAction(), new ShootBallsAction())));
        //make sure its in defense just to be sure
        runAction(new InstantAction(() -> {Superstructure.getInstance().setTarget(Superstructure.SuperstructureTarget.DEFENSE);}));

    }
    
}