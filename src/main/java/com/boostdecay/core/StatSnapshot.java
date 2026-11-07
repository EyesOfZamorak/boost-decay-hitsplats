package com.boostdecay.core;

import lombok.Value;

/**
 * Boosted and real level of one skill at one game tick.
 */
@Value
public class StatSnapshot
{
	int boosted;
	int real;
}
