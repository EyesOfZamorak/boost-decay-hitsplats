package com.boostdecay.render;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SplatStyle
{
	ICON("Skill icon"),
	DRAWN("Drawn");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
