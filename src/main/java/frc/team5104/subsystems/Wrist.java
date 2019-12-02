package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.GamePiece;
import frc.team5104.Superstructure.Height;
import frc.team5104.Superstructure.IntakeMode;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.managers.Subsystem;

public class Wrist extends Subsystem {
	protected String getName() { return "Wrist"; }

	private static TalonSRX wristTalon;
	public static double desiredWristManaul = 0;
	
	//Loop
	static MovingAverage limitSwitchZeroBuffer = new MovingAverage(5, false);
	protected void update() {
		//Auto
		if (Superstructure.getSystemState() == SystemState.AUTONOMOUS)
			setMotionMagic(
					getTargetAngle(), 
					System.currentTimeMillis() > 
					(Superstructure.systemStateStart > Superstructure.modeStart ? 
					Superstructure.systemStateStart : Superstructure.modeStart) + 
					Constants.WRIST_LIMP_MODE_TIME_START
				);
		
		//Calibrating
		else if (Superstructure.getSystemState() == SystemState.CALIBRATING && !backLimitSwitchHit())
			setPercentOutput(-Constants.WRIST_CALIBRATE_SPEED);
		
		//Manual
		else if (Superstructure.getSystemState() == SystemState.MANUAL)
			setPercentOutput(desiredWristManaul);
		
		//Stop
		else stop();
		
		//Zero Encoder In Runtime
		limitSwitchZeroBuffer.update(backLimitSwitchHit());
		if (limitSwitchZeroBuffer.getBooleanOutput())
			resetEncoder();
	}

	//Internal Functions
	private static void setMotionMagic(double angle, boolean limpMode) {
		setLimpMode(limpMode);
		wristTalon.set(
			ControlMode.MotionMagic, angle / 360.0 * 4096.0, 
			DemandType.ArbitraryFeedForward, getFTerm()
		);
	}
	private static double getFTerm() {
		return (-getEncoderAngle() / 900.0) + 0.1;
	}
	private static void setPercentOutput(double percent) {
		setLimpMode(false);
		wristTalon.set(ControlMode.PercentOutput, percent);
	}
	private static void stop() {
		setLimpMode(false);
		wristTalon.set(ControlMode.Disabled, 0);
	}
	private static void setLimpMode(boolean limp) {
		if (limp) {
			wristTalon.configPeakOutputForward(getFTerm() + Constants.WRIST_LIMP_MODE_MAX_SPEED);
			wristTalon.configPeakOutputReverse(getFTerm() - Constants.WRIST_LIMP_MODE_MAX_SPEED);
		}
		else {
			wristTalon.configPeakOutputForward(1);
			wristTalon.configPeakOutputReverse(-1);
		}
	}
	private static double getTargetAngle() {
		if (Superstructure.getSystemState() == SystemState.AUTONOMOUS) {
			if (Superstructure.getMode() == Mode.IDLE || !Elevator.isRoughlyAtTargetHeight()) 
				return 0;
			else if (Superstructure.getGamePiece() == GamePiece.HATCH) {
				if (Superstructure.getMode() == Mode.INTAKE)
					return Constants.WRIST_ANGLE_HATCH_INTAKE;
				else return Constants.WRIST_ANGLE_HATCH_EJECT;
			}
			else {
				if (Superstructure.getMode() == Mode.INTAKE) {
					if (Superstructure.getIntakeMode() == IntakeMode.GROUND)
						return Constants.WRIST_ANGLE_CARGO_INTAKE_GROUND;
					else return Constants.WRIST_ANGLE_CARGO_INTAKE_WALL;
				}
				else {
					if (Superstructure.getHeight() == Height.SHIP)
						return Constants.WRIST_ANGLE_CARGO_EJECT_SHIP;
					else return Constants.WRIST_ANGLE_CARGO_EJECT_ROCKET;
				}
			}
		}
		else return 0;
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