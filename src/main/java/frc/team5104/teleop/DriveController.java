/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.teleop;

import frc.team5104.main.Controls;
import frc.team5104.main.Superstructure;
import frc.team5104.main.Superstructure.GamePiece;
import frc.team5104.main.Superstructure.Height;
import frc.team5104.main.Superstructure.SystemState;
import frc.team5104.subsystems.drive.Drive;
import frc.team5104.subsystems.drive.DriveHelper;
import frc.team5104.util.managers.TeleopController;
import frc.team5104.vision.VisionManager;

public class DriveController extends TeleopController {
	protected String getName() { return "Drive-Controller"; }

	boolean visionEnabled = false;
	protected void update() {
		//Switch between Manual Control and Vision
		if (Controls.TOGGLE_VISION.get())
			visionEnabled = !visionEnabled;
		
		//Update Manual Drive or Vision
		if ((Superstructure.getState() == SystemState.INTAKE ||
			((Superstructure.getState() == SystemState.PLACE || Superstructure.getState() == SystemState.EJECT) && !(Superstructure.getGamePiece() == GamePiece.CARGO && Superstructure.getHeight() == Height.SHIP)) ||
			Superstructure.getState() == SystemState.PLACE_READY) && visionEnabled)
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
