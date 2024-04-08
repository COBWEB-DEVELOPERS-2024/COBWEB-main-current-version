package org.cobweb.cobweb2.plugins.abiotic;

import org.cobweb.cobweb2.core.NullPhenotype;
import org.cobweb.cobweb2.core.Phenotype;
import org.cobweb.io.ConfDisplayName;
import org.cobweb.io.ConfSquishParent;
import org.cobweb.io.ConfXMLTag;
import org.cobweb.io.ParameterSerializable;

public class AgentFactorParams implements ParameterSerializable {

	@ConfDisplayName("Preference")
	@ConfSquishParent
	public AbioticPreferenceParam preference = new AbioticPreferenceParam();

	@ConfDisplayName("Parameter")
	@ConfXMLTag("Parameter")
	public Phenotype parameter = new NullPhenotype();

	@Override
	protected AgentFactorParams clone() {
		try {
			AgentFactorParams copy = (AgentFactorParams) super.clone();
			copy.preference = this.preference.clone();
			return copy;
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static final long serialVersionUID = 1L;
}