/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.teleop;

import frc.team5104.Controls;
import frc.team5104.subsystems.Drive;
import frc.team5104.util.DriveHelper;
import frc.team5104.util.managers.TeleopController;

public class DriveController extends TeleopController {
	protected String getName() { return "Drive-Controller"; }

	boolean visionEnabled = false;
	protected void update() {
		//Switch between Manual Control and Vision
		if (Controls.TOGGLE_VISION.get())
			visionEnabled = !visionEnabled;
		
		//Update Manual Drive or Vision
//		if ((Superstructure.getMode() == Mode.INTAKE ||
//			((Superstructure.getMode() == Mode.PLACE || Superstructure.getMode() == Mode.EJECT) && !(Superstructure.getGamePiece() == GamePiece.CARGO && Superstructure.getHeight() == Height.SHIP)) ||
//			Superstructure.getMode() == Mode.PLACE_READY) && visionEnabled)
//			handleVisionDrive();
//		else {
			handleManualDrive();
//			if (VisionManager.isFinished())
//				VisionManager.end();
//		}
	}
	
	//Manual Driving
	private void handleManualDrive() {
		double forward = Controls.DRIVE_FORWARD.get() - Controls.DRIVE_REVERSE.get();
		Controls.DRIVE_TURN.changeCurveX1(DriveHelper.getTurnAdjust(forward));
		double turn = Controls.DRIVE_TURN.get();
		Drive.set(DriveHelper.get(turn, forward, true));
	}
	
	//Vision Driving
//	private void handleVisionDrive() {
//		if (!VisionManager.isInVision())
//			VisionManager.start();
//		Drive.set(VisionManager.getNextDriveSignal());
//	}
}
