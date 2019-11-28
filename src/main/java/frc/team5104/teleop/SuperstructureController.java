package frc.team5104.teleop;

import frc.team5104.main.Controls;
import frc.team5104.main.Superstructure;
import frc.team5104.main.Superstructure.ControlMode;
import frc.team5104.main.Superstructure.GamePiece;
import frc.team5104.main.Superstructure.Height;
import frc.team5104.main.Superstructure.SystemState;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.managers.TeleopController;

public class SuperstructureController extends TeleopController {
	protected String getName() { return "Superstructure-Controller"; }

	protected void update() {
		//Idle
		if (Controls.IDLE.get()) {
			console.log(c.IWE, "idling");
			Superstructure.setState(SystemState.IDLE);
		}
		
		//Intake & Intake w/ Vision
		if (Controls.INTAKE.get()) {
			if (Superstructure.getGamePiece() == GamePiece.CARGO) {
				Superstructure.cargoIntakeGround = Superstructure.getState() == SystemState.INTAKE ? !Superstructure.cargoIntakeGround : true;
				console.log(c.IWE, "intaking cargo " + (Superstructure.cargoIntakeGround ? "ground" : "wall"));
			}
			else console.log(c.IWE, "intaking hatch");
			Superstructure.setState(SystemState.INTAKE);
		}
		
		//Eject
		if (Controls.PLACE_EJECT.get()) {
			if (Superstructure.getState() == SystemState.IDLE) {
				if (Superstructure.getHeight() == Height.L2 || Superstructure.getHeight() == Height.L3) {
					console.log(c.IWE, "preparing to place " + Superstructure.getGamePiece().name().toLowerCase() + " at " + Superstructure.getHeight().name().toLowerCase());
					Superstructure.setState(SystemState.PLACE_READY);
				}
				else {
					console.log(c.IWE, "placing " + Superstructure.getGamePiece().name().toLowerCase() + " at " + Superstructure.getHeight().name().toLowerCase());
					Superstructure.setState(SystemState.PLACE);
				}
			}
			else if (Superstructure.getState() == SystemState.PLACE_READY) {
				console.log(c.IWE, "placing " + Superstructure.getGamePiece().name().toLowerCase() + " at " + Superstructure.getHeight().name().toLowerCase());
				Superstructure.setState(SystemState.PLACE);
			}
			else if (Superstructure.getState() == SystemState.PLACE) {
				console.log(c.IWE, "ejecting " + Superstructure.getGamePiece().name().toLowerCase() + " at " + Superstructure.getHeight().name().toLowerCase());
				Superstructure.setState(SystemState.EJECT);
				Controls.EJECT_RUMBLE.start();
			}
		}
		
		//Game Piece
		if (Controls.SWITCH_GAME_PIECE.get()) {
			Superstructure.setGamePiece(Superstructure.getGamePiece() == GamePiece.HATCH ? GamePiece.CARGO : GamePiece.HATCH);
			console.log(c.IWE, "switcing game piece to " + Superstructure.getGamePiece().toString().toLowerCase());
			if (Superstructure.getGamePiece() == GamePiece.HATCH)
				Controls.SWITCH_HATCH_RUMBLE.start();
			else
				Controls.SWITCH_CARGO_RUMBLE.start();
		}
		if (Controls.CARGO_OP.get()) 
			Superstructure.setGamePiece(GamePiece.CARGO);
		if (Controls.HATCH_OP.get()) 
			Superstructure.setGamePiece(GamePiece.HATCH);
		
		//Height
		if (Controls.HEIGHT_L1.get() || Controls.HEIGHT_L1_OP.get()) {
			console.log(c.IWE, "setting target height to L1");
			Superstructure.setHeight(Height.L1);
			Controls.SWITCH_HEIGHT_RUMBLE.start();
		}
		if (Controls.HEIGHT_L2.get() || Controls.HEIGHT_L2_OP.get()) {
			console.log(c.IWE, "setting target height to L2");
			Superstructure.setHeight(Height.L2);
			Controls.SWITCH_HEIGHT_RUMBLE.start();
		}
		if (Controls.HEIGHT_L3.get() || Controls.HEIGHT_L3_OP.get()) {
			console.log(c.IWE, "setting target height to L3");
			Superstructure.setHeight(Height.L3);
			Controls.SWITCH_HEIGHT_RUMBLE.start();
		}
		if (Controls.HEIGHT_SHIP.get() || Controls.HEIGHT_SHIP_OP.get()) {
			console.log(c.IWE, "setting target height to ship");
			Superstructure.setHeight(Height.SHIP);
			Controls.SWITCH_HEIGHT_RUMBLE.start();
		}
		
		//Control Mode
		if (Controls.TOGGLE_MANUAL.get()) {
			Superstructure.setControlMode(Superstructure.getControlMode() == ControlMode.AUTONOMOUS ? ControlMode.MANUAL : ControlMode.AUTONOMOUS);
			console.log(c.IWE, "setting control mode to " + Superstructure.getControlMode().toString().toLowerCase());
		}
		Superstructure.desiredWristManaul = Controls.WRIST_MANUAL.get();
		Superstructure.desiredElevatorManaul = Controls.ELEVATOR_MANUAL.get();
	}
}
