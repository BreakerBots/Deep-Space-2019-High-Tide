package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.main.Constants;
import frc.team5104.main.Ports;
import frc.team5104.main.Superstructure;
import frc.team5104.main.Superstructure.SystemState;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.managers.Subsystem;

public class Wrist extends Subsystem {
	protected String getName() { return "Wrist"; }

	private static TalonSRX wristTalon;
	public static double targetWristAngle = 0;
	public static boolean isLimp = false;
	public static double desiredWristManaul = 0;
	
	//Loop
	static MovingAverage limitSwitchZeroBuffer = new MovingAverage(5, false);
	protected void update() {
		//Auto
		if (Superstructure.getSystemState() == SystemState.AUTONOMOUS)
			setMotionMagic(targetWristAngle, isLimp);
		
		//Calibrating
		else if (Superstructure.getSystemState() == SystemState.CALIBRATING && !backLimitSwitchHit())
			setPercentOutput(-Constants.WRIST_CALIBRATE_SPEED);
		
		//Manual
		else if (Superstructure.getSystemState() == SystemState.MANUAL)
			setPercentOutput(desiredWristManaul);
		
		//Zero Encoder In Runtime
		limitSwitchZeroBuffer.update(backLimitSwitchHit());
		if (limitSwitchZeroBuffer.getBooleanOutput())
			resetEncoder();
	}

	//Internal Functions
	static void setMotionMagic(double angle, boolean limpMode) {
		setLimpMode(limpMode);
		wristTalon.set(
			ControlMode.MotionMagic, angle / 360.0 * 4096.0, 
			DemandType.ArbitraryFeedForward, getFTerm()
		);
	}
	static double getFTerm() {
		return (-getEncoderAngle() / 900.0) + 0.1;
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
			wristTalon.configPeakOutputForward(getFTerm() + Constants.WRIST_LIMP_MODE_MAX_SPEED);
			wristTalon.configPeakOutputReverse(getFTerm() - Constants.WRIST_LIMP_MODE_MAX_SPEED);
		}
		else {
			wristTalon.configPeakOutputForward(1);
			wristTalon.configPeakOutputReverse(-1);
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
	protected void init() {
		wristTalon = new TalonSRX(Ports.WRIST_TALON);
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
	protected void enabled() { stop(); }
	protected void disabled() { stop(); }
}