package com.boostdecay.render;

/**
 * Pure layout math for drawing several splats in a row centered on one point.
 */
public final class SplatLayout
{
	public static final int GAP = 4;

	private SplatLayout()
	{
	}

	/**
	 * Returns the left edge of each splat, relative to the row's center.
	 */
	public static int[] xOffsets(int[] widths)
	{
		int total = 0;
		for (int w : widths)
		{
			total += w;
		}
		total += GAP * Math.max(0, widths.length - 1);

		int[] offsets = new int[widths.length];
		int x = -total / 2;
		for (int i = 0; i < widths.length; i++)
		{
			offsets[i] = x;
			x += widths[i] + GAP;
		}
		return offsets;
	}

	/**
	 * Opacity of a splat: full for the first two thirds of its lifetime, then a linear fade to 0.
	 */
	public static float alpha(long elapsedMillis, int durationMillis)
	{
		long fadeStart = durationMillis * 2L / 3;
		if (elapsedMillis <= fadeStart)
		{
			return 1f;
		}
		float t = (float) (elapsedMillis - fadeStart) / (durationMillis - fadeStart);
		return Math.max(0f, Math.min(1f, 1f - t));
	}
}
