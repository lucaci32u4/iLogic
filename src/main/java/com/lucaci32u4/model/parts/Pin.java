package com.lucaci32u4.model.parts;

import com.lucaci32u4.core.LogicPin;
import com.lucaci32u4.main.Const;
import com.lucaci32u4.model.CoordinateHelper;
import com.lucaci32u4.ui.viewport.renderer.DrawAPI;
import com.lucaci32u4.model.Subcurcuit;
import lombok.Getter;
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
		private @Getter int positionX = 0;
		private @Getter int positionY = 0;
		private static final int diamater = Integer.parseInt(Const.query("pin.diameter"));
		
		@Override public void onAttach(Component componentContainer) {
			component = componentContainer;
			// TODO: Logic pin
			termination = new Component.Termination(componentContainer, new LogicPin[]{ null });
			termArr[0] = termination;
		}
		
		@Override
		public void onChangePosition(int x, int y) {
			positionX = x;
			positionY = y;
		}
		
		@Override
		public void onChangeDimension(int width, int height) {
			// Nothing: Pins do not change dimension
		}
		
		@Override
		public void onInteractiveClick(int x, int y) {
			// Nothing: pins do not interact with the user
		}
		
		@Override
		public void onDetach() {
		
		}
		
		@Override public Component.Termination[] getTerminations() {
			return termArr;
		}
		
		public void onDraw(@NotNull DrawAPI graphics, boolean attach, boolean detach) {
		
		}
	}
}
