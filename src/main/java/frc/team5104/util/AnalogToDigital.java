package frc.team5104.util;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogToDigital {
	private AnalogInput analogInput;
	
	public AnalogToDigital(int port) { 
		analogInput = new AnalogInput(port); 
	}
	
	public boolean get() {
		return analogInput.getVoltage() > 2.5;
	}
}