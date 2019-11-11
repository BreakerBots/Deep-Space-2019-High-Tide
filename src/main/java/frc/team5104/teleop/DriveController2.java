/*BreakerBots Robotics Team 2019*/
package frc.team5104.teleop;

import frc.team5104.main.Controls;
import frc.team5104.statemachines.IWE;
import frc.team5104.statemachines.IWE.IWEState;
import frc.team5104.subsystems.drive.Drive;
import frc.team5104.subsystems.drive.DriveHelper;
import frc.team5104.subsystems.elevator.Elevator;
import frc.team5104.util.managers.TeleopController;
import frc.team5104.vision.VisionManager2;

public class DriveController2 extends TeleopController {
	protected String getName() { return "Drive Controller"; }

	boolean visionEnabled = false;
	
	protected void update() {
		//Enable/Disable Vision
		if (Controls.TOGGLE_VISION.getPressed()) {
			visionEnabled = !visionEnabled;
		}
	
		//Manual Driving
		double turn = Controls.DRIVE_TURN.getAxis();
		double forward = Controls.DRIVE_FORWARD.getAxis() - Controls.DRIVE_REVERSE.getAxis();
		if (Elevator.getEncoderHeight() > 26) {
			forward = DriveHelper.applyKickstandForward(forward);
			turn = DriveHelper.applyKickstandTurn(turn);
		}
		
		//Vision
		if (visionEnabled && IWE.getState() != IWEState.IDLE) {
			turn += VisionManager2.getNextTurnSignal();
		}
		
		//Output
		Drive.set(DriveHelper.get(turn, forward, true));
	}
}
