package frc.team5104.subsystems.wrist;

import frc.team5104.main.Constants;
import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEControl;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.util.managers.Subsystem;

class WristLooper extends Subsystem.Looper {
	
	//Enums
	static enum WristState { CALIBRATING, MANUAL, AUTONOMOUS };
	static enum WristPosition { 
		BACK(0), UP(90), CARGO_INTAKE_WALL(45); /*CARGO_INTAKE_GROUND(160), HATCH_PLACE_ANGLED(60), CARGO_PLACE_ANGLED(135)*/
		public double degrees; private WristPosition(double degrees) { this.degrees = degrees; }
	}
	WristState wristState;
	WristPosition wristPosition;
	
	//Loop
	protected void update() {
		//Sync Wrist State (Force Manaul if IWE is Manual. If switched from manual -> auto then bring it into calibrating)
		if (IWE.getControl() == IWEControl.MANUAL) wristState = WristState.MANUAL;
		else if (wristState == WristState.MANUAL) wristState = WristState.CALIBRATING;
		
		//Sync Wrist Position
		if (IWE.getState() == IWEState.IDLE) wristPosition = WristPosition.BACK;
		else if (IWE.getState() == IWEState.INTAKE) {
			if (IWE.getGamePiece() == IWEGamePiece.HATCH)
				wristPosition = WristPosition.UP;
			else wristPosition = WristPosition.CARGO_INTAKE_WALL;
		}
		else wristPosition = WristPosition.UP;
		
		//Control Wrist
		if (wristState == WristState.AUTONOMOUS) {
			Wrist._interface.setMotionMagic(wristPosition.degrees);
		}
		else if (wristState == WristState.CALIBRATING) {
			if (!Wrist._interface.backLimitSwitchHit())
				Wrist._interface.setPercentOutput(-Constants.WRIST_CALIBRATE_SPEED);
			else {
				wristState = WristState.AUTONOMOUS;
				Wrist._interface.resetEncoder();
			}
		}
		else {
			Wrist._interface.setPercentOutput(IWE.desiredWristManaul);
		}
	}

	//Enabled/Disabled
	protected void disabled() {
		if (IWE.getControl() == IWEControl.AUTONOMOUS)
			wristState = WristState.CALIBRATING;
		Wrist._interface.stop();
	}
	protected void enabled() {
		if (IWE.getControl() == IWEControl.AUTONOMOUS)
			wristState = WristState.CALIBRATING;
	}
}
