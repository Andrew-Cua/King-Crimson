package firelib.auto.actions;

import java.util.ArrayList;
import java.util.List;

public class ParallelAction implements IAction {

    private final ArrayList<IAction> mActions;

    public ParallelAction(List<IAction> actions) {
        mActions = new ArrayList<>(actions);
    }
    @Override
    public void init() {
        // TODO Auto-generated method stub
        for(IAction action : mActions) {
            action.init();
        }

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        for(IAction action : mActions) {
            action.run();
        }

    }

    @Override
    public void stop() {
        for(IAction action : mActions) {
            action.stop();
        }
        
    }

    @Override
    public boolean isFinished() {
        for(IAction action : mActions) {
            if(!action.isFinished()) {
                return false;
            }
        }
        return true;
    }

}