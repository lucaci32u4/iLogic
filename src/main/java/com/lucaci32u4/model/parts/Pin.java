package com.lucaci32u4.model.parts;

import com.lucaci32u4.Core.LogicPin;
import com.lucaci32u4.UI.Viewport.Renderer.DrawAPI;
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
		
		@Override public void onChangePosition(long position) {
			this.position.set(position);
		}
		
		@Override public void onChangeDimension(long dimension) {
			this.dimension.set(dimension);
		}
		
		@Override public void onInteractiveClick(long position) {
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
	}
}
