package com.github.cxt.mybeimi.core.engine.game;

import org.cache2k.expiry.ValueWithExpiryTime;

public interface BeiMiGameTask extends ValueWithExpiryTime {
	public void execute();
}
