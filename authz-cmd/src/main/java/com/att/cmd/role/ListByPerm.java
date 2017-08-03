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

import com.att.cadi.CadiException;
import com.att.cadi.LocatorException;
import com.att.cadi.client.Future;
import com.att.cadi.client.Rcli;
import com.att.cmd.Cmd;
import com.att.cmd.Param;
import com.att.cssa.rserv.HttpMethods;
import com.att.inno.env.APIException;

import aaf.v2_0.Roles;

/**
 * Return Roles by NS
 * 
 *
 */
public class ListByPerm extends Cmd {
	private static final String HEADER = "List Roles by Perm ";
	
	public ListByPerm(List parent) {
		super(parent,"perm", 
				new Param("type",true),
				new Param("instance", true),
				new Param("action", true)); 
	}

	@Override
	public int _exec(int _idx, final String ... args) throws CadiException, APIException, LocatorException {
	        int idx = _idx;
		final String type=args[idx];
		final String instance=args[++idx];
		final String action=args[++idx];

		return same(((List)parent).new ListRoles() {
			@Override
			public Integer code(Rcli<?> client) throws CadiException, APIException {

				Future<Roles> fp = client.read(
						"/authz/roles/perm/"+type+'/'+instance+'/'+action, 
						getDF(Roles.class)
						);
				return list(fp,client, HEADER+type+'|'+instance+'|'+action);
			}
		});
	}
	
	@Override
	public void detailedHelp(int indent, StringBuilder sb) {
		detailLine(sb,indent,HEADER);
		api(sb,indent,HttpMethods.GET,"authz/roles/user/<user>",Roles.class,true);
	}


}
