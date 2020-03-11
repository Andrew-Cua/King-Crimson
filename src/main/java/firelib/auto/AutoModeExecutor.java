package firelib.auto;

import firelib.auto.actions.actionrunners.ActionRunnerBase;

public class AutoModeExecutor {
    private ActionRunnerBase mRunner;
    private boolean mRunning = false;
    private Thread mThread = null;
    public AutoModeExecutor(ActionRunnerBase runner) {
        mRunner = runner;
        mThread = new Thread(new Runnable(){
        
            @Override
            public void run() {
                if (mRunner != null) {
                    mRunner.run();
                }
            }
        });
    }

    public void start() {
        mRunning = true;
        if (mThread != null) {
            mThread.start();
        }
    }

    public void stop() {
        mRunning = false;
        if (mRunner != null) {
            mRunner.stop();
        }

        mThread = null;
    }
}