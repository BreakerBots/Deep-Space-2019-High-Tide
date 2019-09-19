/*BreakerBots Robotics Team 2019*/
package frc.team5104.main;

import frc.team5104.main.setup.RobotController;
import frc.team5104.main.setup.RobotState;
import frc.team5104.statemachines.StateMachineManager;
import frc.team5104.subsystems.SubsystemManager;
import frc.team5104.teleop.TeleopControllerManager;
import frc.team5104.util.BreakerCompressor;
import frc.team5104.util.Controller;
import frc.team5104.util.WebappTuner;
import frc.team5104.util.Webapp;

public class Robot extends RobotController.BreakerRobot {
	public Robot() {
		SubsystemManager.useSubsystems(
			//new Drive()
			//new Wrist()
			//new Elevator()
			//new Intake()
		);
		StateMachineManager.useStateMachines(
			//new IWE()
		);
		TeleopControllerManager.useTeleopControllers(
			//new DriveController()
			//new IWEController()
			//new CompressorController()
		);
		
		Webapp.init();
		WebappTuner.init();
	}
	
	//Teleop (includes sandstorm)
	public void teleopStart() {
		if (RobotState.isSandstorm()) { /*auto init stuffs*/ }
		
		SubsystemManager.enabled();
		StateMachineManager.enabled();
	}
	public void teleopStop() {
		SubsystemManager.disabled();
		StateMachineManager.disabled();
	}
	public void teleopLoop() {
		if (RobotState.isSandstorm()) { /*auto loop stuffs*/ BreakerCompressor.stop(); }
		else { TeleopControllerManager.update(); }
		
		SubsystemManager.update();
		StateMachineManager.update();
		Controller.handle();
	}
	
	//Test
	public void testLoop() {
		BreakerCompressor.run();
	}
}
