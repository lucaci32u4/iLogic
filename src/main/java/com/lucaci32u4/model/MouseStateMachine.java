package com.lucaci32u4.model;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MouseStateMachine {
	public interface StateListener {
		void change(State old, State now);
	}
	
	private enum ClockType {
		DOWN, UP, MOVE,
	}
	
	private interface NavigableNode {
	}
	
	public enum State implements NavigableNode {
		NULLSTATE(null, null) ;
		
		
		private final ClockType[] exit;
		private final NavigableNode[] node;
		private State(@Nullable ClockType[] exits, @Nullable NavigableNode[] nodes) {
			if ((exits == null) ^ (nodes == null)) throw new IllegalArgumentException();
			if ((exits != null) && (exits.length != nodes.length)) throw new IllegalArgumentException();
			this.exit = (exits != null ? exits : new ClockType[0]);
			this.node = (nodes != null ? nodes : new NavigableNode[0]);
		}
		
		public NavigableNode next(@NotNull ClockType ckt) {
			return null;
		}
	}
	
	
	
	
	private State current = null;
	private @Getter int posX = 0;
	private @Getter int posY = 0;
	
	public MouseStateMachine(State initial) {
		current = initial;
	}
	
	
	
}
