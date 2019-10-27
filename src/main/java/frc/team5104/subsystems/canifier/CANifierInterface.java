package frc.team5104.subsystems.canifier;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.GeneralPin;
import com.ctre.phoenix.CANifier.LEDChannel;

import frc.team5104.main.Ports;
import frc.team5104.util.managers.Subsystem;

class CANifierInterface extends Subsystem.Interface {

	//Devices
	private CANifier canifier = new CANifier(Ports.LEDS_CANIFIER);
	
	void set(double red, double green, double blue) {
		canifier.setLEDOutput(red, LEDChannel.LEDChannelA);
		canifier.setLEDOutput(green, LEDChannel.LEDChannelB);
		canifier.setLEDOutput(blue, LEDChannel.LEDChannelC);
	}
	
	boolean revLimitHit() {
		return canifier.getGeneralInput(GeneralPin.LIMR);
	}
	
	//Config
	protected void init() {
		canifier.configFactoryDefault();
	}
}
