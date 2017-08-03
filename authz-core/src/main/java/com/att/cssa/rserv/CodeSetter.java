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
package com.att.cssa.rserv;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.att.inno.env.Trans;

// Package on purpose.  only want between RServlet and Routes
class CodeSetter<TRANS extends Trans> {
	private HttpCode<TRANS,?> code;
	private TRANS trans;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	public CodeSetter(TRANS trans, HttpServletRequest req, HttpServletResponse resp) {
		this.trans = trans;
		this.req = req;
		this.resp = resp;
				
	}
	public boolean matches(Route<TRANS> route) throws IOException, ServletException {
		// Find best Code in Route based on "Accepts (Get) or Content-Type" (if exists)
		return (code = route.getCode(trans, req, resp))!=null;
	}
	
	public HttpCode<TRANS,?> code() {
		return code;
	}
}
