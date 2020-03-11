package frc.auto.modes;

import edu.wpi.first.wpilibj.trajectory.Trajectory;
import firelib.auto.actions.actionrunners.ActionRunnerBase;
import frc.auto.actions.DriveTrajectoryAction;

public class SimpleMode extends ActionRunnerBase {
    private Trajectory mTraj;

    public SimpleMode(Trajectory traj) {
        mTraj = traj;
    }
    @Override
    public void routine() {
        // TODO Auto-generated method stub
        runAction(new DriveTrajectoryAction(mTraj));
    }
    
}