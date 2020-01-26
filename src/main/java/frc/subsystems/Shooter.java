package frc.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import firelib.looper.ILooper;
import firelib.looper.Loop;
import firelib.subsystem.ISubsystem;
import frc.robot.RobotMap;

public class Shooter implements ISubsystem {
    public enum ShooterStates {
        IDLE,
        SPINNING_UP,
        MAINTAIN_SPEED,
        OPEN_LOOP;
    }
    private PeriodicIO mPeriodicIO = new PeriodicIO();


    private ShooterStates mCurrentState = ShooterStates.IDLE;
    private ShooterStates mDesiredState = ShooterStates.IDLE;

    private CANSparkMax mShooterLeft;
    private CANSparkMax mShooterRight;
    private CANPIDController mLeftPID;
    private CANPIDController mRightPID;
    private CANEncoder mLeftEncoder;
    private CANEncoder mRightEncoder;

    private static Shooter instance;
    public static Shooter getInstance() {
        if (instance == null) {
            instance = new Shooter(new CANSparkMax(RobotMap.SHOOTER_LEFT, MotorType.kBrushless), new CANSparkMax(RobotMap.SHOOTER_RIGHT, MotorType.kBrushless));
        }
        return instance;
    }
    public Shooter(CANSparkMax shooterLeft, CANSparkMax shooterRight) {
        mShooterLeft  = shooterLeft;
        mShooterRight = shooterRight;

        mShooterLeft.enableVoltageCompensation(12);
        mShooterRight.enableVoltageCompensation(12);

        mShooterLeft.setSmartCurrentLimit(40);
        mShooterRight.setSmartCurrentLimit(40);

        mLeftPID  = mShooterLeft.getPIDController();
        mRightPID = mShooterRight.getPIDController();

        mLeftEncoder  = mShooterLeft.getEncoder();
        mRightEncoder = mShooterRight.getEncoder();

        mLeftPID.setP(0.0015);
        mRightPID.setP(0.002);
        mLeftPID.setP(0.00015);
        mRightPID.setP(0.0002);
        mLeftPID.setFF(0.00009804);
        mRightPID.setFF(0.0001);

        mShooterLeft.setInverted(true);
    }

    public synchronized void setState(ShooterStates state) {
        mDesiredState = state;
    }

    /**
     * interface to set the motors to a certain percent
     * @param percent percent of voltage to set the motors to (-1,1)
     */
    private void set(double percent) {
        mLeftPID.setReference(percent, ControlType.kDutyCycle);
        mRightPID.setReference(-percent, ControlType.kDutyCycle);
    }

    /**
     * interface to set the motors to a certain rpm
     * @param rpm rotations per minute
     */
    private void setRPM(double rpm) {
        mLeftPID.setReference(rpm, ControlType.kVelocity);
        mRightPID.setReference(rpm, ControlType.kVelocity);
    }

    public void stop() {
        mShooterLeft.set(0);
        mShooterRight.set(0);
    }

    /**
     * set the demanded inputs of the shooter
     * @param demandedPercent voltage percent to set to
     * @param demandedRPM Rotations Per Minute to set to
     */
    public synchronized void setIO(double demandedPercent, double demandedRPM) {
        mPeriodicIO.mDemandedPercent = demandedPercent;
        mPeriodicIO.mDemandedRPM     = demandedRPM;
    }


    /**
     * handles close loop control and state changes between spinning up and holding speed
     */
    private void handleCloseLoop() {
        if (Math.abs(mPeriodicIO.mCurrentLeftSpd) < mPeriodicIO.mDemandedRPM + 100) {
            mCurrentState = ShooterStates.SPINNING_UP;
        } else if (Math.abs(mPeriodicIO.mCurrentLeftSpd) > mPeriodicIO.mDemandedRPM - 100 && Math.abs(mPeriodicIO.mCurrentLeftSpd) < mPeriodicIO.mDemandedPercent + 100) {
            mCurrentState = ShooterStates.MAINTAIN_SPEED;
        }

        if (mCurrentState == ShooterStates.SPINNING_UP) {
            setRPM(mPeriodicIO.mDemandedRPM);
        }

        if (mCurrentState == ShooterStates.MAINTAIN_SPEED) {
            // add something here if the PID isn't good enough, but for now just use setRPM
            setRPM(mPeriodicIO.mDemandedRPM);
        }

    }

    /**
     * handles driving the shooter in openloop control
     */
    private void handleOpenLoop() {
        set(mPeriodicIO.mDemandedPercent);
        mCurrentState = ShooterStates.OPEN_LOOP;
    }
    

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putNumber("Shooter Left RPM", (mPeriodicIO.mCurrentLeftSpd/4096)*600);
        SmartDashboard.putNumber("Shooter Right RPM", (mPeriodicIO.mCurrentRightSpd/4096)*600);
        SmartDashboard.putNumber("Motor Left RPM", mPeriodicIO.mCurrentLeftSpd);
        SmartDashboard.putNumber("Motor Right RPM", mPeriodicIO.mCurrentRightSpd);
        SmartDashboard.putNumber("Shooter Demanded RPM", mPeriodicIO.mDemandedRPM);

    }

    @Override
    public void pollTelemetry() {
        mPeriodicIO.mCurrentLeftSpd  = mLeftEncoder.getVelocity();
        mPeriodicIO.mCurrentRightSpd = mRightEncoder.getVelocity();

    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(new Loop(){
        
            @Override
            public void onStop(double timestamp) {
                set(0);
                
            }
        
            @Override
            public void onStart(double timestamp) {
                set(0);
                
            }
        
            @Override
            public void onLoop(double timestamp) {
                synchronized(Shooter.this) {
                    if(mDesiredState != ShooterStates.IDLE) {
                        handleCloseLoop();
                    } else if(mDesiredState == ShooterStates.OPEN_LOOP) {
                        handleOpenLoop();
                    } else {
                        stop();
                    }
    
                    if(mPeriodicIO.mCurrentLeftSpd == 0 && mPeriodicIO.mCurrentRightSpd == 0) {
                        mCurrentState = ShooterStates.IDLE;
                    }

                }
                

            }
        });

    }

    


    private class PeriodicIO {
        public double mDemandedPercent = 0;
        public double mDemandedRPM = 0;

        public double mCurrentLeftSpd = 0;
        public double mCurrentRightSpd = 0;
    }
    
}