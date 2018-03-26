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

package org.onap.aaf.cadi.aaf.v2_0;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Map;

import org.onap.aaf.cadi.AbsUserCache;
import org.onap.aaf.cadi.Access;
import org.onap.aaf.cadi.CadiException;
import org.onap.aaf.cadi.Lur;
import org.onap.aaf.cadi.Permission;
import org.onap.aaf.cadi.User;
import org.onap.aaf.cadi.Access.Level;
import org.onap.aaf.cadi.CachedPrincipal.Resp;
import org.onap.aaf.cadi.aaf.AAFPermission;
import org.onap.aaf.cadi.client.Future;
import org.onap.aaf.cadi.client.Rcli;
import org.onap.aaf.cadi.client.Retryable;
import org.onap.aaf.cadi.config.Config;
import org.onap.aaf.cadi.lur.LocalPermission;
import org.onap.aaf.misc.env.APIException;
import org.onap.aaf.misc.env.util.Split;

import aaf.v2_0.Perm;
import aaf.v2_0.Perms;

/**
 * Use AAF Service as Permission Service.
 * 
 * This Lur goes after AAF Permissions, which are elements of Roles, not the Roles themselves.
 * 
 * If you want a simple Role Lur, use AAFRoleLur
 * 
 * @author Jonathan
 *
 */
public class AAFLurPerm extends AbsAAFLur<AAFPermission> {
	/**
	 *  Need to be able to transmutate a Principal into either ATTUID or MechID, which are the only ones accepted at this
	 *  point by AAF.  There is no "domain", aka, no "@att.com" in "ab1234@att.com".  
	 *  
	 *  The only thing that matters here for AAF is that we don't waste calls with IDs that obviously aren't valid.
	 *  Thus, we validate that the ID portion follows the rules before we waste time accessing AAF remotely
	 * @throws APIException 
	 * @throws URISyntaxException 
	 * @throws DME2Exception 
	 */
	// Package on purpose
	AAFLurPerm(AAFCon<?> con) throws CadiException, APIException {
		super(con);
		attachOAuth2(con);
	}

	// Package on purpose
	AAFLurPerm(AAFCon<?> con, AbsUserCache<AAFPermission> auc) throws APIException {
		super(con,auc);
		attachOAuth2(con);
	}
	
	private void attachOAuth2(AAFCon<?> con) throws APIException {
		String oauth2_url;
		Class<?> tmcls = Config.loadClass(access,"org.osaaf.cadi.oauth.TokenMgr");
		if(tmcls!=null) {
			if((oauth2_url = con.access.getProperty(Config.CADI_OAUTH2_URL,null))!=null) {
				try {
					Constructor<?> tmconst = tmcls.getConstructor(AAFCon.class,String.class);
					Object tokMangr = tmconst.newInstance(con,oauth2_url);
					@SuppressWarnings("unchecked")
					Class<Lur> oa2cls = (Class<Lur>)Config.loadClass(access,"org.osaaf.cadi.oauth.OAuth2Lur");
					Constructor<Lur> oa2const = oa2cls.getConstructor(tmcls);
					Lur oa2 = oa2const.newInstance(tokMangr);
					setPreemptiveLur(oa2);
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new APIException(e);
				}
			} else {
				access.log(Level.INIT, "Both cadi-oauth jar and Property",Config.CADI_OAUTH2_URL,"is required to initialize OAuth2");
			}
		}
	}

