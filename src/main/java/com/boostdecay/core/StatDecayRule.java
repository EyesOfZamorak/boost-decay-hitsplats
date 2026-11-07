package com.boostdecay.core;

import net.runelite.api.Skill;

/**
 * Decides whether a change in one skill between two ticks is a decay step.
 */
public interface StatDecayRule
{
	Skill skill();

	boolean isDecay(StatSnapshot prev, StatSnapshot now, TickContext ctx);
}
