/*BreakerBots Robotics Team 2019*/
package frc.team5104;

import frc.team5104.Superstructure.SystemState;
import frc.team5104.auto.util.Odometry;
import frc.team5104.subsystems.Drive;
import frc.team5104.subsystems.Elevator;
import frc.team5104.subsystems.Intake;
import frc.team5104.subsystems.Wrist;
import frc.team5104.teleop.CompressorController;
import frc.team5104.teleop.DriveController;
import frc.team5104.teleop.SuperstructureController;
import frc.team5104.util.BreakerCompressor;
import frc.team5104.util.WebappTuner;
import frc.team5104.util.XboxController;
import frc.team5104.util.managers.SubsystemManager;
import frc.team5104.util.managers.TeleopControllerManager;
import frc.team5104.util.setup.RobotController;
import frc.team5104.vision.Limelight;
import frc.team5104.vision.VisionManager;
import frc.team5104.util.Webapp;

public class Robot extends RobotController.BreakerRobot {
	public Robot() {
		//Managers
		SubsystemManager.useSubsystems(
			new Drive(),
			new Wrist(),
			new Elevator(),
			new Intake()
		);
		TeleopControllerManager.useTeleopControllers(
			new DriveController(),
			new SuperstructureController(),
			new CompressorController()
		);
		
		//Other Initialization
		Webapp.run();
		Odometry.run();
		Limelight.init();
		BreakerCompressor.stop();
		WebappTuner.init(VisionManager.class);
	}
	
	//Teleop (includes sandstorm)
	public void teleopStart() {
		TeleopControllerManager.enabled();
		Superstructure.enabled();
		SubsystemManager.enabled();
	}
	public void teleopStop() {
		TeleopControllerManager.disabled();
		Superstructure.enabled();
		SubsystemManager.disabled();
	}
	public void teleopLoop() {
		TeleopControllerManager.update();
		Superstructure.update();
		SubsystemManager.update();
	}
	
	//Test
	public void testLoop() {
		Superstructure.setSystemState(SystemState.DISABLED);
		Drive.stop();
		SubsystemManager.update();
		BreakerCompressor.run(); 
	}
	
	//Main
	public void mainLoop() { XboxController.update(); }
}
