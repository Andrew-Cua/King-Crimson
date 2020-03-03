package frc.subsystems;
/*DEPRECATED EVERYTHING (We might need import stuff)


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
// DEPRACATED BUT STILL MAYBE USABLE

//import com.revrobotics.CANSparkMax;
//import com.revrobotics.CANSparkMaxLowLevel;
//import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.util.Color;

class ControlPanel{
    // DEPRECATED BUT STIlL MAYBE USABLE: private CANSparkMax lift = new CANSparkMax(RobotMap.LIFT, MotorType.kBrushless);
    private TalonSRX rotatorforthecolorwheel=new TalonSRX(RobotMap.CONTROLPANEL_ROTATOR);
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
    // Use Color color = m_colorSensor.getColor() for color output,
    // and use double IRvalue = m_colorSensor.getIR() for IR output.
    // This seems fun!
    // Also, you probably have the URL of an awesome github example ingrained in the computers hard drive. (in a text file.)
    // Just check the file in "/" that you can't mess with.
    ControlPanel(){
        rotatorforthecolorwheel.set(ControlMode.Position,0.5);
    }
}*/

import frc.subsystems.GoalFlow;

public class ControlPanel extends GoalFlow {
    public ControlPanel() {
        super();

    }

    public void runGoal(Object... args) {

    }
}