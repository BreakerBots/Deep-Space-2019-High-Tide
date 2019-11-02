/*BreakerBots Robotics Team 2019*/
package frc.team5104.teleop;

import frc.team5104.main.Constants;
import frc.team5104.main.Controls;
import frc.team5104.subsystems.drive.Drive;
import frc.team5104.subsystems.drive.DriveHelper;
import frc.team5104.util.managers.TeleopController;
import frc.team5104.vision.VisionManager;

public class DriveController extends TeleopController {
	protected String getName() { return "Drive Controller"; }

	//State between Manual Control/Vision
	private boolean inVision = false;
	
	protected void update() {
		//Switch between Manual Control and Vision
		if (Controls.TOGGLE_VISION.getPressed()) {
			inVision = !inVision;
			if (inVision) VisionManager.init();
			else VisionManager.end();
		}
		
		//Enter Vision (Alt)
		if (Controls.IWE_INTAKE_WITH_VISION.getPressed()) {
			inVision = true;
			VisionManager.init();
		}
		
		//Exit Vision (Alts)
		if (isAgressivelyTryingToExitVision() || Controls.IDLE.getPressed()) {
			inVision = false;
			VisionManager.end();
		}
		
		//Update Manual Drive or Vision
		if (inVision)
			handleVisionDrive();
		else
			handleManualDrive();
	}
	
	//Manual Driving
	private void handleManualDrive() {
		double turn = Controls.DRIVE_TURN.getAxis();
		double forward = Controls.DRIVE_FORWARD.getAxis() - Controls.DRIVE_REVERSE.getAxis();
		
		//kickstand
//		if (IWE.getState() == IWEState.PLACE && IWE.getHeight() != IWEHeight.L1) {
//			forward = DriveHelper.applyKickstandForward(forward);
//			turn = DriveHelper.applyKickstandTurn(turn);
//		}
		
		Drive.set(DriveHelper.get(turn, forward, true));
	}
	
	//Vision Driving
	private void handleVisionDrive() {
		Drive.set(VisionManager.getNextDriveSignal());
		
		if (VisionManager.isFinished()) {
			inVision = false;
			VisionManager.end();
		}
	}
	
	//Aggressively Exit Vision :)
	private boolean isAgressivelyTryingToExitVision() {
		return (
			(Math.abs(Controls.DRIVE_TURN.getAxis()) > Constants.DRIVE_AGGR_EXIT_VISION_THRESHOLD) ||
			(Math.abs(Controls.DRIVE_FORWARD.getAxis()) > Constants.DRIVE_AGGR_EXIT_VISION_THRESHOLD) ||
			(Math.abs(Controls.DRIVE_REVERSE.getAxis()) > Constants.DRIVE_AGGR_EXIT_VISION_THRESHOLD)
		);
	}
}
