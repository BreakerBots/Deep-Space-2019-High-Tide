/*BreakerBots Robotics Team 2019*/
package frc.team5104;

import frc.team5104.subsystems.Elevator;
import frc.team5104.teleop.CompressorController;
import frc.team5104.teleop.DriveController;
import frc.team5104.teleop.SuperstructureController;
import frc.team5104.util.WebappTuner;
import frc.team5104.util.XboxController;
import frc.team5104.util.console;
import frc.team5104.util.setup.RobotController;
import frc.team5104.util.setup.RobotState;
import frc.team5104.util.subsystem.SubsystemManager;
import frc.team5104.vision.Limelight;
import frc.team5104.util.TeleopControllerManager;
import frc.team5104.util.Webapp;

public class Robot extends RobotController.BreakerRobot {
	public Robot() {
		//Managers
		SubsystemManager.useSubsystems(
			Subsystems.getSubsystems()
		);
		TeleopControllerManager.useTeleopControllers(
			new DriveController(),
			new SuperstructureController(),
			new CompressorController()
		);
		
		//Other Initialization
		Webapp.run();
		Limelight.init();
		CompressorController.stop();
		WebappTuner.init(Constants.class, Elevator.class);
	}
	
	//Teleop (includes sandstorm)
	public void teleopStart() {
		console.logFile.start();
		TeleopControllerManager.reset();
		Superstructure.reset();
		SubsystemManager.reset();
	}
	public void teleopStop() {
		TeleopControllerManager.reset();
		Superstructure.reset();
		SubsystemManager.reset();
		console.logFile.end();
	}
	public void teleopLoop() {
		if (RobotState.isSandstorm())
			CompressorController.stop();
		else TeleopControllerManager.update(); 
		Superstructure.update();
		SubsystemManager.update();
	}
	
	//Test
	public void testLoop() {
		SubsystemManager.stopAll();
		CompressorController.start(); 
	}
	
	//Main
	public void mainLoop() { 
		XboxController.update(); 
	}
}
