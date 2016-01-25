package com.mucommander.utils;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class StrongCryptographyProperlyConfiguredTest {
	@Test
	public void test() throws NoSuchAlgorithmException {
		CryptoUtils.assertStrongCryptography();
	}
	@Test
	public void testCanBeForced() throws NoSuchAlgorithmException {
		CryptoUtils.removeCryptographyRestrictions();
		CryptoUtils.assertStrongCryptography();
	}
}
