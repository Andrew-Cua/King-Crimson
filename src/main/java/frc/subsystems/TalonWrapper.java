package frc.subsystems;

import frc.subsystems.interfaces.MotorInterface;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

class TalonWrapper implements MotorInterface {
    private TalonSRX talon;
    private double currentSpeed;

    public TalonWrapper(int deviceId) {
        this.setSpeed(0);
        talon = new TalonSRX(deviceId);
    }

    @Override
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    @Override
    public void setSpeed(double speed) {
        currentSpeed = speed;
        talon.set(ControlMode.PercentOutput, currentSpeed);
    }

}