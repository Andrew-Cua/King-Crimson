/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

// HFOV for logitech webcam 70.42
// VFOV for logitech webacm 41.94
package frc.robot;

import java.util.Arrays;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import firelib.auto.AutoModeExecutor;
import firelib.auto.actions.actionrunners.ActionRunnerBase;
import firelib.looper.Looper;
import frc.auto.modes.InitiationMode;
import frc.auto.modes.SimpleMode;
import frc.auto.modes.ThreeBallCenterMode;
import frc.auto.modes.ThreeBallLeft;
import frc.auto.modes.ThreeBallRight;
import frc.controls.ControlBoard;
import frc.subsystems.Indexer;
import frc.subsystems.Intake;
import frc.subsystems.Turret;
import frc.subsystems.Shooter;
import frc.subsystems.Superstructure;
import frc.subsystems.Shooter.ShooterStates;
import frc.subsystems.Superstructure.SuperstructureTarget;
import frc.subsystems.SuperstructureAngle;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.*;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
import frc.subsystems.drivetrain.Drivetrain;
import frc.subsystems.drivetrain.Drivetrain.ControlType;
import frc.trajectories.BackwardsTrajectory;
import frc.trajectories.SimpleTrajectory;
import frc.utils.KingMathUtils;
import frc.utils.MomentarySwitchBoolean;
import frc.utils.ToggleBoolean;

public class Robot extends TimedRobot {
  private Looper mEnabledLooper = new Looper();
  private Looper mDisabledLooper = new Looper();
  private ControlBoard mControls = ControlBoard.getInstance();
  private Drivetrain mDrivetrain = Drivetrain.getInstance();
  private Indexer mIndexer = Indexer.getInstance();
  private Shooter mShooter = Shooter.getInstance();
  private Turret mTurret = Turret.getInstance();
  private Intake mIntake = Intake.getInstance();
  private Superstructure mSuperStructure = Superstructure.getInstance();
  private SuperstructureAngle mSuperstructureAngle = SuperstructureAngle.getInstance();
  private SimpleTrajectory mSimpleTrajectory = new SimpleTrajectory();
  private BackwardsTrajectory mBackwardsTrajectory = new BackwardsTrajectory();
  private final SubsystemManager mSubsystemManager = new SubsystemManager(
      Arrays.asList(mDrivetrain, mTurret, mShooter, mIndexer, mIntake, mSuperstructureAngle, mSuperStructure));
  private ToggleBoolean mToggleIntake = new ToggleBoolean(false, true);
  private ToggleBoolean mToggleVision = new ToggleBoolean(false, true);
  private ToggleBoolean mSwitchSide = new ToggleBoolean(false, true);
  private MomentarySwitchBoolean mFlipTurret = new MomentarySwitchBoolean(false, true);
  private boolean mLooperEnabled = false;
  private Solenoid mLed = new Solenoid(2);

  private AutoModeExecutor mExecutor;
  private ActionRunnerBase mRunner;

  private SendableChooser<ActionRunnerBase> mAuto = new SendableChooser<ActionRunnerBase>(); 
  @Override
  public void robotInit() {
    mSubsystemManager.registerEnabledLoops(mEnabledLooper);
    mSubsystemManager.registerDisabledLoops(mDisabledLooper);
    mSimpleTrajectory.generateTrajectory();
    mBackwardsTrajectory.generateTrajectory();
    mAuto.addOption("Left", new ThreeBallLeft(mBackwardsTrajectory.getTrajectory()));
    mAuto.addOption("Center", new ThreeBallCenterMode(mBackwardsTrajectory.getTrajectory()));
    mAuto.addOption("Right", new ThreeBallRight(mBackwardsTrajectory.getTrajectory()));
    CameraServer.getInstance().startAutomaticCapture();
    

  }

  @Override
  public void disabledPeriodic() {
    mEnabledLooper.stop();
    mDisabledLooper.start();
    mLooperEnabled = false;
    SmartDashboard.putData(mAuto);
  }

