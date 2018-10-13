package com.lucaci32u4.util;

public class CircularSwapchain<Member> {
	private Member[] member;

	public CircularSwapchain(Member[] members) {
		member = members;
	}
	
	public synchronized Member get(int index) {
		return member[index];
	}

	public synchronized void rotate() {
		Member aux = null;
		for (int i = 0; i < member.length - 1; i++) {
			aux = member[i];
			member[i] = member[i + 1];
			member[i + 1] = aux;
		}
	}
}
