package org.cobweb.cobweb2.plugins.learning;

import org.cobweb.io.ConfDisplayName;
import org.cobweb.io.ConfXMLTag;
import org.cobweb.io.ParameterSerializable;
import org.cobweb.util.CloneHelper;
import org.cobweb.util.MutatableFloat;
import org.cobweb.util.MutatableInt;


public class LearningAgentParams implements ParameterSerializable {


	@ConfXMLTag("enabled")
	@ConfDisplayName("Enable learning")
	public boolean learningEnabled = false;

	@ConfXMLTag("learningMemoryDuration")
	@ConfDisplayName("Memory duration")
	public MutatableInt memorySteps = new MutatableInt(20);

	@ConfXMLTag("learningCycle")
	@ConfDisplayName("Learning cycle")
	public MutatableInt learningCycle = new MutatableInt(10);

	@ConfXMLTag("learningWeighting")
	@ConfDisplayName("Recent event bias")
	public MutatableFloat weighting = new MutatableFloat(0.1f);

	@ConfXMLTag("learningAdjustmentStrength")
	@ConfDisplayName("Learning adjustment strength")
	public MutatableFloat adjustmentStrength = new MutatableFloat(0.3f);

	public LearningAgentParams() {
	}

	@Override
	public LearningAgentParams clone() {
		try {
			LearningAgentParams copy = (LearningAgentParams) super.clone();
			CloneHelper.resetMutatable(copy);
			return copy;
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static final long serialVersionUID = 1L;
}
