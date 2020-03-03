package frc.controls;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class ControlBoard implements IButtonControlBoard, IJoystickControlBoard {
    private XboxController mDriveController;
    private Joystick mBoard;
    private static ControlBoard mInstance = new ControlBoard();

    private ControlBoard() {
        mDriveController = new XboxController(0);
        mBoard = new Joystick(1);
    }

    
   

    @Override
    public double getYThrottle() {
        return mDriveController.getY(Hand.kLeft);

    }

    @Override
    public double getXThrottle() {
        return mDriveController.getX(Hand.kRight);

    }

    public double getRightY() {
        return mDriveController.getY(Hand.kRight);
    }

    @Override
    public boolean getShoot() {
        return mDriveController.getBumper(Hand.kLeft);
    }
 
    public static ControlBoard getInstance() {
        return mInstance;
    }

    @Override
    public boolean getTurnTurretLeft() {
        return (mDriveController.getPOV() == 90);
    }

    @Override
    public boolean getTurnTurretRight() {
        return (mDriveController.getPOV() == 270);
    }

    public boolean raiseAngle() {
        return (mDriveController.getPOV() == 0);
    }

    public boolean lowerAngle() {
        return (mDriveController.getPOV() == 180);
    }

    public int getPOV(){
        return mDriveController.getPOV();
    }


    @Override
    public boolean runIntake() {
        // TODO Auto-generated method stub
        return mDriveController.getBumper(Hand.kRight);
    }

    @Override
    public boolean enableMusic() {
        return mDriveController.getAButtonPressed();
    }

    @Override
    public boolean enableVisionTracking() {
        return mDriveController.getBButton();
    }

    @Override
    public boolean toggleIntake() {
        return mDriveController.getXButton();
    }

    public double getBoardX() {
        return mBoard.getX();
    }

    public boolean getYButton() {
        return mDriveController.getYButton();
    }

    public boolean getAButton() {
        return mDriveController.getAButton();
    }

    public boolean pulseIndex() {
        return mBoard.getRawButtonPressed(3);
    }

    public boolean intakeMode() {
        return mBoard.getRawButton(7);
    }

    @Override
    public boolean getAimMode() {
        // TODO Auto-generated method stub
        return !mBoard.getRawButton(8) && !mBoard.getRawButton(7);
    }

    @Override
    public boolean getIntakeMode() {
        // TODO Auto-generated method stub
        return mBoard.getRawButton(7);
    }

    @Override
    public boolean getDefenseMode() {
        // TODO Auto-generated method stub
        return mBoard.getRawButton(8);
    }

    @Override
    public boolean maybeEnableVision() {
        // TODO Auto-generated method stub
        return mBoard.getRawButton(15);
    }

    @Override
    public boolean maybeRunIntake() {
        // TODO Auto-generated method stub
        return mBoard.getRawButton(11);
    }

    @Override
    public int getTurretAngle() {
        // TODO Auto-generated method stub
        return (int)mBoard.getX()*180;
    }

    @Override
    public int getSuperstructureAngle() {
        // TODO Auto-generated method stub
        return (int)(Math.abs(mBoard.getY())*50000);
    }

    @Override
    public boolean maybeShootBall() {
        // TODO Auto-generated method stub
        return mBoard.getRawButton(11);
    }

    @Override
    public boolean maybeFlipTurret() {
        // TODO Auto-generated method stub
        return !mBoard.getRawButton(1);
    }
}
