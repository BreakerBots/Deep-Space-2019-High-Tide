package frc.team5104.subsystems.wrist;

import frc.team5104.main.Constants;
import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEControl;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEHeight;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.subsystems.elevator.Elevator;
import frc.team5104.util.Buffer;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.Subsystem;

class WristLooper extends Subsystem.Looper {
	
	//Enums
	static enum WristState { CALIBRATING, MANUAL, AUTONOMOUS };
	static enum WristPosition {
		BACK(0), HATCH_INTAKE(80), HATCH_EJECT(80), CARGO_EJECT_ROCKET(30), 
		CARGO_EJECT_SHIP(120), CARGO_INTAKE_WALL(50), CARGO_INTAKE_GROUND(125); /*CARGO_INTAKE_GROUND(160), HATCH_PLACE_ANGLED(60), CARGO_PLACE_ANGLED(135)*/
		public double degrees; private WristPosition(double degrees) { this.degrees = degrees; }
	}
	WristState wristState;
	WristPosition wristPosition;
	private WristState lastWristState;
	private WristPosition lastWristPosition;
	long wristStateStartTime = 0;
	long wristPositionStartTime = 0;
	private Buffer limitSwitchZeroBuffer = new Buffer(5, false);
	private Buffer averageMotorOutput = new Buffer(300, 0.0);
	
	//Loop
	protected void update() {
		//Sync Wrist State (Force Manaul if IWE is Manual. If switched from manual -> auto then bring it into calibrating)
		if (IWE.getControl() == IWEControl.MANUAL) wristState = WristState.MANUAL;
		else if (wristState == WristState.MANUAL) wristState = WristState.CALIBRATING;
		
		//Sync Wrist Position
		if (IWE.getState() == IWEState.IDLE || !Elevator.isRoughlyAtTargetHeight()) 
			wristPosition = WristPosition.BACK;
		else if (IWE.getGamePiece() == IWEGamePiece.HATCH) {
			//Hatch
			if (IWE.getState() == IWEState.INTAKE)
				wristPosition = WristPosition.HATCH_INTAKE;
			else wristPosition = WristPosition.HATCH_EJECT;
		}
		else {
			//Cargo
			if (IWE.getState() == IWEState.INTAKE) {
				if (IWE.cargoIntakeGround)
					wristPosition = WristPosition.CARGO_INTAKE_GROUND;
				else wristPosition = WristPosition.CARGO_INTAKE_WALL;
			}
			else {
				if (IWE.getHeight() == IWEHeight.SHIP)
					wristPosition = WristPosition.CARGO_EJECT_SHIP;
				else wristPosition = WristPosition.CARGO_EJECT_ROCKET;
			}
		}
		
		//Time Tracking for State and Position
		if (lastWristState != wristState)
			wristStateStartTime = System.currentTimeMillis();
		if (lastWristPosition != wristPosition)
			wristPositionStartTime = System.currentTimeMillis();
		
		//Recalibrate During Runtime
		if (wristState == WristState.AUTONOMOUS && wristPosition == WristPosition.BACK &&
			!Wrist.backLimitSwitchHit() && System.currentTimeMillis() > wristPositionStartTime + 8000) {
			console.warn(c.WRIST, "Recalibrating Wrist");
			wristState = WristState.CALIBRATING;
		}
		
		//Control Wrist
		if (wristState == WristState.AUTONOMOUS) {
			//Autonomous
			Wrist._interface.setMotionMagic(wristPosition.degrees, System.currentTimeMillis() > wristPositionStartTime + Constants.WRIST_LIMP_MODE_TIME_START);
			
			//Error Catch
			//if (giving neg output for too long) { stop(); }
			//if (giving pos output for too long) { stop(); }
		}
		else if (wristState == WristState.CALIBRATING) {
			//Calibrating
			if (!Wrist._interface.backLimitSwitchHit())
				Wrist._interface.setPercentOutput(-Constants.WRIST_CALIBRATE_SPEED);
			else {
				wristState = WristState.AUTONOMOUS;
				Wrist._interface.resetEncoder();
			}
			
			//Error Catch
			if (System.currentTimeMillis() > wristStateStartTime + 6000) {
				console.error(c.WRIST, "WTF!!!! Calibration Error (Entering Manual)");
				IWE.setControl(IWEControl.MANUAL);
			}
		}
		else {
			//Manual
			Wrist._interface.setPercentOutput(IWE.desiredWristManaul);
		}
		
		//Zero Encoder In Runtime
		limitSwitchZeroBuffer.update(Wrist._interface.backLimitSwitchHit());
		if (limitSwitchZeroBuffer.getBooleanOutput())
			Wrist._interface.resetEncoder();
		
		//Other
		lastWristState = wristState;
		lastWristPosition = wristPosition;
		averageMotorOutput.update(Wrist._interface.getMotorPercentOutput());
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
