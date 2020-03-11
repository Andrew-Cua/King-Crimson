
package frc.controls;

public interface IButtonControlBoard {
    //test control methods
    public boolean getShoot();
    public boolean getTurnTurretLeft();
    public boolean getTurnTurretRight();
    public boolean enableVisionTracking();
    public boolean enableMusic();
    public boolean toggleIntake();
    public boolean runIntake();

    //competion control methods
    public boolean getAimMode();
    public boolean getIntakeMode();
    public boolean getDefenseMode();
    public boolean getTurboButton();

    public boolean maybeEnableVision();
    public boolean maybeRunIntake();
    public boolean maybeShootBall();
    public boolean maybeFlipTurret();

    public int getTurretAngle();
    public int getSuperstructureAngle();
}
