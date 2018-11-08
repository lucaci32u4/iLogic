package com.lucaci32u4.UI.Viewport.Picker.Implementation;

import com.lucaci32u4.UI.Viewport.Picker.Hitbox;
import com.lucaci32u4.UI.Viewport.Picker.PickerAPI;
import com.lucaci32u4.util.Helper;
import com.lucaci32u4.util.SimpleEventQueue;

import java.util.ArrayList;
import java.util.Collection;

public abstract class SingleIterativeCollectionPicker implements PickerAPI {

	private ArrayList<Collection<Hitbox>> channel;
	
	@Override public void init() {
		channel = createCollections(3);
	}
	
	@Override public void attach(Hitbox hitbox, int pickerChannel) {
		channel.get(pickerChannel).add(hitbox);
	}

	@Override public void detach(Hitbox hitbox, int pickerChannel) {
		channel.get(pickerChannel).remove(hitbox);
	}

	@Override public Hitbox pick(int pointerX, int pointerY, int pickerChannel) {
		float depth = -1;
		Hitbox hb = null;
		Collection<Hitbox> ch = channel.get(pickerChannel);
		for (Hitbox hitbox : ch) {
			if (depth < hitbox.depth) if (hitbox.left <= pointerX) if (pointerX <= hitbox.right) if (hitbox.top <= pointerY) if (pointerY <= hitbox.bottom) {
				hb = hitbox;
				depth = hitbox.depth;
			}
		}
		return hb;
	}

	@Override public void destroy() {
	
	}

	public abstract ArrayList<Collection<Hitbox>> createCollections(int collectionCount);
}
