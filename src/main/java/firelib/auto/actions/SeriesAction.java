package firelib.auto.actions;

import java.util.ArrayList;

public class SeriesAction implements IAction {

    private IAction mCurrentAction;
    private ArrayList<IAction> mActions;

    public SeriesAction(ArrayList<IAction> actions) {
        mActions = actions;
    }
    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        if(mCurrentAction == null) {
            if(mActions.isEmpty()) {
                return;
            }
            mCurrentAction = mActions.remove(0);
            mCurrentAction.init();
        }

        mCurrentAction.run();

        if(mCurrentAction.isFinished()) {
            mCurrentAction.stop();
            mCurrentAction = null;
        }

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFinished() {
        // TODO Auto-generated method stub
        return mActions.isEmpty() && mCurrentAction == null;
    }

}