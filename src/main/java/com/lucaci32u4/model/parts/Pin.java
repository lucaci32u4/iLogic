package com.lucaci32u4.model.parts;

import com.lucaci32u4.core.LogicPin;
import com.lucaci32u4.model.CoordinateHelper;
import com.lucaci32u4.ui.viewport.renderer.DrawAPI;
import com.lucaci32u4.model.Subcurcuit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

public class Pin extends Component {
	
	public Pin(Subcurcuit subcurcuit) {
		super(new PinSpec(), subcurcuit);
	}
	
	private static class PinSpec implements Component.BehaviourSpecification {
		private final PinSpec pinSpec = new PinSpec();
		private Component.Termination termination = null;
		private final Component.Termination[] termArr = new Component.Termination[1];
		private Component component = null;
		private final AtomicLong position = new AtomicLong();
		private final AtomicLong dimension = new AtomicLong();
		
		@Override public void onAttach(Component componentContainer) {
			component = componentContainer;
			// TODO: Logic pin
			termination = new Component.Termination(componentContainer, new LogicPin[]{ null });
			termArr[0] = termination;
		}
		
		@Override public void onChangePosition(int x, int y) {
			this.position.set((((long)x) << CoordinateHelper.SHIFT) | ((long)y));
		}
		
		@Override public void onChangeDimension(int width, int height) {
			this.dimension.set(dimension.get());
		}
		
		@Override public void onInteractiveClick(int x, int y) {
			// Nothing: pins do not interact with the user
		}
		
		@Override public Component.Termination[] getTerminations() {
			return termArr;
		}
		
		public long getPosition() {
			return position.get();
		}
		
		public long getDimension() {
			return position.get();
		}
		
		public void onDraw(@NotNull DrawAPI graphics, boolean attach, boolean detach) {
		
		}
		
		@Override
		public void onDetach() {
		
		}
	}
}
