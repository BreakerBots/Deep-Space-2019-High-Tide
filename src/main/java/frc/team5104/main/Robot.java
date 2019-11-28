/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import frc.team5104.auto.util.Odometry;
import frc.team5104.main.setup.RobotController;
import frc.team5104.subsystems.drive.Drive;
import frc.team5104.subsystems.elevator.Elevator;
import frc.team5104.subsystems.intake.Intake;
import frc.team5104.subsystems.wrist.Wrist;
import frc.team5104.teleop.CompressorController;
import frc.team5104.teleop.DriveController;
import frc.team5104.teleop.IWEController;
import frc.team5104.util.BreakerCompressor;
import frc.team5104.util.WebappTuner;
import frc.team5104.util.XboxController;
import frc.team5104.util.managers.SubsystemManager;
import frc.team5104.util.managers.TeleopControllerManager;
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
			new IWEController(),
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
	public void testLoop() { BreakerCompressor.run(); }
	
	//Main
	public void mainLoop() { XboxController.update(); }
}
