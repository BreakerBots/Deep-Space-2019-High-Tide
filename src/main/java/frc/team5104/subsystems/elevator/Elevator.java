package frc.team5104.subsystems.elevator;

import frc.team5104.main.Superstructure;
import frc.team5104.main.Superstructure.ControlMode;
import frc.team5104.main.Superstructure.GamePiece;
import frc.team5104.main.Superstructure.Height;
import frc.team5104.main.Superstructure.SystemState;
import frc.team5104.subsystems.elevator.ElevatorConstants.ElevatorPosition;
import frc.team5104.subsystems.elevator.ElevatorConstants.ElevatorState;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.Subsystem;

public class Elevator extends Subsystem {
	protected String getName() { return "Elevator"; }

	//Enabled, Disabled, Init
	protected void init() { ElevatorInterface.init(); }
	protected void enabled() {
		if (Superstructure.getControlMode() == ControlMode.AUTONOMOUS)
			elevatorState = ElevatorState.CALIBRATING;
	}
	protected void disabled() {
		if (Superstructure.getControlMode() == ControlMode.AUTONOMOUS)
			elevatorState = ElevatorState.CALIBRATING;
		ElevatorInterface.stop();
	}

	//Loop
	static ElevatorState elevatorState;
	static ElevatorPosition elevatorPosition;
	static ElevatorState lastElevatorState;
	static ElevatorPosition lastElevatorPosition;
	static long elevatorStateStartTime = System.currentTimeMillis();
	static long elevatorPositionStartTime = System.currentTimeMillis();
	static MovingAverage limitSwitchZeroBuffer = new MovingAverage(5, false);
	static MovingAverage averageMotorOutput = new MovingAverage(300, 0.0);
	protected void update() {
		syncStates();
		
		//Time Tracking For State/Position
		if (lastElevatorPosition != elevatorPosition)
			elevatorPositionStartTime = System.currentTimeMillis();
		if (lastElevatorState != elevatorState)
			elevatorStateStartTime = System.currentTimeMillis();
		
		//Control Elevator
		if (elevatorState == ElevatorState.AUTONOMOUS) {
			//Auto
			ElevatorInterface.setMotionMagic(elevatorPosition.height);
		}
		else if (elevatorState == ElevatorState.CALIBRATING) {
			//Calibrating
			if (!ElevatorInterface.lowerLimitHit())
				ElevatorInterface.setPercentOutput(-ElevatorConstants.ELEVATOR_CALIBRATE_SPEED);
			else {
				elevatorState = ElevatorState.AUTONOMOUS;
				ElevatorInterface.resetEncoder();
			}
			
			//Error Catch
			if (System.currentTimeMillis() > elevatorStateStartTime + 6000) {
				console.error(c.ELEVATOR, "WTF!!!! Calibration Error (Entering Manual)");
				Superstructure.setControlMode(ControlMode.MANUAL);
			}
		}
		else {
			//Manual
			ElevatorInterface.setPercentOutput(Superstructure.desiredElevatorManaul);
		}
		
		//Zero Encoder In Runtime
		limitSwitchZeroBuffer.update(ElevatorInterface.lowerLimitHit());
		if (limitSwitchZeroBuffer.getBooleanOutput())
			ElevatorInterface.resetEncoder();
			
		//Other
		lastElevatorPosition = elevatorPosition;
		lastElevatorState = elevatorState;
		averageMotorOutput.update(ElevatorInterface.getMotorPercentOutput());
	}

	//Sync States with IWE
	void syncStates() {
		//Sync Elevator State (Force Manaul if IWE is Manual. If switched from manual -> auto then bring it into calibrating)
		if (Superstructure.getControlMode() == ControlMode.MANUAL) elevatorState = ElevatorState.MANUAL;
		else if (elevatorState == ElevatorState.MANUAL) elevatorState = ElevatorState.CALIBRATING;
		
		//Sync Elevator Height
		if (Superstructure.getState() == SystemState.EJECT || Superstructure.getState() == SystemState.PLACE) {
			elevatorPosition = (
				// L3 Cargo, Hatch
				Superstructure.getHeight() == Height.L3 ? 
					(Superstructure.getGamePiece() == Superstructure.GamePiece.CARGO ? 
						ElevatorPosition.CARGO_L3 : ElevatorPosition.HATCH_L3) :
				//L2 Cargo, Hatch
				(Superstructure.getHeight() == Height.L2 ? 
					(Superstructure.getGamePiece() == Superstructure.GamePiece.CARGO ?
						ElevatorPosition.CARGO_L2 : ElevatorPosition.HATCH_L2) : 
				//Ship + L1 Cargo, Hatch
					(Superstructure.getGamePiece() == Superstructure.GamePiece.CARGO ? 
						(Superstructure.getHeight() == Height.L1 ? 
							ElevatorPosition.CARGO_L1 : ElevatorPosition.CARGO_SHIP) :
						ElevatorPosition.BOTTOM))
			);
		}
		else if (Superstructure.getState() == SystemState.INTAKE) {
			if (Superstructure.getGamePiece() == GamePiece.HATCH) elevatorPosition = ElevatorPosition.BOTTOM;
			else {
				if (Superstructure.cargoIntakeGround)
					elevatorPosition = ElevatorPosition.BOTTOM;
				else elevatorPosition = ElevatorPosition.CARGO_WALL;
			}
		}
		else elevatorPosition = ElevatorPosition.BOTTOM;
	}
}
