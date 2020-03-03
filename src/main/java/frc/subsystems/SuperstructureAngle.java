package frc.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import firelib.looper.ILooper;
import firelib.looper.Loop;
import firelib.subsystem.ISubsystem;
import frc.robot.RobotMap;

public class SuperstructureAngle implements ISubsystem {

    public enum ControlType {
        OPEN_LOOP, POSITION_CLOSED_LOOP, VISION_CLOSED_LOOP;
    }

    private TalonSRX mAngleLeft;
    private TalonSRX mAngleRight;
    private PeriodicIO mPeriodicIO = new PeriodicIO();
    private PIDConstants mConstants = new PIDConstants();
    private ControlType mControlType = ControlType.OPEN_LOOP;
    private static SuperstructureAngle instance;

    public static SuperstructureAngle getInstance() {
        if (instance == null) {
            instance = new SuperstructureAngle(new TalonSRX(RobotMap.SHOOTER_ANGLE_LEFT),
                    new TalonSRX(RobotMap.SHOOTER_ANGLE_RIGHT));
        }

        return instance;
    }

    /**
     * ctor -- DO NOT USE -- except for unit testing
     */
    public SuperstructureAngle(TalonSRX angleLeft, TalonSRX angleRight) {
        mAngleLeft = angleLeft;
        mAngleRight = angleRight;

        mAngleRight.follow(mAngleLeft);
        mAngleLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        mAngleRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        mAngleLeft.config_kF(0, 0.08525);
        mAngleLeft.config_kF(0, 0.09234567654);
        mAngleLeft.configMotionCruiseVelocity(7000);
        mAngleLeft.configMotionAcceleration(14000);
        mAngleLeft.configForwardSoftLimitThreshold(524000);
        mAngleLeft.configForwardSoftLimitEnable(true);

        mConstants.kP = ((double) 1 / (double) 16) * 0.85;
    }

    public synchronized void setIO(double demandedPower, double demandedAngle) {
        mPeriodicIO.demandedPower = demandedPower;
        mPeriodicIO.demandedAngle = demandedAngle;
    }

    public synchronized void setControlType(ControlType type) {
        mControlType = type;
    }

    private synchronized void handleOpenLoop() {
        setOpenLoopPower(mPeriodicIO.demandedPower);
    }

    private synchronized void handleClosedLoop() {
        if (mControlType == ControlType.VISION_CLOSED_LOOP) {
            double p_power = -mConstants.kP * mPeriodicIO.camaraAngleOffset;
            setOpenLoopPower(p_power);
        } else if (mControlType == ControlType.POSITION_CLOSED_LOOP) {
            mAngleLeft.set(ControlMode.MotionMagic, mPeriodicIO.demandedAngle);
        }
    }

    private synchronized void setOpenLoopPower(double power) {
        mAngleLeft.set(ControlMode.PercentOutput, power);
    }

    @Override
    public void updateSmartDashboard() {
        // TODO Auto-generated method stub
        SmartDashboard.putNumber("SuperstructureAngle/angle",
                (mAngleLeft.getSelectedSensorPosition() + mAngleRight.getSelectedSensorPosition()) / 2);
        SmartDashboard.putNumber("SuperstructureAngle/LeftSpeed", mAngleLeft.getSelectedSensorVelocity());
        SmartDashboard.putNumber("SuperstructureAngle/RightSpeed", mAngleRight.getSelectedSensorVelocity());

    }

    @Override
    public void pollTelemetry() {
        // TODO Auto-generated method stub
        mPeriodicIO.camaraAngleOffset = SmartDashboard.getNumber("camera/y_offset", 0);

    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        // TODO Auto-generated method stub
        enabledLooper.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLoop(double timestamp) {
                // TODO Auto-generated method stub
                synchronized (SuperstructureAngle.this) {
                    if (mControlType == ControlType.OPEN_LOOP) {
                        handleOpenLoop();
                    } else {
                        handleClosedLoop();
                    }
                }

            }

            @Override
            public void onStop(double timestamp) {
                // TODO Auto-generated method stub

            }

        });

    }

    private class PeriodicIO {
        public double demandedAngle = 0;
        public double camaraAngleOffset = 0;
        public double demandedPower = 0;

        public double currentAngle;
    }

    private class PIDConstants {
        public double kP = 0;
        public double kI = 0;
        public double kD = 0;
        public double currentError = 0;
        public double lastError = 0;
    }

}