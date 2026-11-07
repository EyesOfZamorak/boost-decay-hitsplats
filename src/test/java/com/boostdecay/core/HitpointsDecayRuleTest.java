package com.boostdecay.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.runelite.api.Skill;
import org.junit.Test;

public class HitpointsDecayRuleTest
{
	private static final StatSnapshot PREV = new StatSnapshot(99, 90);
	private static final StatSnapshot NOW = new StatSnapshot(98, 90);

	private final HitpointsDecayRule rule = new HitpointsDecayRule();

	@Test
	public void ruleIsForHitpoints()
	{
		assertEquals(Skill.HITPOINTS, rule.skill());
	}

	@Test
	public void decayWithoutHitsplat()
	{
		assertTrue(rule.isDecay(PREV, NOW, new TickContext(false, 0)));
	}

	@Test
	public void noDecayWithHitsplat()
	{
		assertFalse(rule.isDecay(PREV, NOW, new TickContext(true, 0)));
	}
}
