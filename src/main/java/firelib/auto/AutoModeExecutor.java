package firelib.auto;

import firelib.auto.actions.actionrunners.ActionRunnerBase;

public class AutoModeExecutor {
    private ActionRunnerBase mRunner;
    private boolean mRunning = false;

    public AutoModeExecutor(ActionRunnerBase runner) {
        mRunner = runner;
    }

    public void start() {
        mRunning = true;
    }

    public void run() {
        if (mRunning) {
            if (mRunner != null) {
                mRunner.run();
            }
        }
    }

    public void stop() {
        mRunning = false;
        mRunner = null;
    }
}