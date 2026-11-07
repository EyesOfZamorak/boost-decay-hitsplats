package com.boostdecay.render;

import com.boostdecay.BoostDecayConfig;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;

/**
 * Maps each skill to the image its splat is drawn with. Drawn-style splats are
 * rendered into an image once on rebuild, so the overlay only ever blits images.
 */
@Singleton
public class SplatAppearance
{
	private static final int DRAWN_SIZE = 24;
	private static final Color OUTLINE = new Color(20, 20, 20);

	private final BoostDecayConfig config;
	private final SkillIconManager skillIconManager;
	private final Map<Skill, BufferedImage> images = new EnumMap<>(Skill.class);

	@Inject
	SplatAppearance(BoostDecayConfig config, SkillIconManager skillIconManager)
	{
		this.config = config;
		this.skillIconManager = skillIconManager;
	}

	public void rebuild()
	{
		images.clear();
		images.put(Skill.HITPOINTS, config.hitpointsStyle() == SplatStyle.DRAWN
			? drawCircle(config.hitpointsColor())
			: skillIconManager.getSkillImage(Skill.HITPOINTS));
	}

	/**
	 * Skills that have an appearance, whether or not their rule is enabled.
	 */
	public Set<Skill> getSkills()
	{
		return Collections.unmodifiableSet(images.keySet());
	}

	/**
	 * Returns the image for a skill's splat, or null if the skill has no appearance.
	 */
	public BufferedImage forSkill(Skill skill)
	{
		return images.get(skill);
	}

	private static BufferedImage drawCircle(Color fill)
	{
		BufferedImage image = new BufferedImage(DRAWN_SIZE, DRAWN_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(fill);
		g.fillOval(1, 1, DRAWN_SIZE - 2, DRAWN_SIZE - 2);
		g.setColor(OUTLINE);
		g.setStroke(new BasicStroke(1f));
		g.drawOval(1, 1, DRAWN_SIZE - 3, DRAWN_SIZE - 3);
		g.dispose();
		return image;
	}
}
