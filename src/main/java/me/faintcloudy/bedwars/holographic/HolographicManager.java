package me.faintcloudy.bedwars.holographic;

import java.util.ArrayList;
import java.util.List;

public class HolographicManager {

	private final List<SimpleHologram> holographics;

	public HolographicManager() {
		holographics = new ArrayList<>();
	}

	public void addHolographic(SimpleHologram holo) {
		if (!holographics.contains(holo)) {
			holographics.add(holo);
		}
	}

	public void removeHolographic(SimpleHologram holo) {
		holographics.remove(holo);
	}

	public void deleteHolographic(SimpleHologram holo) {
		if (holographics.contains(holo)) {
			holo.remove();
			holographics.remove(holo);
		}
	}

	public List<SimpleHologram> getHolographics() {
		return holographics;
	}
}
