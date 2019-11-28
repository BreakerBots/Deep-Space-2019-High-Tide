package frc.team5104.subsystems.intake;

import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.subsystems.intake.IntakeInterface.IntakePistonMode;
import frc.team5104.util.managers.Subsystem;

public class Intake extends Subsystem {
	protected String getName() { return "Intake"; }

	//Enabled, Disabled, Init
	protected void init() { IntakeInterface.init(); }
	protected void enabled() {
		IntakeInterface.stopWheels();
	}
	protected void disabled() {
		IntakeInterface.stopWheels();
	}
	
	//Loop
	protected void update() {
		//Open/Close Intake
		IntakeInterface.setMode(
				(IWE.getGamePiece() == IWEGamePiece.CARGO && IWE.getState() == IWEState.INTAKE) ? 
				IntakePistonMode.OPEN :
				IntakePistonMode.CLOSED
			);
		
		//Set Wheel Speed
		if (IWE.getState() == IWEState.IDLE || IWE.getState() == IWEState.PLACE) {
			if (IWE.getGamePiece() == IWEGamePiece.HATCH ? IntakeInterface.hasHatch() : IntakeInterface.hasCargo()) {
				//Hold
				IntakeInterface.setWheelSpeed(IWE.getGamePiece() == IWEGamePiece.HATCH ? 
						IntakeConstants.INTAKE_HOLD_SPEED_HATCH : 
						-IntakeConstants.INTAKE_HOLD_SPEED_CARGO
					);
			}
			else {
				//Idle
				IntakeInterface.setWheelSpeed(0);
			}
		}
		else if (IWE.getState() == IWEState.INTAKE) {
			//Intake
			IntakeInterface.setWheelSpeed(IWE.getGamePiece() == IWEGamePiece.HATCH ? 
					IntakeConstants.INTAKE_INTAKE_SPEED_HATCH : 
					-IntakeConstants.INTAKE_INTAKE_SPEED_CARGO
				);
		}
		else if (IWE.getState() == IWEState.EJECT) {
			//Eject
			IntakeInterface.setWheelSpeed(IWE.getGamePiece() == IWEGamePiece.HATCH ? 
					-IntakeConstants.INTAKE_EJECT_SPEED_HATCH : 
						IntakeConstants.INTAKE_EJECT_SPEED_CARGO
				);
		}
	}
}
