package frc.team5104.subsystems.wrist;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.main.Constants;
import frc.team5104.main.Ports;
import frc.team5104.subsystems.wrist.WristLooper.WristPosition;
import frc.team5104.util.WebappTuner.tunerOutput;
import frc.team5104.util.managers.Subsystem;

class WristInterface extends Subsystem.Interface {

	//Devices
	private TalonSRX wristTalon = new TalonSRX(Ports.WRIST_TALON);
	double lastTargetAngle = 0;
	
	//Functions
	void setMotionMagic(double angle, boolean limpMode) {
		lastTargetAngle = angle;
		setLimpMode(limpMode);
		wristTalon.set(
			ControlMode.MotionMagic, angle / 360.0 * 4096.0, 
			DemandType.ArbitraryFeedForward, getFTerm()
		);
	}
	private double getFTerm() {
		//0 -> 0.1
		//90 -> 0
		//180 -> -0.1
		double f = (-getEncoderAngle() / 900.0) + 0.1;
		if (Wrist._looper.wristPosition == WristPosition.BACK)
			f -= 0.1;
		return f;
	}
	void setPercentOutput(double percent) {
		setLimpMode(false);
		wristTalon.set(ControlMode.PercentOutput, percent);
	}
	void stop() {
		setLimpMode(false);
		wristTalon.set(ControlMode.Disabled, 0);
	}
	void setLimpMode(boolean limp) {
		if (limp) {
			wristTalon.configPeakOutputForward(getFTerm() + Constants.WRIST_LIMP_MODE_MAX_SPEED);
			wristTalon.configPeakOutputReverse(getFTerm() - Constants.WRIST_LIMP_MODE_MAX_SPEED);
		}
		else {
			wristTalon.configPeakOutputForward(1);
			
			if (Wrist._looper.wristPosition == WristPosition.BACK && getEncoderAngle() < 45)
				wristTalon.configPeakOutputReverse(-0.25);
			else wristTalon.configPeakOutputReverse(-1);
		}
	}
	double getEncoderAngle() {
		return getRawEncoderAngle() / 4096.0 * 360.0;
	}
	@tunerOutput
	double getRawEncoderAngle() {
		return wristTalon.getSelectedSensorPosition();
	}
	@tunerOutput
	double getRawEncoderVelocity() {
		return wristTalon.getSelectedSensorVelocity();
	}
	void resetEncoder() {
		wristTalon.setSelectedSensorPosition(0);
	}
	boolean encoderDisconnected() {
		return wristTalon.getSensorCollection().getPulseWidthRiseToRiseUs() == 0;
	}
	boolean backLimitSwitchHit() {
		return wristTalon.getSensorCollection().isRevLimitSwitchClosed();
	}
	double getMotorPercentOutput() {
		return wristTalon.getMotorOutputPercent();
	}
	
	//Config
	protected void init() {
		wristTalon.configFactoryDefault();
		wristTalon.configContinuousCurrentLimit(Constants.WRIST_CURRENT_LIMIT, 10);
		wristTalon.enableCurrentLimit(true);
		wristTalon.setNeutralMode(Constants.WRIST_NEUTRAL_MODE);
		wristTalon.config_kP(0, Constants.WRIST_MOTION_KP);
		wristTalon.config_kI(0, Constants.WRIST_MOTION_KI);
		wristTalon.config_kD(0, Constants.WRIST_MOTION_KD);
		wristTalon.config_kF(0, 0);
		wristTalon.configMotionAcceleration(Constants.WRIST_MOTION_ACCEL);
		wristTalon.configMotionCruiseVelocity(Constants.WRIST_MOTION_CRUISE_VELOCITY);
		wristTalon.configNominalOutputForward(0);
		wristTalon.configNominalOutputReverse(0);
		wristTalon.configPeakOutputForward(1);
		wristTalon.configPeakOutputReverse(-1);
		wristTalon.selectProfileSlot(0, 0);
		wristTalon.setSensorPhase(true);
	}
}
