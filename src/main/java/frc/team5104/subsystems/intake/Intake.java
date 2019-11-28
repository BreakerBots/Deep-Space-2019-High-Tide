package frc.team5104.subsystems.intake;

import frc.team5104.main.Superstructure;
import frc.team5104.main.Superstructure.GamePiece;
import frc.team5104.main.Superstructure.SystemState;
import frc.team5104.subsystems.intake.IntakeInterface.IntakePistonMode;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.managers.Subsystem;

public class Intake extends Subsystem {
	protected String getName() { return "Intake"; }

	//Actions
	private static MovingAverage cargoBuffer = new MovingAverage(10, 0);
	private static MovingAverage hatchBuffer = new MovingAverage(10, 0);
	public static boolean hasHatch() {
		return hatchBuffer.getBooleanOutput();
	}
	public static boolean hasCargo() {
		return cargoBuffer.getBooleanOutput();
	}
	
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
				(Superstructure.getGamePiece() == GamePiece.CARGO && Superstructure.getState() == SystemState.INTAKE) ? 
				IntakePistonMode.OPEN :
				IntakePistonMode.CLOSED
			);
		
		//Set Wheel Speed
		if (Superstructure.getState() == SystemState.IDLE || Superstructure.getState() == SystemState.PLACE) {
			if (Superstructure.getGamePiece() == GamePiece.HATCH ? IntakeInterface.hasHatch() : IntakeInterface.hasCargo()) {
				//Hold
				IntakeInterface.setWheelSpeed(Superstructure.getGamePiece() == GamePiece.HATCH ? 
						IntakeConstants.INTAKE_HOLD_SPEED_HATCH : 
						-IntakeConstants.INTAKE_HOLD_SPEED_CARGO
					);
			}
			else {
				//Idle
				IntakeInterface.setWheelSpeed(0);
			}
		}
		else if (Superstructure.getState() == SystemState.INTAKE) {
			//Intake
			IntakeInterface.setWheelSpeed(Superstructure.getGamePiece() == GamePiece.HATCH ? 
					IntakeConstants.INTAKE_INTAKE_SPEED_HATCH : 
					-IntakeConstants.INTAKE_INTAKE_SPEED_CARGO
				);
		}
		else if (Superstructure.getState() == SystemState.EJECT) {
			//Eject
			IntakeInterface.setWheelSpeed(Superstructure.getGamePiece() == GamePiece.HATCH ? 
					-IntakeConstants.INTAKE_EJECT_SPEED_HATCH : 
						IntakeConstants.INTAKE_EJECT_SPEED_CARGO
				);
		}
		
		//Banner Sensors
		cargoBuffer.update(IntakeInterface.hasCargo());
		hatchBuffer.update(IntakeInterface.hasHatch());
	}
}
