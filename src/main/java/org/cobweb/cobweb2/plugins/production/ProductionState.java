package org.cobweb.cobweb2.plugins.production;

import org.cobweb.cobweb2.plugins.AgentState;
import org.cobweb.io.ConfXMLTag;


public class ProductionState implements AgentState {

	@ConfXMLTag("AgentParams")
	public ProductionAgentParams agentParams;

	public int unsoldProducts = 0;

	@Deprecated // for reflection use only!
	public ProductionState() {
	}

	public ProductionState(ProductionAgentParams agentParams) {
		this.agentParams = agentParams;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

	private static final long serialVersionUID = 1L;
}
