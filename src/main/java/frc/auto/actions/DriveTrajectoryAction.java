package frc.auto.actions;


import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import firelib.auto.actions.IAction;
import frc.subsystems.drivetrain.Drivetrain;

public class DriveTrajectoryAction implements IAction {
    private Trajectory mTrajectory = null;
    private Drivetrain mDrivetrain = Drivetrain.getInstance();
    public DriveTrajectoryAction(Trajectory trajectory) {
        mTrajectory = trajectory;
    }

    @Override
    public void init() {
        mDrivetrain.setTrajectory(mTrajectory);
        mDrivetrain.setTimestamp(Timer.getFPGATimestamp());
        mDrivetrain.setControlType(Drivetrain.ControlType.TRAJECTORY_FOLLOWING);
    }

    @Override
    public void run() {
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFinished() {
        return mDrivetrain.doneWithTrajectory();
    }

}