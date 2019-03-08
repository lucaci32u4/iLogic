package com.lucaci32u4.model.parts.mousemachine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

enum InputType {
	MAIN_DOWN, MAIN_UP,
	SECONDARY_DOWN, SECONDARY_UP,
	HOVER, ESCAPE,
}

public class MouseStateMachine {
	private final @Getter Feeder feeder = new Feeder(this);
	private final @Getter WorldObserver worldObserver;
	private final @Getter StateListener stateListener;
	private @Getter State currentState;
	private @Getter @Setter(AccessLevel.PACKAGE) int x = 0;
	private @Getter @Setter(AccessLevel.PACKAGE) int y = 0;
	private int lx = 0;
	private int ly = 0;
	
	public MouseStateMachine(@NotNull State initial, @NotNull StateListener listener, @NotNull WorldObserver observer) {
		currentState = initial;
		stateListener = listener;
		worldObserver = observer;
		listener.update(null, initial);
	}
	
	private void feed(InputType event) {
		WorldObserver.WorldQueryResult worldQuery;
		switch (event) {
			case MAIN_DOWN:
				if (currentState == State.FREE) {
					worldQuery = worldObserver.extstsEntityAt(x, y);
					if (!worldQuery.isEmpty()) {
						currentState = State.HAS_SELECTION;
						stateListener.update(State.FREE, State.HAS_SELECTION);
						stateListener.commitLastSelectionQuery();
					} else {
						lx = x; ly = y;
						currentState = State.SELECT_AREA;
						stateListener.update(State.FREE, State.SELECT_AREA);
						stateListener.commitLastSelectionQuery();
					}
				} else if (currentState == State.HAS_SELECTION) {
					worldQuery = worldObserver.extstsEntityAt(x, y);
					if (!worldQuery.isEmpty()) {
						stateListener.commitLastSelectionQuery();
					} else {
						currentState = State.FREE;
						stateListener.update(State.HAS_SELECTION, State.FREE);
						
					}
				}
				break;
			case MAIN_UP:
				if (currentState == State.SELECT_AREA) {
					worldQuery = worldObserver.existsEntityInside(lx, ly, x, y);
					if (!worldQuery.isEmpty()) {
						currentState = State.HAS_SELECTION;
						stateListener.update(State.SELECT_AREA, State.HAS_SELECTION);
						stateListener.commitLastSelectionQuery();
					} else {
						currentState = State.FREE;
						stateListener.update(State.SELECT_AREA, State.FREE);
					}
				}
				break;
			case HOVER:
				if (currentState == State.SELECT_AREA) {
					worldObserver.existsEntityInside(lx, ly, x, y);
					stateListener.commitLastSelectionQuery();
				}
				break;
			case ESCAPE:
				if (currentState == State.HAS_SELECTION) {
					currentState = State.FREE;
					stateListener.update(State.HAS_SELECTION, State.FREE);
				}
				if (currentState == State.SELECT_AREA) {
					currentState = State.FREE;
					stateListener.update(State.SELECT_AREA, State.FREE);
				}
				break;
			default: // Secondary button up/down
				break;
		}
	}
	
	public static class Feeder {
		private final MouseStateMachine msm;
		Feeder(MouseStateMachine stateMachine) {
			msm = stateMachine;
		}
		public void onPointer(int x, int y) {
			msm.setX(x);
			msm.setY(y);
			msm.feed(InputType.HOVER);
		}
		public void onMain(boolean state) {
			msm.feed(state ? InputType.MAIN_DOWN : InputType.MAIN_UP);
		}
		public void onSecondary(boolean state) {
			msm.feed(state ? InputType.SECONDARY_DOWN : InputType.SECONDARY_UP);
		}
	}
	
	public interface WorldObserver {
		class WorldQueryResult {
			private final boolean wire;
			private final boolean component;
			private final boolean termination;
			public WorldQueryResult(boolean wire, boolean component, boolean termination) {
				this.wire = wire;
				this.component = component;
				this.termination = termination;
			}
			boolean hasWire() { return wire; }
			boolean hasComponent() { return component; }
			boolean hasTermination() { return termination; }
			boolean isEmpty() { return !(wire || component || termination); }
			boolean hasOnlyWire() { return wire && !component && !termination; }
			boolean hasOnlyComponent() { return !wire && component && !termination; }
			boolean hasOnlyTermination() { return !wire && !component && termination; }
		}
		@NotNull WorldQueryResult extstsEntityAt(int x, int y);
		@NotNull WorldQueryResult existsEntityInside(int x, int y, int toX, int toY);
		@NotNull WorldQueryResult existsEntityInsideAndCurrentSelection(int x, int y, int toX, int toY);
	}
	
	public interface StateListener {
		void update(State old, State now);
		void commitLastSelectionQuery();
	}
}



