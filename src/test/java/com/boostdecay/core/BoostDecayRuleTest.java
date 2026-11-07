package com.boostdecay.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.runelite.api.Skill;
import org.junit.Test;

public class BoostDecayRuleTest
{
	private static final TickContext CTX = new TickContext(false, 0);

	private final BoostDecayRule rule = new BoostDecayRule(Skill.STRENGTH);

	private boolean decay(int prevBoosted, int prevReal, int nowBoosted, int nowReal)
	{
		return rule.isDecay(new StatSnapshot(prevBoosted, prevReal), new StatSnapshot(nowBoosted, nowReal), CTX);
	}

	@Test
	public void dropFromBoostedIsDecay()
	{
		assertTrue(decay(99, 90, 98, 90));
	}

	@Test
	public void finalStepToRealIsDecay()
	{
		assertTrue(decay(91, 90, 90, 90));
	}

	@Test
	public void dropWhenNotBoostedIsNotDecay()
	{
		assertFalse(decay(90, 90, 89, 90));
	}

	@Test
	public void dropOfTwoIsNotDecay()
	{
		assertFalse(decay(99, 90, 97, 90));
	}

	@Test
	public void levelUpIsNotDecay()
	{
		assertFalse(decay(99, 90, 98, 91));
	}

	@Test
	public void restoreOfDrainedStatIsNotDecay()
	{
		assertFalse(decay(80, 90, 81, 90));
	}
}
