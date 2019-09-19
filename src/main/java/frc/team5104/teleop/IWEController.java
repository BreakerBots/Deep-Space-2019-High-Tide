package frc.team5104.teleop;

import frc.team5104.main.Controls;
import frc.team5104.subsystems.wrist.Wrist;

public class IWEController extends TeleopController {
	protected String getName() { return "IWE Controller"; }

	boolean auto = false;
	protected void update() {
		if (Controls.IWE_IDLE.getPressed())
			auto = !auto;
		
		if (auto) {
			
		}
		else {
			Wrist.setPercentOutput(Controls.IWE_WRIST_MANUAL.getAxis());
		}
	}
}
