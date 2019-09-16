/*BreakerBots Robotics Team 2019*/
package frc.team5104.module.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.main.Ports;
import frc.team5104.main.RobotState;
import frc.team5104.module.Module;
import frc.team5104.module.drive.DriveSignal.DriveUnit;
import frc.team5104.util.TalonFactory;
import frc.team5104.util.console;
import frc.team5104.util.TalonFactory.TalonSettings;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;

class DriveSystems extends Module.Systems {
	//Talons
	private static TalonSRX Talon_L1;
	private static TalonSRX Talon_L2;
	private static TalonSRX Talon_R1;
	private static TalonSRX Talon_R2;
	
	//Module Attached
	public boolean isModuleAttached() {
		Talon_L1 = TalonFactory.getTalon(Ports.DRIVE_TALON_L1);
		return Talon_L1.getFirmwareVersion() > 0;
	}
	
	//Motors
	static class motors {
		public static void set(DriveSignal signal) {
			switch (signal.unit) {
				case percentOutput: {
					set(
							signal.leftSpeed, 
							signal.rightSpeed, 
							ControlMode.PercentOutput
						);
					break;
				}
				case voltage: {
					set(
							signal.leftSpeed / DriveSystems.motors.getLeftBusVoltage(),
							signal.rightSpeed / DriveSystems.motors.getRightBusVoltage(),
							ControlMode.PercentOutput
						);
				}
			}
		}
		
		public static void stop() {
			Talon_L1.set(ControlMode.Disabled, 0);
			Talon_R1.set(ControlMode.Disabled, 0);
		}
		
		public static void set(double leftSpeed, double rightSpeed, ControlMode mode) {
			if (RobotState.gotDriverStationResponse()) {
				Talon_L1.set(mode, leftSpeed);
				Talon_R1.set(mode, rightSpeed);
			}
			else {
				//Motor Safety
				stop();
			}
		}
		
		public static double getLeftBusVoltage() { return Talon_L1.getBusVoltage(); }
		public static double getRightBusVoltage() { return Talon_R1.getBusVoltage(); }
	}
	
	//Gyro
	public static class gyro {
		public static double getAngle() {
			return -1;
		}
		
		public static void reset(int timeoutMs) {

		}
	}
	
	//Setup
	protected void init() {
		//Initialize Talons
		Talon_L1 = TalonFactory.getTalon(Ports.DRIVE_TALON_L1, new TalonSettings(NeutralMode.Brake, false, DriveConstants.CURRENT_LIMIT, true));
		Talon_L2 = TalonFactory.getTalon(Ports.DRIVE_TALON_L2, new TalonSettings(NeutralMode.Brake, false, DriveConstants.CURRENT_LIMIT, true));
		Talon_R1 = TalonFactory.getTalon(Ports.DRIVE_TALON_R1, new TalonSettings(NeutralMode.Brake, true, DriveConstants.CURRENT_LIMIT, true));
		Talon_R2 = TalonFactory.getTalon(Ports.DRIVE_TALON_R2, new TalonSettings(NeutralMode.Brake, true, DriveConstants.CURRENT_LIMIT, true));
		
		//Wait until Talons are Ready to Recieve
		try {
			Thread.sleep(10);
		} catch (Exception e) { console.error(e); }
		
		// Left Talons Config
		Talon_L2.set(ControlMode.Follower, Talon_L1.getDeviceID());
        
        // Right Talons Config
        Talon_R2.set(ControlMode.Follower, Talon_R1.getDeviceID());
        
		//Stop the motors
		motors.stop();
		
		//Reset Gyro
		gyro.reset(10);
		
		//Make Sure Everything is caught up :)
		try { Thread.sleep(100); } catch (Exception e) { console.error(e); }
		
		console.log(c.DRIVE, t.INFO, "Initialized");
	}
}