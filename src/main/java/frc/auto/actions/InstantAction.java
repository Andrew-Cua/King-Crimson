package frc.auto.actions;

import firelib.auto.actions.IAction;
import frc.auto.actions.interfaces.InstantActionRunner;

public class InstantAction implements IAction {

    private InstantActionRunner mInstantRunner;
    public InstantAction(InstantActionRunner instantRunner) {
        mInstantRunner = instantRunner;
    }
    @Override
    public void init() {
        // TODO Auto-generated method stub
        mInstantRunner.execute();

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