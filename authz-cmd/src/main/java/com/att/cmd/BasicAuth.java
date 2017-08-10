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
 * * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 * *
 ******************************************************************************/
package com.att.cmd;

import java.io.IOException;

import com.att.aft.dme2.api.DME2Client;
import com.att.cadi.SecuritySetter;
import com.att.cadi.Symm;

public class BasicAuth implements SecuritySetter<DME2Client> {
	private String cred;
	private String user;
	
	public BasicAuth(String user, String pass) throws IOException {
		this.user = user;
		cred = "Basic " + Symm.base64.encode(user+':'+pass);
	}
	
	@Override
	public void setSecurity(DME2Client client) {
		client.addHeader("Authorization" , cred);
	}

	@Override
	public String getID() {
		return user;
	}

	//@Override
	public int setLastResponse(int respCode) {
		// TODO Auto-generated method stub
		return 0;
	}

}
