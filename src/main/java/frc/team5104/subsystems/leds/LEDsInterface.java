package frc.team5104.subsystems.leds;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.LEDChannel;

import frc.team5104.main.Ports;
import frc.team5104.util.managers.Subsystem;

class LEDsInterface extends Subsystem.Interface {

	//Devices
	private CANifier canifier = new CANifier(Ports.LEDS_CANIFIER);
	
	void set(double red, double green, double blue) {
		canifier.setLEDOutput(red, LEDChannel.LEDChannelA);
		canifier.setLEDOutput(green, LEDChannel.LEDChannelB);
		canifier.setLEDOutput(blue, LEDChannel.LEDChannelC);
	}
	
	//Config
	protected void init() {
		canifier.configFactoryDefault();
	}
}
