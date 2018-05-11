/*******************************************************************************
 * ============LICENSE_START====================================================
 * * org.onap.aaf
 * * ===========================================================================
 * * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
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
 * *
 ******************************************************************************/

package org.onap.aaf.cadi.cm.test;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.aaf.cadi.cm.CmAgent;

public class JU_CmAgent {

	private static final String resourceDirString = "src/test/resources";
	private static final String aafDir = resourceDirString + "/aaf";

	private ByteArrayInputStream inStream;

	@Before
	public void setup() {
		System.setProperty("user.home", aafDir);

		// Simulate user input
		inStream = new ByteArrayInputStream("test\nhttp://example.com\nhttp://example.com".getBytes());
		System.setIn(inStream);
	}

	@After
	public void tearDown() {
		recursiveDelete(new File(aafDir));
	}

	@Test
	public void test() {
		String[] args;
		args = new String[] {
				"-login",
				"-noexit",
		};
		CmAgent.main(args);

		inStream.reset();
		args = new String[] {
				"noexit=true",
		};
		CmAgent.main(args);

		inStream.reset();
		args = new String[] {
				"place",
		};
		CmAgent.main(args);

		inStream.reset();
		args = new String[] {
				"create"
		};
		CmAgent.main(args);

		inStream.reset();
		args = new String[] {
				"read"
		};
		CmAgent.main(args);

		inStream.reset();
		args = new String[] {
				"copy"
		};
		CmAgent.main(args);

		inStream.reset();
		args = new String[] {
				"update"
		};
		CmAgent.main(args);

		inStream.reset();
		args = new String[] {
				"delete"
		};
		CmAgent.main(args);

		inStream.reset();
		args = new String[] {
				"showpass"
		};
		CmAgent.main(args);

	}

	private void recursiveDelete(File file) {
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				recursiveDelete(f);
			}
			f.delete();
		}
		file.delete();
	}

}
