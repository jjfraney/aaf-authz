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

package org.onap.aaf.auth.server;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.Filter;

import org.onap.aaf.auth.common.Define;
import org.onap.aaf.auth.rserv.RServlet;
import org.onap.aaf.cadi.Access;
import org.onap.aaf.cadi.Access.Level;
import org.onap.aaf.cadi.CadiException;
import org.onap.aaf.cadi.LocatorException;
import org.onap.aaf.cadi.aaf.v2_0.AAFConHttp;
import org.onap.aaf.cadi.client.Rcli;
import org.onap.aaf.cadi.client.Retryable;
import org.onap.aaf.cadi.config.Config;
import org.onap.aaf.cadi.http.HTransferSS;
import org.onap.aaf.cadi.principal.TaggedPrincipal;
import org.onap.aaf.cadi.register.Registrant;
import org.onap.aaf.cadi.util.Split;
import org.onap.aaf.misc.env.APIException;
import org.onap.aaf.misc.env.Trans;
import org.onap.aaf.misc.env.impl.BasicEnv;

public abstract class AbsService<ENV extends BasicEnv, TRANS extends Trans> extends RServlet<TRANS> {
	public final Access access;
	public final ENV env;
	private AAFConHttp aafCon;

	public final String app_name;
	public final String app_version;
	public final String app_interface_version;
	public final String ROOT_NS;

    public AbsService(final Access access, final ENV env) throws CadiException {
    		Define.set(access);
    		ROOT_NS = Define.ROOT_NS();
		this.access = access;
		this.env = env;

		String component = access.getProperty(Config.AAF_COMPONENT, null);
		final String[] locator_deploy;
		
		if(component == null) {
			locator_deploy = null;
		} else {
			locator_deploy = Split.splitTrim(':', component);
		}
			
		if(component == null || locator_deploy==null || locator_deploy.length<2) {
			throw new CadiException("AAF Component must include the " + Config.AAF_COMPONENT + " property, <fully qualified service name>:<full deployed version (i.e. 2.1.3.13)");
		}
		final String[] version = Split.splitTrim('.', locator_deploy[1]);
		if(version==null || version.length<2) {
			throw new CadiException("AAF Component Version must have at least Major.Minor version");
		}
    		app_name = Define.varReplace(locator_deploy[0]);
    		app_version = locator_deploy[1];
    		app_interface_version = version[0]+'.'+version[1];
    		
		// Print Cipher Suites Available
		if(access.willLog(Level.DEBUG)) {
			SSLContext context;
			try {
				context = SSLContext.getDefault();
			} catch (NoSuchAlgorithmException e) {
				throw new CadiException("SSLContext issue",e);
			}
			SSLSocketFactory sf = context.getSocketFactory();
			StringBuilder sb = new StringBuilder("Available Cipher Suites: ");
			boolean first = true;
			int count=0;
			for( String cs : sf.getSupportedCipherSuites()) {
				if(first)first = false;
				else sb.append(',');
				sb.append(cs);
				if(++count%4==0){sb.append('\n');}
			}
			access.log(Level.DEBUG,sb);
		}
    }

	public abstract Filter[] filters() throws CadiException,  LocatorException;


    public abstract Registrant<ENV>[] registrants(final int port) throws CadiException, LocatorException;

	// Lazy Instantiation
    public synchronized AAFConHttp aafCon() throws CadiException, LocatorException {
	    	if(aafCon==null) {
		    	if(access.getProperty(Config.AAF_URL,null)!=null) {
		    		aafCon = _newAAFConHttp();
		    	} else {
		    		throw new CadiException("AAFCon cannot be constructed without " + Config.AAF_URL);
		    	}
	    	}
	    	return aafCon;
    }
    
    /**
     * Allow to be over ridden for special cases
     * @return
     * @throws LocatorException 
     */
    protected synchronized AAFConHttp _newAAFConHttp() throws CadiException, LocatorException {
		try {
			if(aafCon==null) {
				aafCon = new AAFConHttp(access);
			} 
			return aafCon;
		} catch (APIException e) {
			throw new CadiException(e);
		}
    }
    
    // This is a method, so we can overload for AAFAPI
    public String aaf_url() {
    		return access.getProperty(Config.AAF_URL, null);
    }
    
	public Rcli<?> client() throws CadiException {
		return aafCon.client(Config.AAF_DEFAULT_VERSION);
	}

	public Rcli<?> clientAsUser(TaggedPrincipal p) throws CadiException {
		return aafCon.client(Config.AAF_DEFAULT_VERSION).forUser(
				new HTransferSS(p,app_name, aafCon.securityInfo()));
	}

	public<RET> RET clientAsUser(TaggedPrincipal p,Retryable<RET> retryable) throws APIException, LocatorException, CadiException  {
			return aafCon.hman().best(new HTransferSS(p,app_name, aafCon.securityInfo()), retryable);
	}
}