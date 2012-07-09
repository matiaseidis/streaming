package org.test.streaming;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class ConfTest {

	@Test
	public void testBaseDir() {
		Conf conf = new Conf("/confUnderTest.properties");
		String actualBaseDir = conf.getBaseDir();
		Assert.assertEquals(actualBaseDir, "files");
		String userHome = System.getProperty("user.home");
		File expectedBaseDir = new File(new File(userHome), "files");
	}

	@Test
	public void testCachosDir() throws Exception {
		Conf conf = new Conf("/confUnderTest.properties");
		String userHome = System.getProperty("user.home");
		File actualCachosDir = conf.getCachosDir();
		File expectedCachosDir = new File(new File(new File(userHome), "files"), "cachos");
		Assert.assertEquals(expectedCachosDir, actualCachosDir);
	}

	@Test
	public void testSharedDir() throws Exception {
		Conf conf = new Conf("/confUnderTest.properties");
		String userHome = System.getProperty("user.home");
		File actualSharedDir = conf.getSharedDir();
		File expectedSharedDir = new File(new File(new File(userHome), "files"), "shared");
		Assert.assertEquals(expectedSharedDir, actualSharedDir);
	}

	@Test
	public void testTempDir() throws Exception {
		Conf conf = new Conf("/confUnderTest.properties");
		String userHome = System.getProperty("user.home");
		File actualTempDir = conf.getTempDir();
		File expectedTempDir = new File(new File(new File(userHome), "files"), "temp");
		Assert.assertEquals(expectedTempDir, actualTempDir);
	}
}
