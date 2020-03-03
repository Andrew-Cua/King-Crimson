package frc.robot;

public class Constants {

    public static final double INTAKE_LOWER_ANGLE = 102000;
    public static final double INTAKE_TO_TURN_ANGLE = 50000;
    public static final double TURRET_TICK_TO_ANGLE = 4096/360;
    public static final double ANGLE_TICK_TO_INCH = 69000.420; //nice
    public static final double SUPERSTRUCTURE_INTAKE_TICKS = 0; //TODO find angles

    public static int turretAngleToTick(double angle) {
        return (int)(11.44*(angle));
    }
}