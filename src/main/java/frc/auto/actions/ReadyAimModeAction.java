package frc.auto.actions;

import firelib.auto.actions.IAction;
import frc.subsystems.Superstructure;
import frc.subsystems.SuperstructureAngle;
import frc.subsystems.Turret;
import frc.utils.KingMathUtils;

public class ReadyAimModeAction implements IAction {

    private int mTurretAngle;
    private int mSuperstructureAngle;
    private int mShooterRPM;
    private Superstructure mSuperstructure = Superstructure.getInstance();
    private Turret mTurret = Turret.getInstance();
    private SuperstructureAngle mSSAngle = SuperstructureAngle.getInstance();
    public ReadyAimModeAction(int turretAngle, int superAngle, int RPM){
        mTurretAngle = turretAngle;
        mSuperstructureAngle = superAngle;
        mShooterRPM = RPM;
    }
    @Override
    public void init() {
        // TODO Auto-generated method stub
        mSuperstructure.setTarget(Superstructure.SuperstructureTarget.SHOOTING);
        mSuperstructure.setSuperstructureAngle(mSuperstructureAngle);
        mSuperstructure.setTurretAngle(mTurretAngle);
        mSuperstructure.setShooterRPM(mShooterRPM*3);

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        mSuperstructure.setSuperstructureAngle(mSuperstructureAngle);
        mSuperstructure.setTurretAngle(mTurretAngle);
        

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFinished() {
        // TODO Auto-generated method stub
        return KingMathUtils.applyDeadband(mTurret.getAngle(), 5, 5, mTurretAngle) == mTurretAngle &&
               KingMathUtils.applyDeadband(mSSAngle.getAngle(), 2000, 2000, mSuperstructureAngle) == mSuperstructureAngle;
    }
    
}