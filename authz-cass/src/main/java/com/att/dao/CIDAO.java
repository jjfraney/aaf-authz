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
package com.att.dao;

import java.util.Date;

import com.att.authz.layer.Result;
import com.att.inno.env.Trans;

public interface CIDAO<TRANS extends Trans> {

	/**
	 * Touch the date field for given Table
	 *  
	 * @param trans
	 * @param name
	 * @return
	 */
	public abstract Result<Void> touch(TRANS trans, String name, int ... seg);

	/**
	 * Read all Info entries, and set local Date objects
	 * 
	 * This is to support regular data checks on the Database to speed up Caching behavior
	 * 
	 */
	public abstract Result<Void> check(TRANS trans);

	public abstract Date get(TRANS trans, String table, int seg);

}
