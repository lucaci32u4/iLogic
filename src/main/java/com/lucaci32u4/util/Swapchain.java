package com.lucaci32u4.util;

public class Swapchain<Member> {
	private Member left;
	private Member center;
	private Member right;
	public Swapchain(Member left, Member center, Member right) {
		this.left = left;
		this.center = center;
		this.right = right;
	}
	
	public synchronized Member left() {
		return left;
	}
	
	public synchronized Member right() {
		return right;
	}
	
	public synchronized Member swapLeft() {
		Member aux = center;
		center = left;
		left = aux;
		return aux;
	}

	public synchronized Member swapRight() {
		Member aux = center;
		center = right;
		right = aux;
		return aux;
	}
}