	protected User<AAFPermission> loadUser(final Principal principal)  {
		final String name = principal.getName();
//		// Note: The rules for AAF is that it only stores permissions for ATTUID and MechIDs, which don't 
//		// have domains.  We are going to make the Transitive Class (see this.transmutative) to convert
//		final Principal tp = principal; //transmutate.mutate(principal);
//		if(tp==null) {
//			return null; // if not a valid Transmutated credential, don't bother calling...
//		}
//		TODO Create a dynamic way to declare domains supported.
		final long start = System.nanoTime();
		final boolean[] success = new boolean[]{false};
		
//		new Exception("loadUser").printStackTrace();
		try {
			return aaf.best(new Retryable<User<AAFPermission>>() {
				@Override
				public User<AAFPermission> code(Rcli<?> client) throws CadiException, ConnectException, APIException {
					Future<Perms> fp = client.read("/authz/perms/user/"+name,aaf.permsDF);
					
					// In the meantime, lookup User, create if necessary
					User<AAFPermission> user = getUser(principal);
					Principal p;
					if(user!=null && user.principal == null) {
						p = new Principal() {// Create a holder for lookups
							private String n = name;
							public String getName() {
								return n;
							}
						};
					} else {
						p = principal;
					}
					
					if(user==null) {
						addUser(user = new User<AAFPermission>(p,aaf.userExpires)); // no password
					}
					
					// OK, done all we can, now get content
					if(fp.get(aaf.timeout)) {
						success[0]=true;
						Map<String, Permission> newMap = user.newMap();
						boolean willLog = aaf.access.willLog(Level.DEBUG);
						for(Perm perm : fp.value.getPerm()) {
							user.add(newMap,new AAFPermission(perm.getType(),perm.getInstance(),perm.getAction(),perm.getRoles()));
							if(willLog) {
								aaf.access.log(Level.DEBUG, name,"has '",perm.getType(),'|',perm.getInstance(),'|',perm.getAction(),'\'');
							}
						}
						user.setMap(newMap);
					} else {
						int code;
						switch(code=fp.code()) {
							case 401:
								aaf.access.log(Access.Level.ERROR, code, "Unauthorized to make AAF calls");
								break;
							case 404:
								user.setNoPerms();
								break;
							default:
								aaf.access.log(Access.Level.ERROR, code, fp.body());
						}
					}

					return user;
				}
			});
		} catch (Exception e) {
			aaf.access.log(e,"Calling","/authz/perms/user/"+name);
			success[0]=false;
			return null;
		} finally {
			float time = (System.nanoTime()-start)/1000000f;
			aaf.access.log(Level.INFO, success[0]?"Loaded":"Load Failure",name,"from AAF in",time,"ms");
		}
	}

	public Resp reload(User<AAFPermission> user) {
		final String name = user.name;
		long start = System.nanoTime();
		boolean success = false;
		try {
			Future<Perms> fp = aaf.client(Config.AAF_DEFAULT_VERSION).read(
					"/authz/perms/user/"+name,
					aaf.permsDF
					);
			
			// OK, done all we can, now get content
			if(fp.get(aaf.timeout)) {
				success = true;
				Map<String,Permission> newMap = user.newMap(); 
				boolean willLog = aaf.access.willLog(Level.DEBUG);
				for(Perm perm : fp.value.getPerm()) {
					user.add(newMap, new AAFPermission(perm.getType(),perm.getInstance(),perm.getAction(),perm.getRoles()));
					if(willLog) {
						aaf.access.log(Level.DEBUG, name,"has",perm.getType(),perm.getInstance(),perm.getAction());
					}
				}
				user.renewPerm();
				return Resp.REVALIDATED;
			} else {
				int code;
				switch(code=fp.code()) {
					case 401:
						aaf.access.log(Access.Level.ERROR, code, "Unauthorized to make AAF calls");
						break;
					default:
						aaf.access.log(Access.Level.ERROR, code, fp.body());
				}
				return Resp.UNVALIDATED;
			}
		} catch (Exception e) {
			aaf.access.log(e,"Calling","/authz/perms/user/"+name);
			return Resp.INACCESSIBLE;
		} finally {
			float time = (System.nanoTime()-start)/1000000f;
			aaf.access.log(Level.AUDIT, success?"Reloaded":"Reload Failure",name,"from AAF in",time,"ms");
		}
	}

	@Override
	protected boolean isCorrectPermType(Permission pond) {
		return pond instanceof AAFPermission;
	}

	/* (non-Javadoc)
	 * @see org.onap.aaf.cadi.Lur#createPerm(java.lang.String)
	 */
	@Override
	public Permission createPerm(String p) {
		String[] params = Split.split('|', p);
		if(params.length==3) {
			return new AAFPermission(params[0],params[1],params[2]);
		} else {
			return new LocalPermission(p);
		}
	}
	
}