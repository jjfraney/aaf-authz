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
package com.att.authz.gw.service;

import com.att.authz.env.AuthzTrans;
import com.att.authz.gw.mapper.Mapper;

public class GwServiceImpl<IN,OUT,ERROR> 
	  implements GwService<IN,OUT,ERROR> {
	
		private Mapper<IN,OUT,ERROR> mapper;
	
		public GwServiceImpl(AuthzTrans trans, Mapper<IN,OUT,ERROR> mapper) {
			this.mapper = mapper;
		}
		
		public Mapper<IN,OUT,ERROR> mapper() {return mapper;}

//////////////// APIs ///////////////////
};
