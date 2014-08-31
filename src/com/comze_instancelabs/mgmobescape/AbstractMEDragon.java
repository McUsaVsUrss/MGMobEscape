package com.comze_instancelabs.mgmobescape;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface AbstractMEDragon {

	public Vector getNextPosition();
	
	public void setPosition(double x, double y, double z);
	
	public void setYawPitch(float yaw, float pitch);
}
