package com.boostdecay.render;

import com.boostdecay.BoostDecayConfig;
import com.boostdecay.core.DecayEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * Draws active decay splats over the local player.
 */
public class BoostDecayOverlay extends Overlay
{
	private final Client client;
	private final BoostDecayConfig config;
	private final SplatAppearance appearance;

	// One event per skill; EnumMap iterates in Skill order, giving a fixed row order.
	private final Map<Skill, DecayEvent> active = new EnumMap<>(Skill.class);

	@Inject
	BoostDecayOverlay(Client client, BoostDecayConfig config, SplatAppearance appearance)
	{
		this.client = client;
		this.config = config;
		this.appearance = appearance;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	/**
	 * Adds events; a new event for a skill replaces that skill's current one.
	 */
	public void add(List<DecayEvent> events)
	{
		for (DecayEvent event : events)
		{
			active.put(event.getSkill(), event);
		}
	}

	/**
	 * Whether a splat for the skill is still within its display lifetime.
	 */
	public boolean isShowing(Skill skill, long nowMillis)
	{
		DecayEvent event = active.get(skill);
		return event != null && nowMillis - event.getStartMillis() < config.durationMillis();
	}

	public void clear()
	{
		active.clear();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (active.isEmpty())
		{
			return null;
		}

		long now = System.currentTimeMillis();
		int duration = config.durationMillis();
		active.values().removeIf(e -> now - e.getStartMillis() >= duration);
		if (active.isEmpty())
		{
			return null;
		}

		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		List<DecayEvent> events = new ArrayList<>();
		List<BufferedImage> images = new ArrayList<>();
		for (DecayEvent event : active.values())
		{
			BufferedImage image = appearance.forSkill(event.getSkill());
			if (image != null)
			{
				events.add(event);
				images.add(image);
			}
		}
		if (images.isEmpty())
		{
			return null;
		}

		BufferedImage anchor = images.get(0);
		int zOffset = player.getLogicalHeight() / 2 + config.verticalOffset();
		Point loc = player.getCanvasImageLocation(anchor, zOffset);
		if (loc == null)
		{
			return null;
		}
		int centerX = loc.getX() + anchor.getWidth() / 2;
		int centerY = loc.getY() + anchor.getHeight() / 2;

		int[] widths = new int[images.size()];
		for (int i = 0; i < widths.length; i++)
		{
			widths[i] = images.get(i).getWidth();
		}
		int[] offsets = SplatLayout.xOffsets(widths);

		Composite original = graphics.getComposite();
		graphics.setFont(FontManager.getRunescapeSmallFont());
		for (int i = 0; i < events.size(); i++)
		{
			DecayEvent event = events.get(i);
			BufferedImage image = images.get(i);
			int x = centerX + offsets[i];
			int y = centerY - image.getHeight() / 2;

			float alpha = SplatLayout.alpha(now - event.getStartMillis(), duration);
			graphics.setComposite(AlphaComposite.SrcOver.derive(alpha));
			graphics.drawImage(image, x, y, null);

			if (config.showNumber())
			{
				drawNumber(graphics, String.valueOf(Math.abs(event.getDelta())), x, y, image);
			}
		}
		graphics.setComposite(original);
		return null;
	}

	private static void drawNumber(Graphics2D graphics, String text, int x, int y, BufferedImage image)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		int textX = x + (image.getWidth() - metrics.stringWidth(text)) / 2;
		int textY = y + (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
		graphics.setColor(Color.BLACK);
		graphics.drawString(text, textX + 1, textY + 1);
		graphics.setColor(Color.WHITE);
		graphics.drawString(text, textX, textY);
	}
}
