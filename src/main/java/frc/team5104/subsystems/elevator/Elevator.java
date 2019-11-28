package frc.team5104.subsystems.elevator;

import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEControl;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEHeight;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.subsystems.elevator.ElevatorConstants.ElevatorPosition;
import frc.team5104.subsystems.elevator.ElevatorConstants.ElevatorState;
import frc.team5104.util.Buffer;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.Subsystem;

public class Elevator extends Subsystem {
	protected String getName() { return "Elevator"; }

	//Enabled, Disabled, Init
	protected void init() { ElevatorInterface.init(); }
	protected void enabled() {
		if (IWE.getControl() == IWEControl.AUTONOMOUS)
			elevatorState = ElevatorState.CALIBRATING;
	}
	protected void disabled() {
		if (IWE.getControl() == IWEControl.AUTONOMOUS)
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
	static Buffer limitSwitchZeroBuffer = new Buffer(5, false);
	static Buffer averageMotorOutput = new Buffer(300, 0.0);
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
				IWE.setControl(IWEControl.MANUAL);
			}
		}
		else {
			//Manual
			ElevatorInterface.setPercentOutput(IWE.desiredElevatorManaul);
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
		if (IWE.getControl() == IWEControl.MANUAL) elevatorState = ElevatorState.MANUAL;
		else if (elevatorState == ElevatorState.MANUAL) elevatorState = ElevatorState.CALIBRATING;
		
		//Sync Elevator Height
		if (IWE.getState() == IWEState.EJECT || IWE.getState() == IWEState.PLACE) {
			elevatorPosition = (
				// L3 Cargo, Hatch
				IWE.getHeight() == IWEHeight.L3 ? 
					(IWE.getGamePiece() == IWE.IWEGamePiece.CARGO ? 
						ElevatorPosition.CARGO_L3 : ElevatorPosition.HATCH_L3) :
				//L2 Cargo, Hatch
				(IWE.getHeight() == IWEHeight.L2 ? 
					(IWE.getGamePiece() == IWE.IWEGamePiece.CARGO ?
						ElevatorPosition.CARGO_L2 : ElevatorPosition.HATCH_L2) : 
				//Ship + L1 Cargo, Hatch
					(IWE.getGamePiece() == IWE.IWEGamePiece.CARGO ? 
						(IWE.getHeight() == IWEHeight.L1 ? 
							ElevatorPosition.CARGO_L1 : ElevatorPosition.CARGO_SHIP) :
						ElevatorPosition.BOTTOM))
			);
		}
		else if (IWE.getState() == IWEState.INTAKE) {
			if (IWE.getGamePiece() == IWEGamePiece.HATCH) elevatorPosition = ElevatorPosition.BOTTOM;
			else {
				if (IWE.cargoIntakeGround)
					elevatorPosition = ElevatorPosition.BOTTOM;
				else elevatorPosition = ElevatorPosition.CARGO_WALL;
			}
		}
		else elevatorPosition = ElevatorPosition.BOTTOM;
	}
}
