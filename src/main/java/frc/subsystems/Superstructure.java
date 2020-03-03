package frc.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import firelib.looper.ILooper;
import firelib.looper.Loop;
import firelib.subsystem.ISubsystem;
import frc.robot.Constants;
import frc.utils.KingMathUtils;

public class Superstructure implements ISubsystem {
    public enum SuperstructureTarget {
        DEFENSE("Defense"), SHOOTING("Shooting"), INTAKING("Intaking"), TURNING("TURNING");

        private String mName;

        SuperstructureTarget(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    public enum TurretHomePosition {
        FRONT(0), BACK(180);

        private int mAngle;

        TurretHomePosition(int angle) {
            mAngle = angle;
        }

        public int getAngle() {
            return mAngle;
        }
    }

    private Turret mTurret = Turret.getInstance();
    private Intake mIntake = Intake.getInstance();
    private Indexer mIndexer = Indexer.getInstance();
    private Shooter mShooter = Shooter.getInstance();
    private SuperstructureAngle mSuperstructureAngle = SuperstructureAngle.getInstance();
    private SuperstructureTarget mCurrentTarget = SuperstructureTarget.DEFENSE;
    private SuperstructureTarget mDesiredTarget = SuperstructureTarget.DEFENSE;

    private TurretHomePosition mHome = TurretHomePosition.FRONT;

    private UserControlValues mVals = new UserControlValues();

    private static Superstructure instance;

    public static Superstructure getInstance() {
        if (instance == null) {
            instance = new Superstructure();
        }
        return instance;
    }

    public synchronized void setTarget(SuperstructureTarget target) {
        mDesiredTarget = target;
    }

    /**
     * setters for values from operator control
     * 
     */
    public synchronized void setTurretAngle(double angle) {
        mVals.turretAngle = angle;
    }

    public synchronized void setSuperstructureAngle(double angle) {
        mVals.superstructureAngle = angle;
    }

    public synchronized void setShooterRPM(double rpm) {
        mVals.shooterRPM = rpm;
    }

    public synchronized void runIntake(boolean run) {
        mVals.runIntake = run;
    }

    public synchronized void enableVision(boolean enableVision) {
        mVals.enableVision = enableVision;
    }

    public synchronized void flipTurret() {
        if (mHome == TurretHomePosition.FRONT) {
            mHome = TurretHomePosition.BACK;
        } else if (mHome == TurretHomePosition.BACK) {
            mHome = TurretHomePosition.FRONT;
        }
    }

    public void handleStateChange() {

        /**
         * command the robot initial state settings
         */
        switch (mDesiredTarget) {
            case DEFENSE:
                mShooter.setState(Shooter.ShooterStates.IDLE);
                mTurret.setDesiredAngle(mHome.getAngle());
                mTurret.setControlType(Turret.ControlType.POSITION_CLOSED_LOOP);
                mIntake.stowIntake();
                mIntake.stopIntake();
                mIndexer.setIO(0, 0);
                break;
            case INTAKING:
                mShooter.setState(Shooter.ShooterStates.IDLE);
                mTurret.setDesiredAngle(mHome.getAngle());
                mTurret.setControlType(Turret.ControlType.POSITION_CLOSED_LOOP);
                mIndexer.setIO(0.7, 0);
                mIndexer.setControlType(Indexer.ControlType.POSITION_CLOSED_LOOP);
                mSuperstructureAngle.setControlType(SuperstructureAngle.ControlType.POSITION_CLOSED_LOOP);
                mSuperstructureAngle.setIO(0, 1000);
                break;
            case SHOOTING:
                mShooter.setState(Shooter.ShooterStates.SPINNING_UP);
                mIntake.stowIntake();
                mIntake.stopIntake();
                mTurret.setDesiredAngle(mHome.getAngle());
                mTurret.setControlType(Turret.ControlType.POSITION_CLOSED_LOOP);
                mIndexer.setIO(0, 0);
                mIndexer.setControlType(Indexer.ControlType.OPEN_LOOP);
                break;
            case TURNING:
                break;

        }

        /**
         * if condiditons are met set current state to desired state
         */

        if (mDesiredTarget == SuperstructureTarget.DEFENSE
                && KingMathUtils.applyDeadband(mTurret.getAngle(), 50, 50, mHome.getAngle()) == mHome.getAngle()
                && mIntake.getState().equals("Defense")) {

            mCurrentTarget = mDesiredTarget;
        }

        if (mDesiredTarget == SuperstructureTarget.INTAKING
                && KingMathUtils.applyDeadband(mTurret.getAngle(), 5, 5, mHome.getAngle()) == mHome.getAngle()) {

            mIntake.lowerIntake();
            mCurrentTarget = mDesiredTarget;

        }

        if (mDesiredTarget == SuperstructureTarget.SHOOTING
                && KingMathUtils.applyDeadband(mTurret.getAngle(), 50, 50, mHome.getAngle()) == mHome.getAngle()
                && mIntake.getState().equals("Defense")) {

            mCurrentTarget = mDesiredTarget;

        }
    }

    @Override
    public void updateSmartDashboard() {
        // TODO Auto-generated method stub
        SmartDashboard.putString("Superstructure/currentTarget", mCurrentTarget.getName());
        SmartDashboard.putString("Superstructure/desiredTarget", mDesiredTarget.getName());

        SmartDashboard.putNumber("Superstructure/homePosition", mHome.getAngle());
        SmartDashboard.putNumber("Superstructure/demandedTurretPositon", mVals.turretAngle);
        SmartDashboard.putBoolean("Superstructure/enableVision", mVals.enableVision);

    }

    @Override
    public void pollTelemetry() {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        // TODO Auto-generated method stub
        enabledLooper.register(new Loop() {

            @Override
            public void onStop(double timestamp) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStart(double timestamp) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Superstructure.this) {
                    if (mCurrentTarget != mDesiredTarget) {
                        handleStateChange();
                    } else if (mCurrentTarget == mDesiredTarget) {

                        /**
                         * shooting logic
                         */
                        if (mCurrentTarget == SuperstructureTarget.SHOOTING) {
                            if (mVals.enableVision) {
                                mTurret.setControlType(Turret.ControlType.VISION_CLOSED_LOOP);
                                mSuperstructureAngle.setControlType(SuperstructureAngle.ControlType.VISION_CLOSED_LOOP);
                            } else {
                                mTurret.setControlType(Turret.ControlType.POSITION_CLOSED_LOOP);
                                mSuperstructureAngle
                                        .setControlType(SuperstructureAngle.ControlType.POSITION_CLOSED_LOOP);
                            }
                            mTurret.setDesiredAngle(mVals.turretAngle);
                            mSuperstructureAngle.setIO(0, mVals.superstructureAngle);
                            mShooter.setIO(0, mVals.shooterRPM);
                            mShooter.setState(Shooter.ShooterStates.SPINNING_UP);
                        }

                        /**
                         * intake logic
                         */
                        if (mCurrentTarget == SuperstructureTarget.INTAKING) {
                            if (mVals.runIntake) {
                                mIntake.runIntake();
                            } else {
                                mIntake.stopIntake();
                            }

                            if (mHome.getAngle() != KingMathUtils.applyDeadband(mTurret.getAngle(), 5, 5,
                                    mHome.getAngle())) {
                                mIntake.liftToTurn();
                                if (mIntake.atState()) {
                                    mTurret.setDesiredAngle(mHome.mAngle);
                                }
                            } else {
                                mIntake.lowerIntake();
                            }

                        }

                        /**
                         * defense logic
                         */
                        if (mCurrentTarget == SuperstructureTarget.DEFENSE) {
                            mIntake.stowIntake();
                            mIntake.stopIntake();
                            mTurret.setDesiredAngle(mHome.getAngle());
                            mTurret.setControlType(Turret.ControlType.POSITION_CLOSED_LOOP);
                            mShooter.stop();
                            mSuperstructureAngle.setIO(0, 0);
                            mSuperstructureAngle.setControlType(SuperstructureAngle.ControlType.POSITION_CLOSED_LOOP);
                            // TODO make sure that superstructure angle is 0
                        }
                    }

                }

            }
        });

    }

    private class UserControlValues {
        public boolean flipHomePosition = false;
        public boolean runIntake = false;
        public boolean enableVision = false;
        public double turretAngle = 0;
        public double superstructureAngle = 0;
        public double shooterRPM = 0;
    }

}