  @Override
  public void autonomousInit() {
    if (!mLooperEnabled) {
      mDisabledLooper.stop();
      mEnabledLooper.start();
      mTurret.resetEncoder();
      mIntake.resetEncoder();
      mDrivetrain.resetGyro();
      mDrivetrain.resetEncoders();
      mLooperEnabled = true;
    }
    mRunner = new InitiationMode(mBackwardsTrajectory.getTrajectory());
    mExecutor = new AutoModeExecutor(mRunner);
    if (mExecutor != null) {
      mExecutor.start();
    }
  }

  @Override
  public void autonomousPeriodic() {
    
  }

  @Override
  public void teleopInit() {

    if (!mLooperEnabled) {
      mDisabledLooper.stop();
      mEnabledLooper.start();
      mLooperEnabled = true;
    }

    if (mExecutor != null) {
      mExecutor.stop();
    }

  }

  @Override
  public void teleopPeriodic() {
    competitionControl();
  }

  @Override
  public void testInit() {
    // TODO add logic
    mSuperstructureAngle.resetEncoder();
  }

  @Override
  public void testPeriodic() {
    // TODO add logic
  }

  private void competitionControl() {
    double throttle = KingMathUtils.clampD(mControls.getYThrottle(), 0.075);
    double rot = KingMathUtils.clampD(mControls.getXThrottle(), 0.075);
    boolean wantsAimMode = mControls.getAimMode();
    boolean wantsIntakeMode = mControls.getIntakeMode();
    boolean wantsDefenseMode = mControls.getDefenseMode();
    boolean wantsTurbo = mControls.getTurboButton();

    boolean maybeEnableVision = mControls.maybeEnableVision();
    boolean maybeFlipTurret = mControls.maybeFlipTurret();
    boolean maybeRunIntake = mControls.maybeRunIntake();
    boolean maybeShootBall = mControls.maybeShootBall();
    boolean maybeUnjamShooter = mControls.maybeUnjamShooter();

    int turretAngle = mControls.getTurretAngle();
    int superstructureAngle = mControls.getSuperstructureAngle();
    int intakeAngle = mControls.getIntakeAngle();

    mFlipTurret.update(maybeFlipTurret);
    mToggleVision.update(maybeEnableVision);

    if (wantsAimMode) {
      mSuperStructure.setTarget(Superstructure.SuperstructureTarget.SHOOTING);
      mSuperStructure.setShooterRPM(3000 * 3);
    } else if (wantsIntakeMode) {
      mSuperStructure.setTarget(Superstructure.SuperstructureTarget.INTAKING);
      mSuperStructure.setShooterRPM(0);
    } else if (wantsDefenseMode) {
      mSuperStructure.setTarget(Superstructure.SuperstructureTarget.DEFENSE);
      mSuperStructure.setShooterRPM(0);
    } else {
      mSuperStructure.setTarget(Superstructure.SuperstructureTarget.DEFENSE);
    }

    if (mFlipTurret.getCurrentState() && !wantsDefenseMode) {
      mSuperStructure.flipTurret();
    }

    if (mToggleVision.getCurrentState() && wantsAimMode) {
      mSuperStructure.enableVision(mToggleVision.getCurrentState());
      mLed.set(true);
    } else {
      mSuperStructure.enableVision(false);
      mLed.set(false);
    }

    if (wantsAimMode && !mToggleVision.getCurrentState()) {
      mSuperStructure.setSuperstructureAngle(superstructureAngle);
      mSuperStructure.setTurretAngle(turretAngle);
    }

    if (wantsAimMode && maybeShootBall) {
      mSuperStructure.maybeShootBalls();
    } else if (wantsAimMode && !maybeShootBall) {
      mSuperStructure.stopShootingBalls();
    }

    if (wantsIntakeMode && maybeRunIntake) {
      mSuperStructure.runIntake(maybeRunIntake);
    } else {
      mSuperStructure.runIntake(false);
    }

    if (wantsIntakeMode) {
      mSuperStructure.setIntakeAngle(intakeAngle);
      System.out.println(intakeAngle);
    }

    if (maybeUnjamShooter) {
      mIndexer.decrementPos();
    }
    if (wantsTurbo) {
      mDrivetrain.setIO(KingMathUtils.logit(-throttle), -KingMathUtils.turnExp(rot * 0.7));
    } else {
      mDrivetrain.setIO(KingMathUtils.logit(-throttle * 0.7), -KingMathUtils.turnExp(rot * 0.5));
    }
  }

