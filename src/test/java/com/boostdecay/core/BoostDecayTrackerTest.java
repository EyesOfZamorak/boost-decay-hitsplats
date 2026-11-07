package com.boostdecay.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import org.junit.Test;

public class BoostDecayTrackerTest
{
	private static final TickContext CTX = new TickContext(false, 1234);

	private static Map<Skill, StatSnapshot> levels(Object... skillBoostedReal)
	{
		Map<Skill, StatSnapshot> map = new EnumMap<>(Skill.class);
		for (int i = 0; i < skillBoostedReal.length; i += 3)
		{
			map.put((Skill) skillBoostedReal[i],
				new StatSnapshot((int) skillBoostedReal[i + 1], (int) skillBoostedReal[i + 2]));
		}
		return map;
	}

	@Test
	public void firstTickAfterConstructionNeverEmits()
	{
		BoostDecayTracker tracker = new BoostDecayTracker(Collections.singletonList(new HitpointsDecayRule()));
		assertTrue(tracker.onTick(levels(Skill.HITPOINTS, 98, 90), CTX).isEmpty());
	}

	@Test
	public void firstTickAfterResetNeverEmits()
	{
		BoostDecayTracker tracker = new BoostDecayTracker(Collections.singletonList(new HitpointsDecayRule()));
		tracker.onTick(levels(Skill.HITPOINTS, 99, 90), CTX);
		tracker.reset();
		assertTrue(tracker.onTick(levels(Skill.HITPOINTS, 98, 90), CTX).isEmpty());
	}

	@Test
	public void emitsDecayEvent()
	{
		BoostDecayTracker tracker = new BoostDecayTracker(Collections.singletonList(new HitpointsDecayRule()));
		tracker.onTick(levels(Skill.HITPOINTS, 99, 90), CTX);
		List<DecayEvent> events = tracker.onTick(levels(Skill.HITPOINTS, 98, 90), CTX);
		assertEquals(Collections.singletonList(new DecayEvent(Skill.HITPOINTS, -1, 1234)), events);
	}

	@Test
	public void twoSkillsEmitIndependentlyOnSameTick()
	{
		BoostDecayTracker tracker = new BoostDecayTracker(Arrays.asList(
			new HitpointsDecayRule(), new BoostDecayRule(Skill.STRENGTH)));
		tracker.onTick(levels(Skill.HITPOINTS, 99, 90, Skill.STRENGTH, 110, 99), CTX);
		List<DecayEvent> events = tracker.onTick(levels(Skill.HITPOINTS, 98, 90, Skill.STRENGTH, 109, 99), CTX);
		// Events come out in Skill enum order
		assertEquals(Arrays.asList(
			new DecayEvent(Skill.STRENGTH, -1, 1234),
			new DecayEvent(Skill.HITPOINTS, -1, 1234)), events);
	}

	@Test
	public void unregisteredSkillNeverEmits()
	{
		BoostDecayTracker tracker = new BoostDecayTracker(Collections.singletonList(new HitpointsDecayRule()));
		tracker.onTick(levels(Skill.STRENGTH, 110, 99), CTX);
		assertTrue(tracker.onTick(levels(Skill.STRENGTH, 109, 99), CTX).isEmpty());
	}

	@Test
	public void removedRuleStopsEmitting()
	{
		BoostDecayTracker tracker = new BoostDecayTracker(Collections.singletonList(new HitpointsDecayRule()));
		tracker.onTick(levels(Skill.HITPOINTS, 99, 90), CTX);
		tracker.setRules(Collections.emptyList());
		assertTrue(tracker.onTick(levels(Skill.HITPOINTS, 98, 90), CTX).isEmpty());
	}
}
