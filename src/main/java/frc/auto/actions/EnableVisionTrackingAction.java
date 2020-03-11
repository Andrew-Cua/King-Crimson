package frc.auto.actions;

import firelib.auto.actions.IAction;
import frc.subsystems.Superstructure;

public class EnableVisionTrackingAction implements IAction {
    private Superstructure mSuperstructure = Superstructure.getInstance();
    @Override
    public void init() {
        // TODO Auto-generated method stub
        mSuperstructure.enableVision(true);
        

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFinished() {
        // TODO Auto-generated method stub
        return true;
    }

}