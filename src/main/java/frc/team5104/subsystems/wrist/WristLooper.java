package frc.team5104.subsystems.wrist;

import frc.team5104.subsystems.Subsystem;
import frc.team5104.subsystems.wrist.Wrist.WristPosition;
import frc.team5104.subsystems.wrist.Wrist.WristState;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;

class WristLooper extends Subsystem.Looper {
	
	//States/Variables
	WristState wristState = WristState.CALIBRATING;
	WristPosition wristPosition = WristPosition.BACK;
	
	
	
	
	//Loop
	protected void update() {
		switch (wristState) {
			case AUTONOMOUS:
				//
				break;
			case CALIBRATING:
				//Calibrating - Slowly Drive Back Until Limit Switch Hit
				if (!Wrist._interface.lowerLimitSwitchHit())
					Wrist._interface.setPercentOutput(0.1);
				else
					wristState = WristState.AUTONOMOUS;
				break;
			case MANUAL:
				//Manual - Do Nothing
				break;
			default: console.log(c.WRIST, t.ERROR, "Unknown State Error"); break;
		}
	}

	//Stop Everything
	protected void disabled() { 
		wristState = WristState.CALIBRATING;
		wristPosition = WristPosition.BACK;
	}
	protected void enabled() {
		wristState = WristState.CALIBRATING;
		wristPosition = WristPosition.BACK;
	}
}
