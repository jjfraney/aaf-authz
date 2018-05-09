/**
 * ============LICENSE_START====================================================
 * org.onap.aaf
 * ===========================================================================
 * Copyright (c) 2018 AT&T Intellectual Property. All rights reserved.
 * ===========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END====================================================
 *
 */

package org.onap.aaf.auth.helpers.test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.aaf.auth.actions.Message;
import org.onap.aaf.auth.helpers.Approver;
import org.onap.aaf.auth.org.Organization;

import static org.mockito.Mockito.*;
import org.junit.Test;

public class JU_Approver {

	Approver approver;
	Organization org;
	Message msg;
	
	@Before
	public void setUp() {
		org = mock(Organization.class);
		approver = new Approver("approver", org);
		msg = new Message();
	}
	
	@Test
	public void testAddRequest() {
		approver.addRequest("user");
		approver.addRequest("user");
	}
	
	@Test
	public void testBuild() {
		approver.addRequest("user");
		approver.addRequest("user1");
		approver.addRequest("user2");
		approver.addRequest("user3");
		approver.build(msg);
	}

}
