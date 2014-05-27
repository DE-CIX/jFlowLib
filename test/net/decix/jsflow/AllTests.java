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
import net.decix.jsflow.util.AddressTest;
import net.decix.jsflow.util.UtilityTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for net.decix.jsflow");
		suite.addTestSuite(AddressTest.class);
		suite.addTestSuite(UtilityTest.class);
		suite.addTestSuite(SflowHeaderTest.class);
		suite.addTestSuite(SampleDataHeaderTest.class);
		suite.addTestSuite(ExpandedCounterSampleHeaderTest.class);
		suite.addTestSuite(ExpandedFlowSampleHeaderTest.class);
		suite.addTestSuite(SampleDataHeaderTest.class);
		return suite;
	}
}