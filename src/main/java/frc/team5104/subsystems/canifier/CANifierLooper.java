package frc.team5104.subsystems.canifier;

import frc.team5104.subsystems.canifier.CANifier.LEDsMode;
import frc.team5104.util.managers.Subsystem;
import frc.team5104.util.managers.SubsystemManager.DebugMessage;

class CANifierLooper extends Subsystem.Looper {

	LEDsMode ledmode = LEDsMode.NONE;
	
	//Loop
	protected void update() {
		CANifier._interface.set(1, 1, 1);
	}

	//Stop Everything
	protected void disabled() {  }
	protected void enabled() { }
	
	//Debug
	protected DebugMessage debug() { return new DebugMessage(); }
}
