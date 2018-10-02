package com.lucaci32u4.core;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class LogicID  {
	public static String toString(@NotNull UUID id) {
		return Long.toHexString(id.getMostSignificantBits()) + Long.toHexString(id.getLeastSignificantBits());
	}
}
