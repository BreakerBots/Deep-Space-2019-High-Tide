package frc.team5104.subsystems.intake;

import frc.team5104.main.Constants;
import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.SubsystemManager.DebugMessage;

class IntakeLooper extends Subsystem.Looper {

	//Loop
	protected void update() {
		//Open/Close Intake
		Intake._interface.setMode(IWE.getGamePiece());
		
		//Set Wheel Speed
		if (IWE.getState() == IWEState.IDLE || IWE.getState() == IWEState.PLACE) {
			if (IWE.getGamePiece() == IWEGamePiece.HATCH ? Intake._interface.hasHatch() : Intake._interface.hasCargo()) {
				//Hold
				Intake._interface.setWheelSpeed(IWE.getGamePiece() == IWEGamePiece.HATCH ? 
						Constants.INTAKE_HOLD_SPEED_HATCH : 
						-Constants.INTAKE_HOLD_SPEED_CARGO
					);
			}
			else {
				//Idle
				Intake._interface.setWheelSpeed(0);
			}
		}
		else if (IWE.getState() == IWEState.INTAKE) {
			//Intake
			Intake._interface.setWheelSpeed(IWE.getGamePiece() == IWEGamePiece.HATCH ? 
					Constants.INTAKE_INTAKE_SPEED_HATCH : 
					-Constants.INTAKE_INTAKE_SPEED_CARGO
				);
		}
		else if (IWE.getState() == IWEState.EJECT) {
			//Eject
			Intake._interface.setWheelSpeed(IWE.getGamePiece() == IWEGamePiece.HATCH ? 
					-Constants.INTAKE_EJECT_SPEED_HATCH : 
					Constants.INTAKE_EJECT_SPEED_CARGO
				);
		}
	}

	//Debug
	protected DebugMessage debug() {
		return new DebugMessage(
				"hasCargo: ", Intake._interface.hasCargo(),
				"hasHatch: ", Intake._interface.hasHatch()
			);
	}
	
	//Enabled/Disabled
	protected void disabled() { Intake._interface.stopWheels(); }
	protected void enabled() { }
}
