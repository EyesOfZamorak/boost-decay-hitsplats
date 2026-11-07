package com.boostdecay;

import com.boostdecay.render.SplatStyle;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(BoostDecayConfig.GROUP)
public interface BoostDecayConfig extends Config
{
	String GROUP = "boostdecayhitsplats";

	@ConfigItem(
		keyName = "showNumber",
		name = "Show number",
		description = "Draw the number on the splat",
		position = 0
	)
	default boolean showNumber()
	{
		return true;
	}

	@Range(min = 300, max = 3000)
	@Units(Units.MILLISECONDS)
	@ConfigItem(
		keyName = "durationMillis",
		name = "Duration",
		description = "How long each splat stays on screen",
		position = 1
	)
	default int durationMillis()
	{
		return 1200;
	}

	@Range(min = -100, max = 100)
	@ConfigItem(
		keyName = "verticalOffset",
		name = "Vertical offset",
		description = "Extra height above the default position, in local units",
		position = 2
	)
	default int verticalOffset()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "preview",
		name = "Preview",
		description = "Keep showing sample splats on your character so you can try out the look",
		position = 3
	)
	default boolean preview()
	{
		return false;
	}

	@ConfigSection(
		name = "Hitpoints",
		description = "Splats for overheal Hitpoints decay",
		position = 10
	)
	String hitpointsSection = "hitpoints";

	@ConfigItem(
		keyName = "hitpointsEnabled",
		name = "Enabled",
		description = "Show splats for overheal Hitpoints decay",
		section = hitpointsSection,
		position = 11
	)
	default boolean hitpointsEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hitpointsStyle",
		name = "Style",
		description = "Use the Hitpoints skill icon or a drawn circle",
		section = hitpointsSection,
		position = 12
	)
	default SplatStyle hitpointsStyle()
	{
		return SplatStyle.ICON;
	}

	@ConfigItem(
		keyName = "hitpointsColor",
		name = "Color",
		description = "Fill color when the style is Drawn",
		section = hitpointsSection,
		position = 13
	)
	default Color hitpointsColor()
	{
		return new Color(0x8E44AD);
	}
}
