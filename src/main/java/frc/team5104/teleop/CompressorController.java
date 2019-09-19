package frc.team5104.teleop;

import frc.team5104.main.Controls;
import frc.team5104.util.BreakerCompressor;

public class CompressorController extends TeleopController {
	protected String getName() { return "IWE Controller"; }

	protected void update() {
		if (Controls.COMPRESSOR_TOGGLE.getPressed()) {
			if (BreakerCompressor.isRunning())
				BreakerCompressor.stop();
			else
				BreakerCompressor.run();
		}
	}
}
