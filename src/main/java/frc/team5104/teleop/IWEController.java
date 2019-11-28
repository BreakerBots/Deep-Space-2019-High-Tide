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

public class IWEController extends TeleopController {
	protected String getName() { return "IWE Controller"; }

	protected void update() {
		//Idle
		if (Controls.IDLE.get()) {
			console.log(c.IWE, "idling");
			Superstructure.setState(SystemState.IDLE);
		}
		
		//Intake & Intake w/ Vision
		if (Controls.IWE_INTAKE.get()) {
			if (Superstructure.getGamePiece() == GamePiece.CARGO) {
				Superstructure.cargoIntakeGround = Superstructure.getState() == SystemState.INTAKE ? !Superstructure.cargoIntakeGround : true;
				console.log(c.IWE, "intaking cargo " + (Superstructure.cargoIntakeGround ? "ground" : "wall"));
			}
			else console.log(c.IWE, "intaking hatch");
			Superstructure.setState(SystemState.INTAKE);
		}
		
		//Eject
		if (Controls.IWE_PLACE_EJECT.get()) {
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
				Controls.IWE_EJECT_RUMBLE.start();
			}
		}
		
		//Game Piece
		if (Controls.IWE_SWITCH_GAME_PIECE.get()) {
			Superstructure.setGamePiece(Superstructure.getGamePiece() == GamePiece.HATCH ? GamePiece.CARGO : GamePiece.HATCH);
			console.log(c.IWE, "switcing game piece to " + Superstructure.getGamePiece().toString().toLowerCase());
			if (Superstructure.getGamePiece() == GamePiece.HATCH)
				Controls.IWE_SWITCH_HATCH_RUMBLE.start();
			else
				Controls.IWE_SWITCH_CARGO_RUMBLE.start();
		}
		
		if (Controls.IWE_CARGO_OP.get()) Superstructure.setGamePiece(GamePiece.CARGO);
		if (Controls.IWE_HATCH_OP.get()) Superstructure.setGamePiece(GamePiece.HATCH);
		
		//Height
		if (Controls.IWE_HEIGHT_L1.get() || Controls.IWE_HEIGHT_L1_OP.get()) {
			console.log(c.IWE, "setting target height to L1");
			Superstructure.setHeight(Height.L1);
			Controls.IWE_SWITCH_HEIGHT_RUMBLE.start();
		}
		if (Controls.IWE_HEIGHT_L2.get() || Controls.IWE_HEIGHT_L2_OP.get()) {
			console.log(c.IWE, "setting target height to L2");
			Superstructure.setHeight(Height.L2);
			Controls.IWE_SWITCH_HEIGHT_RUMBLE.start();
		}
		if (Controls.IWE_HEIGHT_L3.get() || Controls.IWE_HEIGHT_L3_OP.get()) {
			console.log(c.IWE, "setting target height to L3");
			Superstructure.setHeight(Height.L3);
			Controls.IWE_SWITCH_HEIGHT_RUMBLE.start();
		}
		if (Controls.IWE_HEIGHT_SHIP.get() || Controls.IWE_HEIGHT_SHIP_OP.get()) {
			console.log(c.IWE, "setting target height to ship");
			Superstructure.setHeight(Height.SHIP);
			Controls.IWE_SWITCH_HEIGHT_RUMBLE.start();
		}
		
		//Control Mode
		if (Controls.TOGGLE_MANUAL.get()) {
			Superstructure.setControlMode(Superstructure.getControlMode() == ControlMode.AUTONOMOUS ? ControlMode.MANUAL : ControlMode.AUTONOMOUS);
			console.log(c.IWE, "setting control mode to " + Superstructure.getControlMode().toString().toLowerCase());
		}
		Superstructure.desiredWristManaul = Controls.IWE_WRIST_MANUAL.get();
		Superstructure.desiredElevatorManaul = Controls.IWE_ELEVATOR_MANUAL.get();
	}
}
