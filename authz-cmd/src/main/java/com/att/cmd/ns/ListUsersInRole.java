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
package com.att.cmd.ns;

import java.util.HashSet;
import java.util.Set;

import com.att.cadi.CadiException;
import com.att.cadi.LocatorException;
import com.att.cadi.client.Future;
import com.att.cadi.client.Rcli;
import com.att.cadi.client.Retryable;
import com.att.cmd.AAFcli;
import com.att.cmd.Cmd;
import com.att.cmd.Param;
import com.att.cssa.rserv.HttpMethods;
import com.att.inno.env.APIException;

import aaf.v2_0.Nss;
import aaf.v2_0.Nss.Ns;
import aaf.v2_0.Role;
import aaf.v2_0.Roles;
import aaf.v2_0.Users;
import aaf.v2_0.Users.User;

/**
 * p
 *
 */
public class ListUsersInRole extends Cmd {
	private static final String HEADER="List Users in Roles of Namespace ";
	
	public ListUsersInRole(ListUsers parent) {
		super(parent,"role", 
				new Param("ns",true)); 
	}

	@Override
	public int _exec(int _idx, final String ... args) throws CadiException, APIException, LocatorException {
	        int idx = _idx;
		final String ns=args[idx++];
		final boolean detail = aafcli.isDetailed();
		return same(new Retryable<Integer>() {
			@Override
			public Integer code(Rcli<?> client) throws CadiException, APIException {
				((ListUsers)parent).report(HEADER,ns);
				Future<Nss> fn = client.read("/authz/nss/"+ns,getDF(Nss.class));
				if(fn.get(AAFcli.timeout())) {
					if(fn.value!=null) {
						Set<String> uset = detail?null:new HashSet<String>();
						for(Ns n : fn.value.getNs()) {
							Future<Roles> fr = client.read("/authz/roles/ns/"+n.getName(), getDF(Roles.class));
							if(fr.get(AAFcli.timeout())) {
								for(Role r : fr.value.getRole()) {
									if(detail) {
										((ListUsers)parent).report(r.getName());
									}
									Future<Users> fus = client.read(
											"/authz/users/role/"+r.getName(), 
											getDF(Users.class)
											);
									if(fus.get(AAFcli.timeout())) {
										for(User u : fus.value.getUser()) {
											if(detail) {
												((ListUsers)parent).report("  ",u);
											} else {
											    uset.add(u.getId());
											}
										}
									} else if(fn.code()==404) {
										return 200;
									}
								}
							}
						}
						if(uset!=null) {
							for(String u : uset) {
								pw().print("  ");
								pw().println(u);
							}
						}
					}
				} else if(fn.code()==404) {
					return 200;
				} else {	
					error(fn);
				}
				return fn.code();
			}
		});
	}

	@Override
	public void detailedHelp(int _indent, StringBuilder sb) {
	        int indent = _indent;
		detailLine(sb,indent,HEADER);
		indent+=4;
		detailLine(sb,indent,"Report Users associated with this Namespace's Roles");
		sb.append('\n');
		detailLine(sb,indent,"If \"set details=true\" is specified, then all roles are printed ");
		detailLine(sb,indent,"with the associated users and expiration dates");
		indent-=4;
		api(sb,indent,HttpMethods.GET,"authz/nss/<ns>",Nss.class,true);
		api(sb,indent,HttpMethods.GET,"authz/roles/ns/<ns>",Roles.class,false);
		api(sb,indent,HttpMethods.GET,"authz/users/role/<ns>",Users.class,false);
	}

}
