package frc.team5104;

import java.lang.reflect.Field;

import frc.team5104.subsystems.Drive;
import frc.team5104.subsystems.Elevator;
import frc.team5104.subsystems.Intake;
import frc.team5104.subsystems.Wrist;
import frc.team5104.util.subsystem.Subsystem;

public class Subsystems {
	public static Drive drive = new Drive();
	public static Elevator elevator = new Elevator();
	public static Intake intake = new Intake();
	public static Wrist wrist = new Wrist();
	
	public static Subsystem[] getSubsystems() {
		Field[] fields = Subsystems.class.getDeclaredFields();
		Subsystem[] subsystems = new Subsystem[fields.length];
		for (int i = 0; i < fields.length; i++) {
			try {
				subsystems[i] = (Subsystem) fields[i].get(null);
			} 
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return subsystems;
	}
}
