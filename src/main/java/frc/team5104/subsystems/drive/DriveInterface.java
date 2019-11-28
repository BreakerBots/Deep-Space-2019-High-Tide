/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.subsystems.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import frc.team5104.subsystems.drive.DriveConstants.DriveEncoders;
import frc.team5104.subsystems.drive.DriveConstants.DriveUnits;

public class DriveInterface {

	//Devices
	private static TalonSRX talonL1, talonL2, talonR1, talonR2;
	private static PigeonIMU gyro;
	
	//Internal Functions
	static void set(double leftSpeed, double rightSpeed, ControlMode controlMode, double feedForward) {
		talonL1.set(controlMode, leftSpeed, DemandType.ArbitraryFeedForward, feedForward);
		talonR1.set(controlMode, rightSpeed, DemandType.ArbitraryFeedForward, feedForward);
	}
	static void stop() {
		talonL1.set(ControlMode.Disabled, 0);
		talonR1.set(ControlMode.Disabled, 0);
	}
	static void shift(boolean toHigh) { /*shifter.set(toHigh ? Value.kForward : Value.kReverse);*/ }
	static boolean getShift() { return true;/*shifter.get() == Value.kForward;*/ }
	
	//External Functions
	public static double getLeftGearboxVoltage() { return talonL1.getBusVoltage(); }
	public static double getRightGearboxVoltage() { return talonR1.getBusVoltage(); }
	public static double getLeftGearboxOutputVoltage() { return talonL1.getMotorOutputVoltage(); }
	public static double getRightGearboxOutputVoltage() { return talonR1.getMotorOutputVoltage(); }
	public static void resetGyro() { gyro.addYaw(getGyro()); }
	public static double getGyro() {
		double[] ypr = new double[3];
		gyro.getYawPitchRoll(ypr); 
		return -ypr[0];
	}
	public static void resetEncoders() {
		talonL1.setSelectedSensorPosition(0);
		talonR1.setSelectedSensorPosition(0);
	}
	public static DriveEncoders getEncoders() {
		return new DriveEncoders(
			talonL1.getSelectedSensorPosition(),
			talonR1.getSelectedSensorPosition(),
			DriveUnits.ticksToWheelRevolutions(talonL1.getSelectedSensorPosition()),
			DriveUnits.ticksToWheelRevolutions(talonR1.getSelectedSensorPosition()),
			talonL1.getSelectedSensorVelocity(),
			talonR1.getSelectedSensorVelocity(),
			DriveUnits.ticksToWheelRevolutions(talonL1.getSelectedSensorVelocity()),
			DriveUnits.ticksToWheelRevolutions(talonR1.getSelectedSensorVelocity())
		);
	}
	
	//Config
	static void init() {
		talonL1 = new TalonSRX(11);
		talonL2 = new TalonSRX(12);
		talonR1 = new TalonSRX(13);
		talonR2 = new TalonSRX(14);
		gyro = new PigeonIMU(talonR2);
		
		talonL1.configFactoryDefault();
		talonL2.configFactoryDefault();
		talonL1.config_kP(0, DriveConstants.DRIVE_KP, 0);
		talonL1.config_kI(0, DriveConstants.DRIVE_KI, 0);
		talonL1.config_kD(0, DriveConstants.DRIVE_KD, 0);
		talonL1.config_kF(0, 0, 0);
		talonL2.set(ControlMode.Follower, talonL1.getDeviceID());
		talonL1.setInverted(true);
		talonL2.setInverted(true);
		
		talonR1.configFactoryDefault();
		talonR2.configFactoryDefault();
		talonR1.config_kP(0, DriveConstants.DRIVE_KP, 0);
		talonR1.config_kI(0, DriveConstants.DRIVE_KI, 0);
		talonR1.config_kD(0, DriveConstants.DRIVE_KD, 0);
		talonR1.config_kF(0, 0, 0);
		talonR2.set(ControlMode.Follower, talonR1.getDeviceID());
		
		stop();
		resetGyro();
		resetEncoders();
	}
}
