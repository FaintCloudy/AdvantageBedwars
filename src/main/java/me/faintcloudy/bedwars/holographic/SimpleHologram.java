package me.faintcloudy.bedwars.holographic;

import me.faintcloudy.bedwars.Bedwars;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.*;

public class SimpleHologram {

	private Location location;
	private String title;
	public ArmorStand armorStand;
	private HashMap<SimpleHologram, Location> armorloc = new HashMap<>();
	private HashMap<SimpleHologram, Boolean> armorupward = new HashMap<>();
	private HashMap<SimpleHologram, Integer> armoralgebra = new HashMap<>();
	private boolean removed;

	public SimpleHologram(Location loc, String title) {
		location = loc.clone();
		this.title = title;
		removed = false;
		this.armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
		this.armorStand.setCustomNameVisible(true);
		if (title == null)
			this.armorStand.setCustomNameVisible(false);
		else
			this.armorStand.setCustomName(title);

		this.armorStand.setGravity(false);
		this.armorStand.setVisible(false);
		this.armorStand.setBasePlate(false);

		Bedwars.getInstance().getHolographicManager().addHolographic(this);
	}


	public void moveArmorStand() {
		SimpleHologram holo = this;
		if (!armorloc.containsKey(holo)) {
			armorloc.put(holo, holo.getLocation().clone());
		}
		if (!armorupward.containsKey(holo)) {
			armorupward.put(holo, true);
		}
		if (!armoralgebra.containsKey(holo)) {
			armoralgebra.put(holo, 0);
		}
		Location location = armorloc.get(holo);
		Integer algebra = armoralgebra.get(holo);
		boolean upward = armorupward.get(holo);
		double turn = 1;
		if (!armorupward.get(holo)) {
			turn = -turn;
		}
		double move_yaw = 0;
		double move_y = 0;
		if (algebra <= 30) {
			move_yaw += algebra * 0.62 * turn;
		} else {
			move_yaw += (59 - algebra) * 0.62 * turn;
		}
		if (algebra >= 9 && algebra <= 50) {
			move_y += 0.01125 * turn;
		}
		location.setY(location.getY() + move_y);
		if (algebra >= 59) {
			armoralgebra.put(holo, 0);
			armorupward.put(holo, !upward);
		}
		armoralgebra.put(holo, armoralgebra.get(holo) + 1);
		double yaw = location.getYaw();
		yaw += move_yaw; // * 1.0D
		yaw = yaw > 360 ? (yaw - 360) : yaw;
		yaw = yaw < -360 ? (yaw + 360) : yaw;
		location.setYaw((float) yaw);
		holo.teleport(location);
	}


	public Location getLocation() {
		return location.clone();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (removed)
			return;
		armorStand.setCustomName(title);
	}

	public void remove() {
		if (removed)
			return;
		removed = true;
		armorStand.remove();
		Bedwars.getInstance().getHolographicManager().removeHolographic(this);
	}

	public void teleport(Location loc) {
		if (removed) {
			return;
		}
		armorStand.teleport(location);
		location = loc.clone();
	}

}
