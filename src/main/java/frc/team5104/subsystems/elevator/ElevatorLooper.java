package frc.team5104.subsystems.elevator;

import frc.team5104.main.Constants;
import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEControl;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEHeight;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.subsystems.canifier.CANifier;
import frc.team5104.util.Buffer;
import frc.team5104.util.WebappTuner.tunerInput;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.Subsystem;

public class ElevatorLooper extends Subsystem.Looper {
	//Enums
	static enum ElevatorState { CALIBRATING, MANUAL, AUTONOMOUS };
	static enum ElevatorPosition { 
		BOTTOM(-4), 
		CARGO_SHIP(24), CARGO_WALL(20), CARGO_L1(4), CARGO_L2(28), CARGO_L3(52), 
		HATCH_L2(26), HATCH_L3(52);
		public double height; private ElevatorPosition(double height) { this.height = height; }
	}
	ElevatorState elevatorState;
	ElevatorPosition elevatorPosition;
	ElevatorState lastElevatorState;
	ElevatorPosition lastElevatorPosition;
	long elevatorStateStartTime = System.currentTimeMillis();
	long elevatorPositionStartTime = System.currentTimeMillis();
	private Buffer limitSwitchZeroBuffer = new Buffer(5, false);
	private Buffer averageMotorOutput = new Buffer(300, 0.0);
	
	//tuner heights
	@tunerInput
	static double CARGO_SHIP = ElevatorPosition.CARGO_SHIP.height; 
	@tunerInput
	static double CARGO_WALL = ElevatorPosition.CARGO_WALL.height;
	@tunerInput
	static double CARGO_L1 = ElevatorPosition.CARGO_L1.height;
	@tunerInput
	static double CARGO_L2 = ElevatorPosition.CARGO_L2.height;
	@tunerInput
	static double CARGO_L3 = ElevatorPosition.CARGO_L3.height;
	@tunerInput
	static double HATCH_L2 = ElevatorPosition.HATCH_L2.height;
	@tunerInput
	static double HATCH_L3 = ElevatorPosition.HATCH_L3.height;
	
	//Loop
	protected void update() {
		//tuner heights
		ElevatorPosition.CARGO_SHIP.height = CARGO_SHIP;
		ElevatorPosition.CARGO_WALL.height = CARGO_WALL;
		ElevatorPosition.CARGO_L1.height = CARGO_L1;
		ElevatorPosition.CARGO_L2.height = CARGO_L2;
		ElevatorPosition.CARGO_L3.height = CARGO_L3;
		ElevatorPosition.HATCH_L2.height = HATCH_L2;
		ElevatorPosition.HATCH_L3.height = HATCH_L3;
		
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
		
		//Time Tracking For State/Position
		if (lastElevatorPosition != elevatorPosition)
			elevatorPositionStartTime = System.currentTimeMillis();
		if (lastElevatorState != elevatorState)
			elevatorStateStartTime = System.currentTimeMillis();
		
		//Recalibrate During Runtime
//		if (elevatorState == ElevatorState.AUTONOMOUS && elevatorPosition == ElevatorPosition.BOTTOM &&
//			!CANifier.elevatorLowerLimitHit() && System.currentTimeMillis() > elevatorPositionStartTime + 6000) {
//			console.warn(c.ELEVATOR, "Recalibrating Elevator");
//			elevatorState = ElevatorState.CALIBRATING;
//		}
		
		//Control Elevator
		if (elevatorState == ElevatorState.AUTONOMOUS) {
			//Auto
			Elevator._interface.setMotionMagic(elevatorPosition.height);
			
			//Error Catch
			//if (giving neg output for too long) { stop(); }
			//if (giving pos output for too long) { stop(); }
		}
		else if (elevatorState == ElevatorState.CALIBRATING) {
			//Calibrating
			if (!CANifier.elevatorLowerLimitHit())
				Elevator._interface.setPercentOutput(-Constants.ELEVATOR_CALIBRATE_SPEED);
			else {
				elevatorState = ElevatorState.AUTONOMOUS;
				Elevator._interface.resetEncoder();
			}
			
			//Error Catch
			if (System.currentTimeMillis() > elevatorStateStartTime + 6000) {
				console.error(c.ELEVATOR, "WTF!!!! Calibration Error (Entering Manual)");
				IWE.setControl(IWEControl.MANUAL);
			}
		}
		else {
			//Manual
			Elevator._interface.setPercentOutput(IWE.desiredElevatorManaul);
//			System.out.println(Elevator._interface.getRawEncoderVelocity());
		}
		
		//Zero Encoder In Runtime
		limitSwitchZeroBuffer.update(CANifier.elevatorLowerLimitHit());
		if (limitSwitchZeroBuffer.getBooleanOutput())
			Elevator._interface.resetEncoder();
			
		//Other
		lastElevatorPosition = elevatorPosition;
		lastElevatorState = elevatorState;
		averageMotorOutput.update(Elevator._interface.getMotorPercentOutput());
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
