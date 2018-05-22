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

package org.onap.aaf.auth.actions.test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.aaf.auth.actions.Message;

import static org.mockito.Mockito.*;
import org.junit.Test;

public class JU_Message {
	
	Message msg;
	
	@Before
	public void setUp() {
		msg = new Message();
	}

	@Test
	public void testLine() {
		msg.line("test");
	}
	
	@Test
	public void testClear() {
		msg.clear();
	}
	
	@Test
	public void testMsg() {
		StringBuilder sb = new StringBuilder();
		msg.line("test");
		msg.line("test1");
		msg.msg(sb, "indent");
	}

}