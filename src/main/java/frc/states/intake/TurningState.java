package frc.states.intake;

import frc.robot.Constants;

public class TurningState extends IntakeState {

    @Override
    public double intakeAngle() {
        // TODO Auto-generated method stub
        return Constants.INTAKE_TO_TURN_ANGLE;
    }

    @Override
    public double intakeSpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String name() {
        // TODO Auto-generated method stub
        return "Turning";
    }
    
}