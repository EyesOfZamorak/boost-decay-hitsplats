package com.boostdecay.render;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SplatLayoutTest
{
	@Test
	public void oneSplatIsCentered()
	{
		assertArrayEquals(new int[]{-12}, SplatLayout.xOffsets(new int[]{24}));
	}

	@Test
	public void twoSplatsAreCenteredWithGap()
	{
		// total 24 + 4 + 24 = 52
		assertArrayEquals(new int[]{-26, 2}, SplatLayout.xOffsets(new int[]{24, 24}));
	}

	@Test
	public void threeSplatsAreCenteredWithGaps()
	{
		// total 24*3 + 4*2 = 80
		assertArrayEquals(new int[]{-40, -12, 16}, SplatLayout.xOffsets(new int[]{24, 24, 24}));
	}

	@Test
	public void alphaIsFullForFirstTwoThirds()
	{
		assertEquals(1f, SplatLayout.alpha(0, 1200), 0f);
		assertEquals(1f, SplatLayout.alpha(800, 1200), 0f);
	}

	@Test
	public void alphaFadesLinearlyToZero()
	{
		assertEquals(0.5f, SplatLayout.alpha(1000, 1200), 0.001f);
		assertEquals(0f, SplatLayout.alpha(1200, 1200), 0f);
	}
}
