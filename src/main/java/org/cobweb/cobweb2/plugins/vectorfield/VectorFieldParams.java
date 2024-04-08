package org.cobweb.cobweb2.plugins.vectorfield;

import org.cobweb.cobweb2.core.AgentFoodCountable;
import org.cobweb.cobweb2.plugins.PerTypeParam;
import org.cobweb.io.ConfDisplayName;
import org.cobweb.io.ConfList;
import org.cobweb.io.ConfXMLTag;

import java.util.Arrays;

public class VectorFieldParams implements PerTypeParam<VectorFieldTypeParams> {

    /*
     * Whether gravity is activated
     */

    @ConfXMLTag("VecFieldParams")
    @ConfList(indexName = "Gravity", startAtOne = true)
    public VectorFieldTypeParams[] gravityParams = new VectorFieldTypeParams[0];
    @ConfDisplayName("Enable vectorfield")
    @ConfXMLTag("VFieldenabled")

    public boolean vecFieldEnabled;

    public VectorFieldParams(AgentFoodCountable initialSize) {
        resize(initialSize);
    }

    @Override
    public void resize(AgentFoodCountable envParams) {
        VectorFieldTypeParams[] n = Arrays.copyOf(gravityParams, envParams.getAgentTypes());

        for (int i = gravityParams.length; i < envParams.getAgentTypes(); i++) {
            n[i] = new VectorFieldTypeParams();
        }
        gravityParams = n;
    }

    @Override
    public VectorFieldTypeParams[] getPerTypeParams() {
        return gravityParams;
    }

    private static final long serialVersionUID = 1L;
}
