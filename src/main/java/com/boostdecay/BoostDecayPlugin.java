package com.boostdecay;

import com.boostdecay.core.BoostDecayTracker;
import com.boostdecay.core.DecayEvent;
import com.boostdecay.core.HitpointsDecayRule;
import com.boostdecay.core.StatDecayRule;
import com.boostdecay.core.StatSnapshot;
import com.boostdecay.core.TickContext;
import com.boostdecay.render.BoostDecayOverlay;
import com.boostdecay.render.SplatAppearance;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Boost Decay Hitsplats",
	description = "Shows a hitsplat each time overheal Hitpoints decay",
	tags = {"hitpoints", "hp", "overheal", "anglerfish", "brew", "hitsplat", "boost"}
)
public class BoostDecayPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private BoostDecayConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BoostDecayOverlay overlay;

	@Inject
	private SplatAppearance appearance;

	private final BoostDecayTracker tracker = new BoostDecayTracker(Collections.emptyList());

	private boolean hitsplatOnLocalPlayer;

	@Override
	protected void startUp()
	{
		appearance.rebuild();
		tracker.setRules(buildRules());
		tracker.reset();
		hitsplatOnLocalPlayer = false;
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlay.clear();
		tracker.reset();
	}

	private List<StatDecayRule> buildRules()
	{
		List<StatDecayRule> rules = new ArrayList<>();
		if (config.hitpointsEnabled())
		{
			rules.add(new HitpointsDecayRule());
		}
		return rules;
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() == client.getLocalPlayer())
		{
			hitsplatOnLocalPlayer = true;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			tracker.reset();
			hitsplatOnLocalPlayer = false;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Map<Skill, StatSnapshot> snapshots = new EnumMap<>(Skill.class);
		for (Skill skill : tracker.getSkills())
		{
			snapshots.put(skill, new StatSnapshot(client.getBoostedSkillLevel(skill), client.getRealSkillLevel(skill)));
		}

		TickContext ctx = new TickContext(hitsplatOnLocalPlayer, System.currentTimeMillis());
		hitsplatOnLocalPlayer = false;

		List<DecayEvent> events = tracker.onTick(snapshots, ctx);
		if (!events.isEmpty())
		{
			log.debug("Decay events: {}", events);
			overlay.add(events);
		}

		if (config.preview())
		{
			addPreviewEvents(ctx.getNowMillis());
		}
	}

	/**
	 * Shows a sample splat for each skill with an appearance, starting a new one as soon
	 * as the previous one has finished, so look and duration changes show up right away.
	 */
	private void addPreviewEvents(long nowMillis)
	{
		List<DecayEvent> preview = new ArrayList<>();
		for (Skill skill : appearance.getSkills())
		{
			if (!overlay.isShowing(skill, nowMillis))
			{
				preview.add(new DecayEvent(skill, -1, nowMillis));
			}
		}
		overlay.add(preview);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!BoostDecayConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		clientThread.invoke(() ->
		{
			tracker.setRules(buildRules());
			appearance.rebuild();
		});
	}

	@Provides
	BoostDecayConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BoostDecayConfig.class);
	}
}
