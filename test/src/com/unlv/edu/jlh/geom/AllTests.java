package com.unlv.edu.jlh.geom;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests {
	private final TestSuite suite=new TestSuite();
	public AllTests() {
		suite.addTestSuite(ArcTest.class);
		suite.addTestSuite(CircleTest.class);
		suite.addTestSuite(PointTest.class);
		suite.addTestSuite(SegmentTest.class);
	}

	public void run() {
		TestRunner.run(suite);
	}

	public static void main(String[] args) {
		new AllTests().run();			
	}
}
