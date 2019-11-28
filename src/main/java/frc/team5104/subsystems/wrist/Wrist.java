package frc.team5104.subsystems.wrist;

import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEControl;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEHeight;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.subsystems.elevator.ElevatorInterface;
import frc.team5104.subsystems.wrist.WristConstants.WristPosition;
import frc.team5104.subsystems.wrist.WristConstants.WristState;
import frc.team5104.util.Buffer;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.Subsystem;

public class Wrist extends Subsystem {
	protected String getName() { return "Wrist"; }

	//Enabled, Disabled, Init
	protected void init() { WristInterface.init(); }
	protected void enabled() {
		if (IWE.getControl() == IWEControl.AUTONOMOUS)
			wristState = WristState.CALIBRATING;
	}
	protected void disabled() {
		if (IWE.getControl() == IWEControl.AUTONOMOUS)
			wristState = WristState.CALIBRATING;
		WristInterface.stop();
	}
	
	//Loop
	static WristState wristState;
	static WristPosition wristPosition;
	static private WristState lastWristState;
	static private WristPosition lastWristPosition;
	static long wristStateStartTime = 0;
	static long wristPositionStartTime = 0;
	static Buffer limitSwitchZeroBuffer = new Buffer(5, false);
	static Buffer averageMotorOutput = new Buffer(300, 0.0);
	protected void update() {
		syncStates();
		
		//Time Tracking for State and Position
		if (lastWristState != wristState)
			wristStateStartTime = System.currentTimeMillis();
		if (lastWristPosition != wristPosition)
			wristPositionStartTime = System.currentTimeMillis();
		
		//Recalibrate During Runtime
		if (wristState == WristState.AUTONOMOUS && wristPosition == WristPosition.BACK &&
			!WristInterface.backLimitSwitchHit() && System.currentTimeMillis() > wristPositionStartTime + 8000) {
			console.warn(c.WRIST, "Recalibrating Wrist");
			wristState = WristState.CALIBRATING;
		}
		
		//Control Wrist
		if (wristState == WristState.AUTONOMOUS) {
			//Autonomous
			WristInterface.setMotionMagic(wristPosition.degrees, System.currentTimeMillis() > wristPositionStartTime + WristConstants.WRIST_LIMP_MODE_TIME_START);
			
			//Error Catch
			//if (giving neg output for too long) { stop(); }
			//if (giving pos output for too long) { stop(); }
		}
		else if (wristState == WristState.CALIBRATING) {
			//Calibrating
			if (!WristInterface.backLimitSwitchHit())
				WristInterface.setPercentOutput(-WristConstants.WRIST_CALIBRATE_SPEED);
			else {
				wristState = WristState.AUTONOMOUS;
				WristInterface.resetEncoder();
			}
			
			//Error Catch
			if (System.currentTimeMillis() > wristStateStartTime + 6000) {
				console.error(c.WRIST, "WTF!!!! Calibration Error (Entering Manual)");
				IWE.setControl(IWEControl.MANUAL);
			}
		}
		else {
			//Manual
			WristInterface.setPercentOutput(IWE.desiredWristManaul);
		}
		
		//Zero Encoder In Runtime
		limitSwitchZeroBuffer.update(WristInterface.backLimitSwitchHit());
		if (limitSwitchZeroBuffer.getBooleanOutput())
			WristInterface.resetEncoder();
		
		//Other
		lastWristState = wristState;
		lastWristPosition = wristPosition;
		averageMotorOutput.update(WristInterface.getMotorPercentOutput());
	}
	
	//Sync States with IWE
	void syncStates() {
		//Sync Wrist State (Force Manaul if IWE is Manual. If switched from manual -> auto then bring it into calibrating)
		if (IWE.getControl() == IWEControl.MANUAL) wristState = WristState.MANUAL;
		else if (wristState == WristState.MANUAL) wristState = WristState.CALIBRATING;
		
		//Sync Wrist Position
		if (IWE.getState() == IWEState.IDLE || !ElevatorInterface.isRoughlyAtTargetHeight()) 
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
	}
}