package com.lucaci32u4.UI.Viewport.Picker.Implementation;

import com.lucaci32u4.UI.Viewport.Picker.Hitbox;
import com.lucaci32u4.UI.Viewport.Picker.PickerAPI;
import com.lucaci32u4.util.SimpleEventQueue;

import java.util.ArrayList;
import java.util.Collection;

public abstract class SingleIterativeCollectionPicker implements PickerAPI {

	static class HitboxLifetimeEvent {
		enum Type {
			ATTACH, DETACH, EXIT,
		}
		Type type;
		Hitbox hitbox;
		int pickerChannel;
		HitboxLifetimeEvent(Type type, Hitbox hitbox, int pickerChannel) {
			this.type = type;
			this.hitbox = hitbox;
			this.pickerChannel = pickerChannel;
		}
	}

	private SimpleEventQueue<HitboxLifetimeEvent> queue;

	private ArrayList<Collection<Hitbox>> channel;
	private Thread thread;

	public SingleIterativeCollectionPicker() {
		queue = new SimpleEventQueue<>();
		channel = createCollections(2);
		thread = new Thread(this::run);
	}

	@Override public void attach(Hitbox hitbox, int pickerChannel) {
		queue.produce(new HitboxLifetimeEvent(HitboxLifetimeEvent.Type.ATTACH, hitbox, pickerChannel));
	}

	@Override public void detach(Hitbox hitbox, int pickerChannel) {
		queue.produce(new HitboxLifetimeEvent(HitboxLifetimeEvent.Type.DETACH, hitbox, pickerChannel));
	}

	@Override public void pick(int pointerX, int pointerY, int pickerChannel) {
		synchronized (this) {
			Collection<Hitbox> ch = channel.get(pickerChannel);
			for (Hitbox hitbox : ch) {
				if (hitbox.left <= pointerX) if (pointerX <= hitbox.right) if (hitbox.top <= pointerY) if (pointerY <= hitbox.bottom) {
					hitbox.parent.clicked(pointerX, pointerY, hitbox, pickerChannel);
				}
			}
		}
	}

	@Override public void destroy() {
		queue.produce(new HitboxLifetimeEvent(HitboxLifetimeEvent.Type.EXIT, null, -1));
	}

	private void run() {
		boolean running = true;
		HitboxLifetimeEvent event = null;
		while (running) {
			event = queue.consume(true);
			synchronized (this) {
				switch (event.type) {
					case ATTACH:
						channel.get(event.pickerChannel).add(event.hitbox);
						break;
					case DETACH:
						channel.get(event.pickerChannel).remove(event.hitbox);
						break;
					case EXIT:
						running = false;
						break;
				}
			}
		}
	}

	public abstract ArrayList<Collection<Hitbox>> createCollections(int collectionCount);
}
