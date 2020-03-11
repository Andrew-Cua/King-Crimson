package firelib.auto.actions.actionrunners;

import firelib.auto.actions.IAction;

public abstract class ActionRunnerBase {
    protected boolean isActive = false;
    public abstract void routine();
    public void run() {
        isActive = true;
        routine();
    }
    public void stop() {
        isActive = false;
    }
    public void runAction(IAction action) {
        action.init();
        while(!action.isFinished() && isActive) {
            action.run();
        }
        action.stop();
    }

}