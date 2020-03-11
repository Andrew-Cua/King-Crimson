package frc.states.intake;

import frc.robot.Constants;

public class LoweredState extends IntakeState {

    @Override
    public double intakeAngle() {
        return Constants.INTAKE_LOWER_ANGLE;
    }

    @Override
    public double intakeSpeed() {
        return 1;
    }

    @Override
    public String name() {
        return "Intaking";
    }

}