package com.boostdecay.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Skill;

/**
 * Remembers the previous snapshot of each registered skill and turns each tick's
 * snapshots into decay events using the registered rules.
 */
public class BoostDecayTracker
{
	private final Map<Skill, StatDecayRule> rules = new EnumMap<>(Skill.class);
	private final Map<Skill, StatSnapshot> last = new EnumMap<>(Skill.class);

	public BoostDecayTracker(List<StatDecayRule> rules)
	{
		setRules(rules);
	}

	/**
	 * Replaces the registered rules. Skills that stay registered keep their last snapshot.
	 */
	public void setRules(List<StatDecayRule> newRules)
	{
		rules.clear();
		for (StatDecayRule rule : newRules)
		{
			rules.put(rule.skill(), rule);
		}
		last.keySet().retainAll(rules.keySet());
	}

	public Set<Skill> getSkills()
	{
		return Collections.unmodifiableSet(rules.keySet());
	}

	public List<DecayEvent> onTick(Map<Skill, StatSnapshot> snapshots, TickContext ctx)
	{
		List<DecayEvent> events = new ArrayList<>();
		for (StatDecayRule rule : rules.values())
		{
			Skill skill = rule.skill();
			StatSnapshot now = snapshots.get(skill);
			if (now == null)
			{
				continue;
			}

			StatSnapshot prev = last.get(skill);
			if (prev != null && rule.isDecay(prev, now, ctx))
			{
				events.add(new DecayEvent(skill, -1, ctx.getNowMillis()));
			}
			last.put(skill, now);
		}
		return events;
	}

	/**
	 * Forgets all previous snapshots, so the next tick only records state.
	 */
	public void reset()
	{
		last.clear();
	}
}
