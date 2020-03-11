package frc.utils;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {
    private static NetworkTable mLimelightData = NetworkTableInstance.getDefault().getTable("limelight");
    private double  mAngleY = 0;
    private double  mAngleX = 0;
    private boolean mHasTarget = false;
    private double  mRange  = 0;
    private static Limelight instance;

    public static Limelight getInstance() {
        if (instance == null) {
            instance = new Limelight();
        }

        return instance;
    }


    public Limelight() {

    }

    public void update() {
        mAngleY    = mLimelightData.getEntry("ty").getDouble(0.0);
        mAngleX    = mLimelightData.getEntry("tx").getDouble(0.0);
        mHasTarget = (mLimelightData.getEntry("tv").getDouble(0.0) == 0); 
    }

    public double getVerticleAngleError() {
        return mAngleY;
    }

    public double getHorizontalAngleError() {
        return mAngleX;
    }

    public boolean getHasTarget() {
        return mHasTarget;
    }
}