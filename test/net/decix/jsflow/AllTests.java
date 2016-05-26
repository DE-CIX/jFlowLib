/*
 * This file is part of jsFlow.
 *
 * Copyright (c) 2009 DE-CIX Management GmbH <http://www.de-cix.net> - All rights
 * reserved.
 * 
 * Author: Thomas King <thomas.king@de-cix.net>
 *
 * This software is licensed under the Apache License, version 2.0. A copy of 
 * the license agreement is included in this distribution.
 */
package net.decix.jsflow;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.decix.util.AddressTest;
import net.decix.util.UtilityTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for net.decix.jsflow");
		suite.addTestSuite(SflowHeaderTest.class);
		suite.addTestSuite(SampleDataHeaderTest.class);
		suite.addTestSuite(ExpandedCounterSampleHeaderTest_01.class);
		suite.addTestSuite(ExpandedCounterSampleHeaderTest_02.class);
		suite.addTestSuite(ExpandedFlowSampleHeaderTest.class);
		suite.addTestSuite(SampleDataHeaderTest.class);
		return suite;
	}
}