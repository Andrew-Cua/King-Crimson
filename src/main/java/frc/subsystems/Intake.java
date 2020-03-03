package frc.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import firelib.looper.ILooper;
import firelib.looper.Loop;
import firelib.subsystem.StateSubsystem;
import frc.robot.RobotMap;
import frc.states.intake.IntakeState;
import frc.states.intake.LoweredState;
import frc.states.intake.StowedState;
import frc.states.intake.TurningState;
import frc.utils.KingMathUtils;

public class Intake extends StateSubsystem<IntakeState> {

    private TalonSRX mIntakeAngle;
    private VictorSPX mIntake;
    private double mCurrentAngle = 0;
    private static Intake instance;

    public static Intake getInstance() {
        if (instance == null) {
            instance = new Intake(new TalonSRX(RobotMap.INTAKE_ANGLE), new VictorSPX(RobotMap.INTAKE_SPEED));
        }
        return instance;
    }

    /**
     * Ctor - Do not use unless for static builders or unit testing
     * 
     * @param IntakeAngle Talon instance for intake angle
     * @param intake      Victor instance for intake speed
     */
    public Intake(TalonSRX intakeAngle, VictorSPX intake) {
        mIntakeAngle = intakeAngle;
        mIntake = intake;

        mIntakeAngle.setInverted(false);
        mIntakeAngle.setSensorPhase(false);

        mIntakeAngle.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        mIntakeAngle.configMotionCruiseVelocity(25000); // max 800
        mIntakeAngle.configMotionAcceleration(18000);
        mIntakeAngle.config_kF(0, 0.0213125);
        mIntakeAngle.config_kP(0, 0.04);
        mIntakeAngle.config_kD(0, 0);
        mIntakeAngle.config_kI(0, 0);

        addState("Stowed", new StowedState());
        addState("Lowered", new LoweredState());
        addState("Turning", new TurningState());
        mCurrentState = mStates.get("Stowed");
        mDesiredState = mStates.get("Stowed");

    }

    /**
     * interface to run intake at desired speed will not run if intake is stowed
     */
    public synchronized void runIntake() {
        mIntake.set(ControlMode.PercentOutput, mCurrentState.intakeSpeed());
    }

    public synchronized void resetEncoder() {
        mIntakeAngle.setSelectedSensorPosition(0);
    }

    /**
     * stops intake regardless of state
     */
    public synchronized void stopIntake() {
        mIntake.set(ControlMode.PercentOutput, 0);
    }

    public synchronized void lowerIntake() {
        setState("Lowered");
    }

    public synchronized void liftToTurn() {
        setState("Turning");
    }

    public synchronized void stowIntake() {
        setState("Stowed");
    }

    public synchronized void setIntakeAngleSpeed(double spd) {
        mIntakeAngle.set(ControlMode.PercentOutput, spd);
    }

    public synchronized void setIntakeSpeed(double spd) {
        mIntake.set(ControlMode.PercentOutput, spd);
    }

    /**
     * returns the current state of the intake
     * 
     * @return current state
     */
    public synchronized String getState() {
        return mCurrentState.name();
    }

    public synchronized boolean atState() {
        return mCurrentState == mDesiredState;
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putString("Intake/CurrentState", mCurrentState.name());
        SmartDashboard.putString("Intake/DesiredState", mDesiredState.name());
        SmartDashboard.putNumber("Intake/AngleSpeed", mIntakeAngle.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Intake/Angle", mIntakeAngle.getSelectedSensorPosition());
    }

    @Override
    public void pollTelemetry() {
        mCurrentAngle = mIntakeAngle.getSelectedSensorPosition();

    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(new Loop() {

            @Override
            public void onStop(double timestamp) {
                stopIntake();
            }

            @Override
            public void onStart(double timestamp) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLoop(double timestamp) {
                // TODO Auto-generated method stub
                synchronized (Intake.this) {
                    update();
                }

            }
        });

    }

    @Override
    protected void update() {
        if (KingMathUtils.applyDeadband(mCurrentAngle, 3000, 3000, mDesiredState.intakeAngle()) != mDesiredState
                .intakeAngle()) {
            mIntakeAngle.set(ControlMode.MotionMagic, mDesiredState.intakeAngle());
        } else if (KingMathUtils.applyDeadband(mCurrentAngle, 3000, 3000, mDesiredState.intakeAngle()) == mDesiredState
                .intakeAngle()) {
            mCurrentState = mDesiredState;
        }

    }

}