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
package com.att.cmd.role;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.att.cadi.CadiException;
import com.att.cadi.LocatorException;
import com.att.cmd.AAFcli;
import com.att.cmd.JU_AAFCli;
import com.att.cmd.role.Role;
import com.att.inno.env.APIException;

@RunWith(MockitoJUnitRunner.class)
public class JU_ListByNameOnly {
	
	private static ListByNameOnly lsByName;
	
	@BeforeClass
	public static void setUp () throws NoSuchFieldException, SecurityException, Exception, IllegalAccessException {
		AAFcli cli = JU_AAFCli.getAAfCli();
		Role role = new Role(cli);
		List ls = new List(role);
		lsByName = new ListByNameOnly(ls);
	}
	
	@Test
	public void exec() {
		try {
			assertEquals(lsByName._exec(0, "add","del","reset","extend","clear", "rename", "create"),500);
		} catch (CadiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LocatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
