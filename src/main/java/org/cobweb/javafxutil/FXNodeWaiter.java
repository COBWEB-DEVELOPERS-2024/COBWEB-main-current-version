package org.cobweb.javafxutil;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.application.Platform;

import java.lang.reflect.InvocationTargetException;


public class FXNodeWaiter implements SynchronousDisplay {

	private boolean donePaint = true;

	/**
	 * Runnable used to mark the control as refreshed
	 */
	private Runnable donePaintMarker = new Runnable() {
		@Override
		public void run() {
			donePaint = true;
		}
	};

	private final Node node;

	/** Constructor for the FXNodeWaiter object */
	public FXNodeWaiter(Node newNode) {
		this.node = newNode;
	}

	@Override
	public void refresh(boolean wait) {
		if (wait) {
			donePaint = false;
			redraw(node);
			// Wait for node to redraw
			if (Platform.isFxApplicationThread()) {
				// When we are in the JavaFX thread, redraw() and runLater() executes synchronously
				donePaint = true;
			} else {
				// Otherwise we need to wait for JavaFX thread to finish the redraw
				Platform.runLater(donePaintMarker);
				try {
					waitForJavaFx();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		} else if (donePaint) {
			// Start painting a new frame without waiting
			donePaint = false;
			redraw(node);
			Platform.runLater(donePaintMarker);
		}
	}

	/* Deprecated
	@Override
	public void refresh(boolean wait) {
		if (wait) {
			donePaint = false;
			component.repaint();
			// Wait for displayPanel to repaint
			if (SwingUtilities.isEventDispatchThread()) {
				// When we are in the Swing thread, repaint() executes synchronously
				donePaint = true;
			} else {
				// Otherwise we need to wait for Swing thread to finish the repaint
				try {
					SwingUtilities.invokeAndWait(donePaintMarker);
				} catch (InterruptedException ex) {
					donePaint = true;
				} catch (InvocationTargetException ex) {
					donePaint = true;
				}
			}
		} else if (donePaint) {
			// Start painting a new frame without waiting
			donePaint = false;
			component.repaint();
			SwingUtilities.invokeLater(donePaintMarker);
		}
	}
	*/

	@Override
	public boolean isReadyToRefresh() {
		return donePaint;
	}

	/** Redraws individual nodes when called */
	private void redraw(Node node) {
		if(node instanceof Pane) {
			((Pane) node).layout(); // Redraw containers
		}
		else {
			node.applyCss(); // Redraw individual nodes
		}
	}

	private void waitForJavaFx() throws InterruptedException {
		// Loop until donePaint is set to true by the JavaFX thread
		while(!donePaint) {
			try {
				// Avoid busy-waiting by sleeping the thread
				Thread.sleep(10);
			} catch(InterruptedException ex) {
				// Interrupt status will be restored
				Thread.currentThread().interrupt();
			}
		}
	}
}
