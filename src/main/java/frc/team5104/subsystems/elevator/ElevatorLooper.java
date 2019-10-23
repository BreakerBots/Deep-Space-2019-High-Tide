package frc.team5104.subsystems.elevator;

import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEControl;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEHeight;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.SubsystemManager.DebugMessage;

class ElevatorLooper extends Subsystem.Looper {
	//Enums
	static enum ElevatorState { CALIBRATING, MANUAL, AUTONOMOUS };
	static enum ElevatorPosition { 
		BOTTOM(0), L1(12), L2(36), L3(60), CARGO_EJECT_SHIP(40), CARGO_INTAKE_WALL(20);
		public double height; private ElevatorPosition(double height) { this.height = height; }
	}
	ElevatorState elevatorState;
	ElevatorPosition elevatorPosition;
	
	//Loop
	protected void update() {
		//Sync Elevator State (Force Manaul if IWE is Manual. If switched from manual -> auto then bring it into calibrating)
		if (IWE.getControl() == IWEControl.MANUAL) elevatorState = ElevatorState.MANUAL;
		else if (elevatorState == ElevatorState.MANUAL) elevatorState = ElevatorState.CALIBRATING;
		
		//Sync Elevator Height
		if (IWE.getState() == IWEState.EJECT || IWE.getState() == IWEState.PLACE) {
			elevatorPosition = (
					IWE.getHeight() == IWEHeight.L3 ? ElevatorPosition.L3 : 
					(IWE.getHeight() == IWEHeight.L2 ? ElevatorPosition.L2 : 
					ElevatorPosition.L1)
				);
		}
		else if (IWE.getState() == IWEState.INTAKE) {
			if (IWE.getGamePiece() == IWEGamePiece.HATCH)
				elevatorPosition = ElevatorPosition.L1;
			else elevatorPosition = ElevatorPosition.CARGO_INTAKE_WALL;
		}
		else {
			elevatorPosition = ElevatorPosition.BOTTOM;
		}
		
		//Control Elevator
//		if (elevatorState == ElevatorState.AUTONOMOUS) {
//			//Auto
//			Elevator._interface.setMotionMagic(elevatorPosition.height);
//		}
//		else if (elevatorState == ElevatorState.CALIBRATING) {
//			//Calibrating
//			if (!Elevator._interface.lowerLimitSwitchHit())
//				Elevator._interface.setPercentOutput(-Constants.ELEVATOR_CALIBRATE_SPEED);
//			else {
//				elevatorState = ElevatorState.AUTONOMOUS;
//				Elevator._interface.resetEncoder();
//			}
//		}
//		else {
			//Manual
			//console.log(Elevator._interface.getRawEncoderVelocity());
			Elevator._interface.setPercentOutput(IWE.desiredElevatorManaul);
//		}
	}

	//Debug
	protected DebugMessage debug() {
		return new DebugMessage(
				"heit: ", Elevator._interface.getRawEncoderPosition(),
				"vel: ", Elevator._interface.getRawEncoderVelocity(),
				"po: ", IWE.desiredElevatorManaul
			);
	}
	
	//Enabled/Disabled
	protected void disabled() {
		if (IWE.getControl() == IWEControl.AUTONOMOUS)
			elevatorState = ElevatorState.CALIBRATING;
		Elevator._interface.stop();
	}
	protected void enabled() {
		if (IWE.getControl() == IWEControl.AUTONOMOUS)
			elevatorState = ElevatorState.CALIBRATING;
	}
}
