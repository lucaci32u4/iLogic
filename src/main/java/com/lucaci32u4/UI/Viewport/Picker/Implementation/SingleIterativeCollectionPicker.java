package com.lucaci32u4.UI.Viewport.Picker.Implementation;

import com.lucaci32u4.UI.Viewport.Picker.Hitbox;
import com.lucaci32u4.UI.Viewport.Picker.HitboxPicker;

import java.util.Collection;

public abstract class SingleIterativeCollectionPicker implements HitboxPicker {

	private Collection<Hitbox>[] channel;

	public SingleIterativeCollectionPicker() {
		channel = createCollections(2);
	}

	@Override public void attach(Hitbox hitbox, int pickerChannel) {
		channel[pickerChannel].add(hitbox);
	}

	@Override public void detach(Hitbox hitbox, int pickerChannel) {
		channel[pickerChannel].remove(hitbox);
	}

	@Override public void pick(int pointerX, int pointerY, int pickerChannel) {
		Collection<Hitbox> ch = channel[pickerChannel];
		for (Hitbox hitbox : ch) {
			if (hitbox.left <= pointerX) if (pointerX <= hitbox.right) if (hitbox.top <= pointerY) if (pointerY <= hitbox.bottom) {
				hitbox.parent.clicked(pointerX, pointerY, hitbox, pickerChannel);
			}
		}
	}

	@Override public void destroy() {

	}

	public abstract Collection<Hitbox>[] createCollections(int collectionCount);
}
