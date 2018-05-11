/**
 * 
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

package org.onap.aaf.cadi.taf.dos.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.onap.aaf.cadi.PropAccess;
import org.onap.aaf.cadi.taf.TafResp.RESP;
import org.onap.aaf.cadi.taf.dos.DenialOfServiceTafResp;

public class JU_DenialOfServiceTafResp {

	private final static String description = "description";
	private final static RESP status = RESP.IS_AUTHENTICATED;

	private PropAccess access;

	@Before
	public void setup() {
		access = new PropAccess(new PrintStream(new ByteArrayOutputStream()), new String[0]);
	}

	@Test
	public void test() throws IOException {
		DenialOfServiceTafResp resp = new DenialOfServiceTafResp(access, status, description);
		assertThat(resp.isAuthenticated(), is(status));
		assertThat(resp.authenticate(), is(status));
	}

}
