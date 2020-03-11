package frc.auto.actions;

import edu.wpi.first.wpilibj.Timer;
import firelib.auto.actions.IAction;
import frc.subsystems.Indexer;
import frc.subsystems.Superstructure;
import frc.subsystems.Superstructure.SuperstructureTarget;

public class ShootBallsAction implements IAction {
    private Superstructure mSuperstructure = Superstructure.getInstance();
    private Indexer mIndexer = Indexer.getInstance();
    private boolean mEmpty = false;
    private double mEmptyTimeStamp = 0;
    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        mSuperstructure.maybeShootBalls();
        if (mIndexer.getNumBalls() == 0 && !mEmpty) {
            mEmptyTimeStamp = Timer.getFPGATimestamp();
            mEmpty = true;
        }

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        mSuperstructure.setTarget(SuperstructureTarget.DEFENSE);
        mSuperstructure.stopShootingBalls();

    }

    @Override
    public boolean isFinished() {
        // TODO Auto-generated method stub
        return (Timer.getFPGATimestamp() - mEmptyTimeStamp > 0.5 && mEmpty);
    }

}