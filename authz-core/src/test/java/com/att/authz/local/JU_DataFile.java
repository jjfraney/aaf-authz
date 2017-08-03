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
package com.att.authz.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.att.authz.local.DataFile;
import com.att.authz.local.DataFile.Token;
import com.att.authz.local.DataFile.Token.Field;

public class JU_DataFile {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
		File file = new File("../authz-batch/data/v1.dat");
		DataFile df = new DataFile(file,"r");
		int count = 0;
		List<String> list = new ArrayList<String>();
		try {
			df.open();
			Token tok = df.new Token(1024000);
			Field fld = tok.new Field('|');
	
			while(tok.nextLine()) {
				++count;
				fld.reset();
				list.add(fld.at(0));
			}
//			Collections.sort(list);
			for(String s: list) {
				System.out.println(s);

			}
		} finally {
			System.out.printf("%15s:%12d\n","Total",count);
		}
	}

}
