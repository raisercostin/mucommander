package com.mucommander.utils;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(CryptoUtils.class.getName());

	private static boolean isRestrictedCryptography() {
		try {
			return javax.crypto.Cipher.getMaxAllowedKeyLength("AES") <= 128;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	    // This simply matches the Oracle JRE, but not OpenJDK.
	    //return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
	}

	public static void assertStrongCryptography() {
		try {
			if (javax.crypto.Cipher.getMaxAllowedKeyLength("AES") <= 128) {
				throw new RuntimeException(
						"Java ships with 128-bit security by default only(For US export restriction reasons).\n"
								+ " You need to download and install the Java Cryptography Extension if you want to work with 256-bit+ security.\n"
								+ " Download them from here http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html\n"
								+ " As described in the zip/readme.txt file you should copy the jars in the standard place for JCE\n"
								+ " The standard place for JCE jurisdiction policy JAR files is:\n"
								+ "        <jre-home>/lib/security           [Unix]\n"
								+ "        <jdk-home>/jre/lib/security           [Unix]\n"
								+ "\n"
								+ "        <jre-home>\\lib\\security           [Windows]"
								+ "        <jdk-home>\\jre\\lib\\security           [Windows]"
								+ " @see http://stackoverflow.com/a/24129476/99248");
			}
		} catch (NoSuchAlgorithmException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	public static void removeCryptographyRestrictions() {
	    if (!isRestrictedCryptography()) {
	        logger.trace("Cryptography restrictions removal not needed");
	        return;
	    }
	    try {
	        /*
	         * Do the following, but with reflection to bypass access checks:
	         *
	         * JceSecurity.isRestricted = false;
	         * JceSecurity.defaultPolicy.perms.clear();
	         * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
	         */
	        final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
	        final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
	        final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

	        final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
	        isRestrictedField.setAccessible(true);
	        isRestrictedField.set(null, false);

	        final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
	        defaultPolicyField.setAccessible(true);
	        final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

	        final Field perms = cryptoPermissions.getDeclaredField("perms");
	        perms.setAccessible(true);
	        ((Map<?, ?>) perms.get(defaultPolicy)).clear();

	        final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
	        instance.setAccessible(true);
	        defaultPolicy.add((Permission) instance.get(null));

	        logger.trace("Successfully removed cryptography restrictions");
	    } catch (final Exception e) {
	        logger.warn("Failed to remove cryptography restrictions", e);
	    }
	}
}
