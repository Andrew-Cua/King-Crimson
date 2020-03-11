package frc.auto.modes;

import edu.wpi.first.wpilibj.trajectory.Trajectory;
import firelib.auto.actions.actionrunners.ActionRunnerBase;
import frc.auto.actions.DriveTrajectoryAction;

public class InitiationMode extends ActionRunnerBase {

    private Trajectory mTraj;

    public InitiationMode(Trajectory traj) {
        mTraj = traj;
    }
    @Override
    public void routine() {
        runAction(new DriveTrajectoryAction(mTraj));

    }

}