  private void testControl() {
    double throttle = KingMathUtils.clampD(mControls.getYThrottle(), 0.075);
    double rot = KingMathUtils.clampD(mControls.getXThrottle(), 0.075);

    boolean wantsShot = mControls.getShoot();

    boolean wanstToTurnTurretLeft = mControls.getTurnTurretLeft();
    boolean wantsToTurnTurretRight = mControls.getTurnTurretRight();

    boolean wantsToTrackTarget = mControls.enableVisionTracking();
    boolean runIntake = mControls.runIntake();
    boolean raiseSuperstructure = mControls.raiseAngle();
    boolean lowerSuperstructure = mControls.lowerAngle();
    boolean moveIndex = mControls.pulseIndex();
    boolean intakeMode = mControls.intakeMode();
    mToggleIntake.update(mControls.toggleIntake());
    mToggleVision.update(mControls.enableVisionTracking());
    mSwitchSide.update(mControls.getAButton());

    if (wantsShot) {
      mShooter.setIO(0.7, 3100 * 3);
      mShooter.setState(Shooter.ShooterStates.SPINNING_UP);
      if (mShooter.atSpeed()) {
        mIndexer.setIO(1, 0.65);
      } else {
        mIndexer.setIO(0, 0);
      }
    } else {
      mShooter.setIO(0, 0);
      mShooter.setState(Shooter.ShooterStates.IDLE);
      mIndexer.setIO(0, -mControls.getRightY());
      mDrivetrain.setIO(KingMathUtils.logit(-throttle * 0.7), -KingMathUtils.turnExp(rot * 0.5));
    }

    // if (wanstToTurnTurretLeft) {
    // mTurret.setOpenloopPower(-0.065);
    // mTurret.setControlType(Turret.ControlType.OPEN_LOOP);
    // } else if (wantsToTurnTurretRight) {
    // mTurret.setOpenloopPower(0.065);
    // mTurret.setControlType(Turret.ControlType.OPEN_LOOP);
    // } else {
    // mTurret.setOpenloopPower(0);
    // }

    if (mSwitchSide.getCurrentState()) {
      mTurret.setControlType(Turret.ControlType.POSITION_CLOSED_LOOP);
      mTurret.setDesiredAngle(180);
    } else {
      mTurret.setControlType(Turret.ControlType.POSITION_CLOSED_LOOP);
      mTurret.setDesiredAngle(0);
    }

    if (mToggleIntake.getCurrentState()) {
      mIntake.lowerIntake();
    } else {
      mIntake.stowIntake();
    }

    if (runIntake) {
      mIntake.setIntakeSpeed(1);
      mIndexer.setIO(0.7, 0.3);
    } else {
      mIntake.stopIntake();
    }

    if (mToggleVision.getCurrentState()) {
      mTurret.setControlType(Turret.ControlType.VISION_CLOSED_LOOP);
      mSuperstructureAngle.setControlType(SuperstructureAngle.ControlType.VISION_CLOSED_LOOP);
      mLed.set(true);
    } else {
      mTurret.setControlType(Turret.ControlType.POSITION_CLOSED_LOOP);
      mSuperstructureAngle.setControlType(SuperstructureAngle.ControlType.OPEN_LOOP);
      mLed.set(false);
    }

    if (intakeMode) {
      mSuperstructureAngle.setControlType(SuperstructureAngle.ControlType.POSITION_CLOSED_LOOP);
      mSuperstructureAngle.setIO(0, 390000);
    } else {
      if (raiseSuperstructure) {
        mSuperstructureAngle.setControlType(SuperstructureAngle.ControlType.OPEN_LOOP);
        mSuperstructureAngle.setIO(0.5, 0);
      } else if (lowerSuperstructure) {
        mSuperstructureAngle.setIO(-0.5, 0);
      } else {
        mSuperstructureAngle.setIO(0, 0);
      }
    }

    if (moveIndex) {
      mIndexer.incrementPos();
      System.out.println(moveIndex);

    }

    SmartDashboard.putNumber("Joystick", mControls.getBoardX());
    mDrivetrain.setIO(KingMathUtils.logit(-throttle * 0.7), -KingMathUtils.turnExp(rot * 0.5));

  }

}
