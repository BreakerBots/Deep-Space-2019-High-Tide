package frc.team5104.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

public class Limelight {
	private static NetworkTable table;
	public static NetworkTableEntry getEntry(String key) { return table.getEntry(key); }
	public static void setEntry(String key, double entry) { table.getEntry(key).setDouble(entry); }

	public static double getTargetX() { return getEntry("tx").getDouble(5104); }
	public static double getTargetY() { return getEntry("ty").getDouble(5104); }
	public static boolean hasTarget() { return getEntry("tv").getDouble(0) == 1; }
	public static boolean isConnected() { return getEntry("tl").getDouble(0) == 0.0; }
	
	public static enum LEDMode { OFF(1), ON(3), BLINK(2); int value; private LEDMode(int value) { this.value = value; } }
	public static void setLEDMode(LEDMode ledMode) { 
		if (isConnected())
			setEntry("ledMode", ledMode.value);  
		else console.warn(c.VISION, "limelight is not connected");
	}
	
	public static void init() {
		table = NetworkTableInstance.getDefault().getTable("limelight");
		if (isConnected()) {
			setLEDMode(LEDMode.OFF);
			setEntry("camMode", 0);
			setEntry("pipeline", 0);
			setEntry("stream", 0);
			setEntry("snapshot", 0);
		}
		else console.warn(c.VISION, "limelight is not connected");
	}
}
