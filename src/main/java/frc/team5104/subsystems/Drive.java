/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import frc.team5104.Constants;
import frc.team5104.util.DriveSignal;
import frc.team5104.util.Units;
import frc.team5104.util.subsystem.Subsystem;

public class Drive extends Subsystem.DriveSubsystem {
	private TalonSRX talonL1, talonL2, talonR1, talonR2;
	private PigeonIMU gyro;
	private DriveSignal currentDriveSignal = new DriveSignal();
	
	//Loop
	protected void update() {
		switch (currentDriveSignal.unit) {
			case PERCENT_OUTPUT: {
				setMotors(
						currentDriveSignal.leftSpeed, 
						currentDriveSignal.rightSpeed, 
						ControlMode.PercentOutput,
						currentDriveSignal.leftFeedForward,
						currentDriveSignal.rightFeedForward
					);
				break;
			}
			case FEET_PER_SECOND: {
				setMotors(
						Units.feetPerSecondToTalonVel(currentDriveSignal.leftSpeed), 
						Units.feetPerSecondToTalonVel(currentDriveSignal.rightSpeed), 
						ControlMode.Velocity,
						currentDriveSignal.leftFeedForward,
						currentDriveSignal.rightFeedForward
					);
				break;
			}
			case VOLTAGE: {
				setMotors(
						currentDriveSignal.leftSpeed / getLeftGearboxVoltage(),
						currentDriveSignal.rightSpeed / getRightGearboxVoltage(),
						ControlMode.PercentOutput,
						currentDriveSignal.leftFeedForward,
						currentDriveSignal.rightFeedForward
					);
				break;
			}
			case STOP:
				stopMotors();
				break;
		}
		currentDriveSignal = new DriveSignal();
	}
	
	//Internal Functions
	protected void setMotors(double leftSpeed, double rightSpeed, ControlMode controlMode, double leftFeedForward, double rightFeedForward) {
		talonL1.set(controlMode, leftSpeed, DemandType.ArbitraryFeedForward, leftFeedForward);
		talonR1.set(controlMode, rightSpeed, DemandType.ArbitraryFeedForward, rightFeedForward);
	}
	protected void stopMotors() {
		talonL1.set(ControlMode.Disabled, 0);
		talonR1.set(ControlMode.Disabled, 0);
	}
	
	//External Functions
	public void set(DriveSignal signal) {
		currentDriveSignal = signal;
	}
	public void stop() {
		currentDriveSignal = new DriveSignal();
		stopMotors();
	}
	public double getLeftGearboxVoltage() { return talonL1.getBusVoltage(); }
	public double getRightGearboxVoltage() { return talonR1.getBusVoltage(); }
	public double getLeftGearboxOutputVoltage() { return talonL1.getMotorOutputVoltage(); }
	public double getRightGearboxOutputVoltage() { return talonR1.getMotorOutputVoltage(); }
	public void resetGyro() { gyro.addYaw(getGyro()); }
	public double getGyro() {
		double[] ypr = new double[3];
		gyro.getYawPitchRoll(ypr); 
		return -ypr[0];
	}
	public void resetEncoders() {
		talonL1.setSelectedSensorPosition(0);
		talonR1.setSelectedSensorPosition(0);
	}
	public int getLeftEncoderPositionRaw() {
		return talonL1.getSelectedSensorPosition();
	}
	public int getRightEncoderPositionRaw() {
		return talonR1.getSelectedSensorPosition();
	}
	
	//Config
	protected void init() {
		talonL1 = new TalonSRX(11);
		talonL2 = new TalonSRX(12);
		talonR1 = new TalonSRX(13);
		talonR2 = new TalonSRX(14);
		gyro = new PigeonIMU(talonR2);
		
		talonL1.configFactoryDefault();
		talonL2.configFactoryDefault();
		talonL1.config_kP(0, Constants.DRIVE_KP, 0);
		talonL1.config_kI(0, Constants.DRIVE_KI, 0);
		talonL1.config_kD(0, Constants.DRIVE_KD, 0);
		talonL1.config_kF(0, 0, 0);
		talonL2.set(ControlMode.Follower, talonL1.getDeviceID());
		talonL1.setInverted(true);
		talonL2.setInverted(true);
		
		talonR1.configFactoryDefault();
		talonR2.configFactoryDefault();
		talonR1.config_kP(0, Constants.DRIVE_KP, 0);
		talonR1.config_kI(0, Constants.DRIVE_KI, 0);
		talonR1.config_kD(0, Constants.DRIVE_KD, 0);
		talonR1.config_kF(0, 0, 0);
		talonR2.set(ControlMode.Follower, talonR1.getDeviceID());
		
		stopMotors();
		resetGyro();
		resetEncoders();
	}
}