package frc.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import firelib.looper.ILooper;
import firelib.looper.Loop;
import firelib.subsystem.ISubsystem;
import frc.robot.RobotMap;
import frc.utils.MomentarySwitchBoolean;

public class Indexer implements ISubsystem {
    public enum ControlType {
        OPEN_LOOP, POSITION_CLOSED_LOOP, VELOCITY_CLOSED_LOOP
    }

    public enum Mode {
        INTAKEING, SHOOTING;
    }

    private TalonSRX mRightBelt;
    private TalonSRX mLeftBelt;
    private VictorSPX mPreBelt;
    private ControlType mControlType = ControlType.OPEN_LOOP;
    private Mode mMode = Mode.INTAKEING;
    private PeriodicIO mPeriodicIO = new PeriodicIO();
    private static Indexer instance;
    private DigitalInput mBeamBreakOne = new DigitalInput(0);
    private DigitalInput mBeamBreakTwo = new DigitalInput(1);
    private MomentarySwitchBoolean mBallEntered = new MomentarySwitchBoolean(false, false);
    private MomentarySwitchBoolean mBallExited = new MomentarySwitchBoolean(false, false);

    /**
     * singleton method for use throughout robot
     * 
     * @return Indexer instance
     */
    public static Indexer getInstance() {
        if (instance == null) {
            instance = new Indexer(new TalonSRX(RobotMap.INDEX_LEFT), new VictorSPX(RobotMap.PREBELT));
        }

        return instance;
    }

    /**
     * ctor - not for use unless for static builders or unit testing
     * 
     * @param rightBelt the motor controlling the right belt
     * @param leftBelt  the motor controlling the left belt
     */
    public Indexer(TalonSRX leftBelt, VictorSPX preBelt) {
        mPreBelt = preBelt;
        mLeftBelt = leftBelt;

        mLeftBelt.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        mLeftBelt.config_kF(0, 0.0935);
        mLeftBelt.config_kP(0, 0.4);

        mLeftBelt.configMotionCruiseVelocity(8500);
        mLeftBelt.configMotionAcceleration(16000);

    }

    public synchronized void setControlType(ControlType type) {
        mControlType = type;
    }

    public synchronized void setMode(Mode mode) {
        mMode = mode;
    }

    public synchronized void setPos(int ticks) {
        mLeftBelt.set(ControlMode.MotionMagic, ticks);
    }

    public synchronized void setIO(double preBeltPower, double beltPower) {
        mPeriodicIO.preBeltPower = preBeltPower;
        mPeriodicIO.beltPower = beltPower;
    }

    public synchronized int getNumBalls() {
        return mPeriodicIO.ballsInTube;
    }

    public synchronized void setBallsInTube(int balls) {
        mPeriodicIO.ballsInTube = balls;
    }

    /**
     * incrememnts the position of the belts of indexer by 8000 ticks or 4 inches
     */
    public synchronized void incrementPos() {
        mPeriodicIO.desiredPositon += 6000;

    }

    /**
     * decrements the position of the belts of indexer by 2000 ticks or 1 inche
     */
    public synchronized void decrementPos() {
        mPeriodicIO.desiredPositon -= 3000;
    }

    /**
     * moves motors on the belts to a certain position or velocity
     */
    public synchronized void handleClosedLoop() {
        setPos(mPeriodicIO.desiredPositon);
    }

    /**
     * sets the motors to a percentage of voltage
     */
    public synchronized void handleOpenLoop() {
        mLeftBelt.set(ControlMode.PercentOutput, mPeriodicIO.beltPower);

    }

    public synchronized void resetEncoder() {
        mLeftBelt.setSelectedSensorPosition(0);
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putNumber("Indexer/Speed", mLeftBelt.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Indexer/Position", mLeftBelt.getSelectedSensorPosition());
        SmartDashboard.putNumber("Indexer/DesiredPosition", mPeriodicIO.desiredPositon);
        SmartDashboard.putBoolean("Indexer/BallEntered", mBeamBreakOne.get());
        SmartDashboard.putBoolean("Indexer/BallExited", mBeamBreakTwo.get());

        SmartDashboard.putNumber("Indexer/BallsInTube", mPeriodicIO.ballsInTube);
    }

    @Override
    public void pollTelemetry() {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
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
                // TODO Auto-generated method stub
                synchronized (Indexer.this) {
                    if (mMode == Mode.INTAKEING) {
                        mControlType = ControlType.POSITION_CLOSED_LOOP;
                    } else {
                        mControlType = ControlType.OPEN_LOOP;
                    }

                    boolean shooterBroken = mBeamBreakTwo.get();
                    mBallEntered.update(mBeamBreakOne.get());
                    mBallExited.update(shooterBroken);

                    if (mBallEntered.getCurrentState()) {
                        if (mMode == Mode.INTAKEING) {
                            mPeriodicIO.ballsInTube++;
                            if (mPeriodicIO.ballsInTube > 3) {
                                mPeriodicIO.ballsInTube = 3;
                            }
                            incrementPos();
                        } else if (mMode == Mode.SHOOTING) {
                            // this shouldn't trigger when in shooting mode
                        }
                    }

                    if (mBallExited.getCurrentState()) {
                        if (mMode == Mode.INTAKEING) {
                            decrementPos();
                        } else if (mMode == Mode.SHOOTING) {
                            mPeriodicIO.ballsInTube--;
                            if (mPeriodicIO.ballsInTube < 0) {
                                mPeriodicIO.ballsInTube = 0;
                            }
                        }
                    }

                    if (mPeriodicIO.ballsInTube == 0 && mLeftBelt.getSelectedSensorPosition() != 0 & mMode == Mode.SHOOTING) {
                        mPeriodicIO.desiredPositon = 0;
                        resetEncoder();
                        
                    }

                    if (mControlType == ControlType.OPEN_LOOP) {
                        handleOpenLoop();
                    } else {
                        handleClosedLoop();
                    }
                    mPreBelt.set(ControlMode.PercentOutput, mPeriodicIO.preBeltPower);
                }
            }
        });

    }

    private class PeriodicIO {
        public double preBeltPower = 0;
        public double beltPower = 0;
        public int currentPosition = 0;
        public int desiredPositon = 0;
        public int ballsInTube = 0;

    }

}