package com.boostdecay.core;

import net.runelite.api.Skill;

/**
 * The decay test shared by every boosted stat: the stat was above its real level,
 * dropped by exactly 1, and its real level did not change.
 */
public class BoostDecayRule implements StatDecayRule
{
	private final Skill skill;

	public BoostDecayRule(Skill skill)
	{
		this.skill = skill;
	}

	@Override
	public Skill skill()
	{
		return skill;
	}

	@Override
	public boolean isDecay(StatSnapshot prev, StatSnapshot now, TickContext ctx)
	{
		return now.getReal() == prev.getReal()
			&& prev.getBoosted() > prev.getReal()
			&& now.getBoosted() == prev.getBoosted() - 1;
	}
}
