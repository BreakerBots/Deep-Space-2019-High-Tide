/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.teleop;

import frc.team5104.main.Controls;
import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEGamePiece;
import frc.team5104.statemachines.IWE.IWEHeight;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.subsystems.drive.Drive;
import frc.team5104.subsystems.drive.DriveHelper;
import frc.team5104.util.managers.TeleopController;
import frc.team5104.vision.VisionManager;

public class DriveController extends TeleopController {
	protected String getName() { return "Drive Controller"; }

	boolean visionEnabled = false;
	protected void update() {
		//Switch between Manual Control and Vision
		if (Controls.TOGGLE_VISION.get())
			visionEnabled = !visionEnabled;
		
		//Update Manual Drive or Vision
		if ((IWE.getState() == IWEState.INTAKE ||
			((IWE.getState() == IWEState.PLACE || IWE.getState() == IWEState.EJECT) && !(IWE.getGamePiece() == IWEGamePiece.CARGO && IWE.getHeight() == IWEHeight.SHIP)) ||
			IWE.getState() == IWEState.PLACE_READY) && visionEnabled)
			handleVisionDrive();
		else
			handleManualDrive();
	}
	
	//Manual Driving
	private void handleManualDrive() {
		double forward = Controls.DRIVE_FORWARD.get() - Controls.DRIVE_REVERSE.get();
		Controls.DRIVE_TURN.changeCurveX1(DriveHelper.getTurnAdjust(forward));
		double turn = Controls.DRIVE_TURN.get();
		Drive.set(DriveHelper.get(turn, forward, true));
	}
	
	//Vision Driving
	private void handleVisionDrive() {
		if (!VisionManager.isInVision())
			VisionManager.start();
		Drive.set(VisionManager.getNextDriveSignal());
		if (VisionManager.isFinished())
			VisionManager.end();
	}
}
