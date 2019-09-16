/*BreakerBots Robotics Team 2019*/
package frc.team5104.module.tshirt;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;
import frc.team5104.main.Ports;
import frc.team5104.main.RobotState;
import frc.team5104.module.Module;
import frc.team5104.util.TalonFactory;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;

class TShirtSystems extends Module.Systems {
	
	//Talons
	private static TalonSRX Talon_Turret;
	private static TalonSRX Talon_Pitch;
	//private static TalonSRX Talon_Revolver;
	private static Solenoid Valve;
	
	//Module Attached
	public boolean isModuleAttached() {
		Talon_Turret = TalonFactory.getTalon(Ports.TSHIRT_TALON_TURRET);
		return Talon_Turret.getFirmwareVersion() > 0;
	}
	
	static class valve {
		static enum ValveState {
			FILL,
			EXHAUST
		};
		static ValveState state = ValveState.EXHAUST;
		
		static void toggleState() {
			if (state == ValveState.FILL) state = ValveState.EXHAUST;
			else state = ValveState.FILL;
			update();
		}

		static void setState(ValveState valve_state) {
			state = valve_state;
			update();
		}
		
		static void update() {
			if (state == ValveState.FILL) Valve.set(true);
			else Valve.set(false);
		}
	}
	
	static class turret {
		static void setSpeed(double voltage) {
			if (RobotState.gotDriverStationResponse()) {
				Talon_Turret.set(ControlMode.PercentOutput, voltage/Talon_Turret.getBusVoltage());
			}
			else {
				//Motor Safety
				stop();
			}
		}
		static void stop() { 
			Talon_Turret.set(ControlMode.Disabled, 0);
		}
		
		static double getRotation() {
			return Talon_Turret.getSelectedSensorPosition();
		}
		
		static boolean leftLimitHit() {
			return true;
		}
		
		static boolean rightLimitHit() {
			return true;
		}
	}
	
	static class pitch {
		static void setSpeed(double voltage) {
			if (RobotState.gotDriverStationResponse()) {
				Talon_Pitch.set(ControlMode.PercentOutput, voltage/Talon_Pitch.getBusVoltage());
			}
			else {
				//Motor Safety
				stop();
			}
		}
		static void stop() {
			Talon_Pitch.set(ControlMode.Disabled, 0);
		}
		
		static double getRotation() {
			return -1;
		}
		
		static boolean lowerLimitHit() {
			return true;
		}
		
		static boolean upperLimitHit() {
			return true;
		}
	}
	
	static class pressureSensor {
		static double getRawPressure() {
			return Talon_Pitch.getSelectedSensorPosition();
		}
		
		static double getPressure() {
			return getRawPressure() * .248842 - 26.8749;
		}
		
		//static double getRawPressureVelocity() {
			//return Talon_Pitch.getSelectedSensorVelocity() * 10 /* Convert PSI/100ms to PSI/sec */;
		//}
		
		//static double getPressureVelocity() {
			//return getRawPressureVelocity() * .248842 - 26.8749;
		//}
	}
	
	//static class revolver {}
	
	static class RSL {
		enum RSLMode {
			DISABLED,
			ENABLED,
			FILLING,
		}
		private static RSLMode currentMode = RSLMode.DISABLED;
		private static double currentDemand = 1.0;
		
		static void set(RSLMode mode, double demand) {
			currentMode = mode;
			currentDemand = demand;
		}
		
		static RSLMode get() {
			return currentMode;
		}
		
		static void update() {
			switch (currentMode) {
				case DISABLED: {
					//disabled
					break;
				}
				case ENABLED: {
					//enabled
					break;
				}
				case FILLING: {
					//filling
					break;
				}
			}
		}
	}
	
	protected void init() {
		Valve = new Solenoid(Ports.TSHIRT_VALVE);
		
		Talon_Turret = TalonFactory.getTalon(Ports.TSHIRT_TALON_TURRET);
		Talon_Pitch = TalonFactory.getTalon(Ports.TSHIRT_TALON_PITCH);
		
		Talon_Pitch.configSelectedFeedbackSensor(FeedbackDevice.Analog);
		Talon_Pitch.configSensorTerm(SensorTerm.Diff0, FeedbackDevice.Analog);
		
		//Talon_Revolver = TalonFactory.getTalon...
		
		console.log(c.TSHIRT, t.INFO, "Initialized");
	}
}