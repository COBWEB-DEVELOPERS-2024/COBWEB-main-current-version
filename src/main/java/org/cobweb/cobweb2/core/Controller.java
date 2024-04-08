package org.cobweb.cobweb2.core;


/**
 * The "brain" of an Agent, the controller causes the controlled agent to act by
 * calling methods on it. The Controller is notified by a call to control agent
 * that the Agent is requesting guidance.
 */
public interface Controller {

	/**
	 * Cause the specified agent to act.
	 *
	 * @param theAgent agent to control
	 */
	public void controlAgent(Agent theAgent, ControllerListener inputCallback);

	/**
	 * Creates controller for child based on parameters of the asexual breeding parent
	 */
	public Controller createChildAsexual();

	/**
	 * Creates controller for child based on parameters of the sexual breeding parents
	 *
	 * @param parent2 second parent
	 */
	public Controller createChildSexual(Controller parent2);
}
