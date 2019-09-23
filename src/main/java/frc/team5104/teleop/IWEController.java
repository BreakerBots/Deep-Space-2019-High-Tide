package frc.team5104.teleop;

import frc.team5104.main.Controls;
import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEControl;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEHeight;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.util.managers.TeleopController;

public class IWEController extends TeleopController {
	protected String getName() { return "IWE Controller"; }

	protected void update() {
		//IWE State
		if (Controls.IWE_IDLE.getPressed()) {
			IWE.setControl(IWEControl.MANUAL);
		}
		if (Controls.IWE_INTAKE.getPressed()) {
			IWE.setState(IWEState.INTAKE);
		}
		if (Controls.IWE_PLACE_EJECT.getPressed()) {
			if (IWE.getState() == IWEState.IDLE)
				IWE.setState(IWEState.PLACE);
			else if (IWE.getState() != IWEState.EJECT) {
				IWE.setState(IWEState.EJECT);
				Controls.IWE_EJECT_RUMBLE.start();
			}
		}
		
		//IWE Game Piece
		if (Controls.IWE_SWITCH_GAME_PIECE.getPressed()) {
			IWE.setGamePiece(IWE.getGamePiece() == IWEGamePiece.HATCH ? IWEGamePiece.CARGO : IWEGamePiece.HATCH);
			Controls.IWE_SWITCH_GAME_PIECE_RUMBLE.start();
		}
		
		//IWE Height
		if (Controls.IWE_HEIGHT_L1.getPressed()) {
			IWE.setHeight(IWEHeight.L1);
			Controls.IWE_SWITCH_HEIGHT_RUMBLE.start();
		}
		if (Controls.IWE_HEIGHT_L2.getPressed()) {
			IWE.setHeight(IWEHeight.L2);
			Controls.IWE_SWITCH_HEIGHT_RUMBLE.start();
		}
		if (Controls.IWE_HEIGHT_L3.getPressed()) {
			IWE.setHeight(IWEHeight.L3);
			Controls.IWE_SWITCH_HEIGHT_RUMBLE.start();
		}
		
		//IWE Control
		if (Controls.IWE_IDLE.getDoubleClick() == 2) {
			IWE.setControl(IWE.getControl() == IWEControl.AUTONOMOUS ? IWEControl.MANUAL : IWEControl.AUTONOMOUS);
		}
		IWE.desiredWristManaul = Controls.IWE_WRIST_MANUAL.getAxis();
		IWE.desiredElevatorManaul = Controls.IWE_ELEVATOR_MANUAL.getAxis();
	}
}
