/*******************************************************************************
 * ============LICENSE_START====================================================
 * * org.onap.aai
 * * ===========================================================================
 * * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * * Copyright © 2017 Amdocs
 * * ===========================================================================
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * * 
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 * * 
 *  * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 * * ============LICENSE_END====================================================
 * *
 * * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 * *
 ******************************************************************************/
package com.att.cssa.rserv;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class JU_Version {
	Version version;

	
	@Before
	public void setUp(){
		version = new Version("String");
	}

	@Test
	public void testEquals(){
		boolean val = version.equals(version);
		System.out.println("value of val " +val);
		assertTrue(val);
	}
	
	@Test
	public void testToString(){
		String strVal = version.toString();
		System.out.println("value of strVal " +strVal);
		assertNotNull(strVal);
	}
}
