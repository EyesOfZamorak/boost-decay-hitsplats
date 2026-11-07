package com.boostdecay.core;

import lombok.Value;
import net.runelite.api.Skill;

/**
 * One step of a boosted stat moving back toward its real level.
 */
@Value
public class DecayEvent
{
	Skill skill;

	/**
	 * Signed level change. Always -1 in v1; a future restore rule may emit +1.
	 */
	int delta;

	long startMillis;
}
