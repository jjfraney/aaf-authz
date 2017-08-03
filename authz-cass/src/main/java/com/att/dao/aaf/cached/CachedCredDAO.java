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
package com.att.dao.aaf.cached;

import java.util.List;

import com.att.authz.env.AuthzTrans;
import com.att.authz.layer.Result;
import com.att.dao.CIDAO;
import com.att.dao.CachedDAO;
import com.att.dao.aaf.cass.CredDAO;
import com.att.dao.aaf.cass.Status;

public class CachedCredDAO extends CachedDAO<AuthzTrans, CredDAO, CredDAO.Data> {
	public CachedCredDAO(CredDAO dao, CIDAO<AuthzTrans> info) {
		super(dao, info, CredDAO.CACHE_SEG);
	}
	
	/**
	 * Pass through Cred Lookup
	 * 
	 * Unlike Role and Perm, we don't need or want to cache these elements... Only used for NS Delete.
	 * 
	 * @param trans
	 * @param ns
	 * @return
	 */
	public Result<List<CredDAO.Data>> readNS(AuthzTrans trans, final String ns) {
		
		return dao().readNS(trans, ns);
	}
	
	public Result<List<CredDAO.Data>> readID(AuthzTrans trans, final String id) {
		DAOGetter getter = new DAOGetter(trans,dao()) {
			public Result<List<CredDAO.Data>> call() {
				return dao().readID(trans, id);
			}
		};
		
		Result<List<CredDAO.Data>> lurd = get(trans, id, getter);
		if(lurd.isOK() && lurd.isEmpty()) {
			return Result.err(Status.ERR_UserNotFound,"No User Cred found");
		}
		return lurd;
	}

}
