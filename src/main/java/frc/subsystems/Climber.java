package frc.subsystems;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import firelib.looper.ILooper;
import firelib.looper.Loop;
import firelib.subsystem.ISubsystem;
import frc.robot.RobotMap;

public class Climber implements ISubsystem {

    public enum ControlMode {
        OPEN_LOOP, POSITION_CLOSED_LOOP;
    }

    private CANSparkMax mClimber;
    private VictorSPX mSlider;
    private ControlMode mControlMode = ControlMode.OPEN_LOOP;
    private static Climber instance;

    public static Climber getInstance() {
        if (instance == null) {
            instance = new Climber(new CANSparkMax(RobotMap.CLIMB, MotorType.kBrushless),
                    new VictorSPX(RobotMap.CLIMB_SLIDER));
        }
        return instance;
    }

    public Climber(CANSparkMax climber, VictorSPX slider) {
        mClimber = climber;
        mSlider = slider;
    }

    public synchronized void handleOpenLoop() {

    }

    public synchronized void handleClosedLoop() {

    }

    @Override
    public void updateSmartDashboard() {
        // TODO Auto-generated method stub

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
                synchronized (Climber.this) {
                    if (mControlMode == ControlMode.OPEN_LOOP) {
                        handleOpenLoop();
                    } else {
                        handleClosedLoop();
                    }
                }

            }
        });

    }

}