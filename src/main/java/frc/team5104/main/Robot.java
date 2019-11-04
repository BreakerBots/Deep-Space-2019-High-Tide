/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import frc.team5104.auto.AutoManager;
import frc.team5104.auto.paths.RocketDoubleMiddleHatch;
import frc.team5104.main.setup.RobotController;
import frc.team5104.main.setup.RobotState;
import frc.team5104.statemachines.IWE;
import frc.team5104.subsystems.drive.Drive;
import frc.team5104.subsystems.drive.Odometry;
import frc.team5104.subsystems.elevator.Elevator;
import frc.team5104.subsystems.elevator.ElevatorLooper;
import frc.team5104.subsystems.intake.Intake;
import frc.team5104.subsystems.wrist.Wrist;
import frc.team5104.teleop.CompressorController;
import frc.team5104.teleop.DriveController;
import frc.team5104.teleop.IWEController;
import frc.team5104.util.BreakerCompressor;
import frc.team5104.util.Controller;
import frc.team5104.util.WebappTuner;
import frc.team5104.util.managers.StateMachineManager;
import frc.team5104.util.managers.SubsystemManager;
import frc.team5104.util.managers.TeleopControllerManager;
import frc.team5104.vision.Limelight;
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
		StateMachineManager.useStateMachines(
			new IWE()
		);
		TeleopControllerManager.useTeleopControllers(
			new DriveController(),
			new IWEController(),
			new CompressorController()
		);
		
		//Other Initialization
//		CameraServer.getInstance().startAutomaticCapture();
		Webapp.run();
		Odometry.run();
		AutoManager.setTargetPath(new RocketDoubleMiddleHatch());
		Limelight.init();
		
		//Debug Subsystems
		WebappTuner.init(Elevator.class, ElevatorLooper.class);
	}
	
	//Teleop (includes sandstorm)
	public void teleopStart() {
		if (RobotState.isSandstorm()) { Odometry.reset(); AutoManager.run(); }
		else { TeleopControllerManager.enabled(); }
		StateMachineManager.enabled();
		SubsystemManager.enabled();
		BreakerCompressor.stop();
	}
	public void teleopStop() {
		StateMachineManager.enabled();
		SubsystemManager.disabled();
	}
	public void teleopLoop() {
		if (RobotState.isSandstorm()) { BreakerCompressor.stop(); }
		else { TeleopControllerManager.update(); }
		StateMachineManager.update();
		SubsystemManager.update();
	}
	
	//Test
	public void testLoop() { BreakerCompressor.run(); }
	
	//Main
	public void mainLoop() { Controller.handle(); }
}
