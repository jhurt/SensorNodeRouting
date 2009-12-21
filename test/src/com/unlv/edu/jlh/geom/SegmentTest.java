/*
Copyright (c) 2009, University of Nevada, Las Vegas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    * Neither the name of the University of Nevada, Las Vegas, nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.unlv.edu.jlh.geom;

import junit.framework.TestCase;
import org.junit.Test;

public class SegmentTest extends TestCase {

	@Test
	public void testGetAngleRadiansWithSegment() {
		Point pointA = new Point(0, 0);
		Point pointB = new Point(5, 0);
		Point pointC = new Point(0, 5);

		Segment segmentOne = new Segment(pointA, pointB);
		Segment segmentTwo = new Segment(pointA, pointC);

		assertEquals(Math.PI * 2 * 0.75, segmentOne.getAngleRadiansWithSegment(segmentTwo));
		assertEquals(Math.PI / 2, segmentTwo.getAngleRadiansWithSegment(segmentOne));
	}

	@Test
	public void testEquals() {
		Point pointA = new Point(1, 2);
		Point pointB = new Point(1, 2);
		Point pointC = new Point(4, 5);
		Point pointD = new Point(4, 5);
		Point pointE = new Point(6, 7);
		Point pointF = new Point(9, 10);

		Segment segmentOne = new Segment(pointA, pointC);
		Segment segmentTwo = new Segment(pointB, pointD);
		Segment segmentThree = new Segment(pointE, pointF);

		assertTrue(segmentOne.equals(segmentTwo));
		assertTrue(segmentTwo.equals(segmentOne));
		assertFalse(segmentTwo.equals(segmentThree));
		assertFalse(segmentThree.equals(segmentOne));
	}
}
