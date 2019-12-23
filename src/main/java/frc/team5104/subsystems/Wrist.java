package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Subsystems;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.GamePiece;
import frc.team5104.Superstructure.Height;
import frc.team5104.Superstructure.IntakeMode;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.subsystem.Subsystem;

public class Wrist extends Subsystem.WristSubsystem {
	private TalonSRX wristTalon;
	public double desiredWristManaul = 0;
	private MovingAverage limitSwitchZeroBuffer = new MovingAverage(5, false);
	
	//Loop
	protected void update() {
		//Auto
		if (Superstructure.getSystemState() == SystemState.AUTONOMOUS)
			setMotionMagic(
					getTargetAngle(), 
					atTargetAngle()
				);
		
		//Calibrating
		else if (Superstructure.getSystemState() == SystemState.CALIBRATING && !backLimitHit())
			setPercentOutput(-Constants.WRIST_CALIBRATE_SPEED);
		
		//Manual
		else if (Superstructure.getSystemState() == SystemState.MANUAL)
			setPercentOutput(desiredWristManaul);
		
		//Stop
		else stop();
		
		//Zero Encoder In Runtime
		limitSwitchZeroBuffer.update(backLimitHit());
		if (limitSwitchZeroBuffer.getBooleanOutput())
			resetEncoder();
	}

	//Internal Functions
	protected void setMotionMagic(double angle, boolean limpMode) {
		setLimpMode(limpMode);
		wristTalon.set(
			ControlMode.MotionMagic, angle / 360.0 * 4096.0, 
			DemandType.ArbitraryFeedForward, getFTerm()
		);
	}
	protected double getFTerm() {
		return (-getEncoderAngle() / 900.0) + 0.1;
	}
	protected void setPercentOutput(double percent) {
		setLimpMode(false);
		wristTalon.set(ControlMode.PercentOutput, percent);
	}
	protected void stop() {
		setLimpMode(false);
		wristTalon.set(ControlMode.Disabled, 0);
	}
	protected void setLimpMode(boolean limp) {
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
	public double getEncoderAngle() {
		return getRawEncoderAngle() / 4096.0 * 360.0;
	}
	public double getRawEncoderAngle() {
		return wristTalon.getSelectedSensorPosition();
	}
	public double getRawEncoderVelocity() {
		return wristTalon.getSelectedSensorVelocity();
	}
	public void resetEncoder() {
		wristTalon.setSelectedSensorPosition(0);
	}
	public boolean encoderDisconnected() {
		return wristTalon.getSensorCollection().getPulseWidthRiseToRiseUs() == 0;
	}
	public boolean backLimitHit() {
		return wristTalon.getSensorCollection().isRevLimitSwitchClosed();
	}
	public double getMotorPercentOutput() {
		return wristTalon.getMotorOutputPercent();
	}
	public double getTargetAngle() {
		if (Superstructure.getSystemState() == SystemState.AUTONOMOUS) {
			if (Superstructure.getMode() == Mode.IDLE || !Subsystems.elevator.isRoughlyAtTargetHeight()) 
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
	public boolean atTargetAngle() {
		return Math.abs(getTargetAngle() - getEncoderAngle()) < Constants.WRIST_ANGLE_TOL;
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
	
	//Calibrate
	protected boolean isCalibrated() {
		return backLimitHit();
	}
}