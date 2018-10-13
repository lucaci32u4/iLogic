package com.lucaci32u4.UI.Viewport.Picker.Implementation;

import com.lucaci32u4.UI.Viewport.Picker.Hitbox;
import com.lucaci32u4.UI.Viewport.Picker.PickerAPI;
import com.lucaci32u4.util.SimpleEventQueue;

import java.util.Collection;

public abstract class SingleIterativeCollectionPicker implements PickerAPI {

	private SimpleEventQueue<PickerAPI.PickerEvent> queue;

	private Collection<Hitbox>[] channel;
	private Thread thread;

	public SingleIterativeCollectionPicker() {
		queue = new SimpleEventQueue<>();
		channel = createCollections(2);
		thread = new Thread(this::run);
	}

	@Override
	public void attach(Hitbox hitbox, int pickerChannel) {
		queue.produce(new PickerEvent(PickerEvent.Type.ATTACH, hitbox, pickerChannel));
	}

	@Override public void detach(Hitbox hitbox, int pickerChannel) {
		queue.produce(new PickerEvent(PickerEvent.Type.DETACH, hitbox, pickerChannel));
	}

	@Override public void pick(int pointerX, int pointerY, int pickerChannel) {
		synchronized (this) {
			Collection<Hitbox> ch = channel[pickerChannel];
			for (Hitbox hitbox : ch) {
				if (hitbox.left <= pointerX) if (pointerX <= hitbox.right) if (hitbox.top <= pointerY) if (pointerY <= hitbox.bottom) {
					hitbox.parent.clicked(pointerX, pointerY, hitbox, pickerChannel);
				}
			}
		}
	}

	@Override public void destroy() {
		queue.produce(new PickerEvent(PickerEvent.Type.EXIT, null, -1));
	}

	private void run() {
		boolean running = true;
		PickerAPI.PickerEvent event = null;
		while (running) {
			event = queue.consume(true);
			synchronized (this) {
				switch (event.type) {
					case ATTACH:
						channel[event.pickerChannel].add(event.hitbox);
						break;
					case DETACH:
						channel[event.pickerChannel].remove(event.hitbox);
						break;
					case EXIT:
						running = false;
						break;
				}
			}
		}
	}

	public abstract Collection<Hitbox>[] createCollections(int collectionCount);
}
