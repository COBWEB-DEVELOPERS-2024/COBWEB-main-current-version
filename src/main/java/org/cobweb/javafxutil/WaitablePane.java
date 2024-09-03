package org.cobweb.javafxutil;

import javafx.scene.layout.Pane;

/**
 * Pane that can be repainted synchronously
 */
public class WaitablePane extends Pane implements SynchronousDisplay {

	private FXNodeWaiter waiter = new FXNodeWaiter(this);

	@Override
	public void refresh(boolean wait) {
		waiter.refresh(wait);
	}

	@Override
	public boolean isReadyToRefresh() {
		return waiter.isReadyToRefresh();
	}

	public WaitablePane() {
		super();
	}

	private static final long serialVersionUID = 3027479023284219300L;


}