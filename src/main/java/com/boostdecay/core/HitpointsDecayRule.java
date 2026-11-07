package com.boostdecay.core;

import net.runelite.api.Skill;

/**
 * Hitpoints decay: the shared rule, except any hitsplat on the local player this tick
 * means the drop was damage (or poison, venom, etc.), not decay.
 */
public class HitpointsDecayRule extends BoostDecayRule
{
	public HitpointsDecayRule()
	{
		super(Skill.HITPOINTS);
	}

	@Override
	public boolean isDecay(StatSnapshot prev, StatSnapshot now, TickContext ctx)
	{
		return super.isDecay(prev, now, ctx) && !ctx.isHitsplatOnLocalPlayer();
	}
}
