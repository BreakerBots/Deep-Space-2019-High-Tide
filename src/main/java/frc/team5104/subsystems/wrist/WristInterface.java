package frc.team5104.subsystems.wrist;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.team5104.main.Ports;
import frc.team5104.subsystems.wrist.WristConstants.WristPosition;

public class WristInterface {

	//Devices
	private static TalonSRX wristTalon;
	static double lastTargetAngle = 0;
	
	//Internal Functions
	static void setMotionMagic(double angle, boolean limpMode) {
		lastTargetAngle = angle;
		setLimpMode(limpMode);
		wristTalon.set(
			ControlMode.MotionMagic, angle / 360.0 * 4096.0, 
			DemandType.ArbitraryFeedForward, getFTerm()
		);
	}
	static double getFTerm() {
		double f = (-getEncoderAngle() / 900.0) + 0.1;
		if (Wrist.wristPosition == WristPosition.BACK)
			f -= 0.1;
		return f;
	}
	static void setPercentOutput(double percent) {
		setLimpMode(false);
		wristTalon.set(ControlMode.PercentOutput, percent);
	}
	static void stop() {
		setLimpMode(false);
		wristTalon.set(ControlMode.Disabled, 0);
	}
	static void setLimpMode(boolean limp) {
		if (limp) {
			wristTalon.configPeakOutputForward(getFTerm() + WristConstants.WRIST_LIMP_MODE_MAX_SPEED);
			wristTalon.configPeakOutputReverse(getFTerm() - WristConstants.WRIST_LIMP_MODE_MAX_SPEED);
		}
		else {
			wristTalon.configPeakOutputForward(1);
			if (Wrist.wristPosition == WristPosition.BACK && getEncoderAngle() < 45)
				wristTalon.configPeakOutputReverse(-0.25);
			else wristTalon.configPeakOutputReverse(-1);
		}
	}
	
	//External Functions
	public static double getEncoderAngle() {
		return getRawEncoderAngle() / 4096.0 * 360.0;
	}
	public static double getRawEncoderAngle() {
		return wristTalon.getSelectedSensorPosition();
	}
	public static double getRawEncoderVelocity() {
		return wristTalon.getSelectedSensorVelocity();
	}
	public static void resetEncoder() {
		wristTalon.setSelectedSensorPosition(0);
	}
	public static boolean encoderDisconnected() {
		return wristTalon.getSensorCollection().getPulseWidthRiseToRiseUs() == 0;
	}
	public static boolean backLimitSwitchHit() {
		return wristTalon.getSensorCollection().isRevLimitSwitchClosed();
	}
	public static double getMotorPercentOutput() {
		return wristTalon.getMotorOutputPercent();
	}
	
	//Config
	static void init() {
		wristTalon = new TalonSRX(Ports.WRIST_TALON);
		wristTalon.configFactoryDefault();
		wristTalon.configContinuousCurrentLimit(WristConstants.WRIST_CURRENT_LIMIT, 10);
		wristTalon.enableCurrentLimit(true);
		wristTalon.setNeutralMode(WristConstants.WRIST_NEUTRAL_MODE);
		wristTalon.config_kP(0, WristConstants.WRIST_MOTION_KP);
		wristTalon.config_kI(0, WristConstants.WRIST_MOTION_KI);
		wristTalon.config_kD(0, WristConstants.WRIST_MOTION_KD);
		wristTalon.config_kF(0, 0);
		wristTalon.configMotionAcceleration(WristConstants.WRIST_MOTION_ACCEL);
		wristTalon.configMotionCruiseVelocity(WristConstants.WRIST_MOTION_CRUISE_VELOCITY);
		wristTalon.configNominalOutputForward(0);
		wristTalon.configNominalOutputReverse(0);
		wristTalon.configPeakOutputForward(1);
		wristTalon.configPeakOutputReverse(-1);
		wristTalon.selectProfileSlot(0, 0);
		wristTalon.setSensorPhase(true);
	}
}
