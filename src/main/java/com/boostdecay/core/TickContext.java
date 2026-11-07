package com.boostdecay.core;

import lombok.Value;

/**
 * Facts gathered during one game tick that rules may need.
 */
@Value
public class TickContext
{
	/**
	 * A hitsplat of any type landed on the local player since the previous tick.
	 */
	boolean hitsplatOnLocalPlayer;

	/**
	 * Wall-clock time of the tick, used as the start time of emitted events.
	 */
	long nowMillis;
}
