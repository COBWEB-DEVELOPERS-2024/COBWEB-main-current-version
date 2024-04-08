package org.cobweb.cobweb2.plugins.food;

import java.util.Arrays;

import org.cobweb.cobweb2.core.AgentFoodCountable;
import org.cobweb.cobweb2.plugins.PerTypeParam;
import org.cobweb.cobweb2.plugins.abiotic.AbioticParams;
import org.cobweb.io.ConfDisplayName;
import org.cobweb.io.ConfList;
import org.cobweb.io.ConfXMLTag;


public class FoodGrowthParams implements PerTypeParam<ComplexFoodParams> {
	/**
	 * Spawns new food on the grid.
	 */
	@ConfDisplayName("Drop new food")
	@ConfXMLTag("dropNewFood")
	public boolean dropNewFood = true;

	/**
	 * Probability that food will grow around similar existing food.
	 */
	@ConfDisplayName("Like food probability")
	@ConfXMLTag("likeFoodProb")
	public float likeFoodProb = 0;

	@ConfXMLTag("FoodParams")
	@ConfList(indexName = "Food", startAtOne = true)
	public ComplexFoodParams[] foodParams = new ComplexFoodParams[0];

	private AbioticParams abioticParams;

	public FoodGrowthParams(AgentFoodCountable initialSize, AbioticParams abioticParams) {
		this.abioticParams = abioticParams;
		resize(initialSize);
	}

	@Override
	public void resize(AgentFoodCountable envParams) {
		ComplexFoodParams[] n = Arrays.copyOf(foodParams, envParams.getAgentTypes());

		for (int i = foodParams.length; i < envParams.getAgentTypes(); i++) {
			n[i] = new ComplexFoodParams();
		}
		foodParams = n;

		for (ComplexFoodParams p : foodParams) {
			p.resizeAbiotic(abioticParams.factors.size());
		}
	}

	@Override
	public ComplexFoodParams[] getPerTypeParams() {
		return foodParams;
	}

	private static final long serialVersionUID = 1L;
}
