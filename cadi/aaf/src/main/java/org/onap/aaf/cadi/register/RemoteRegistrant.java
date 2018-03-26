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

package org.onap.aaf.cadi.register;

import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;

import org.onap.aaf.cadi.Access;
import org.onap.aaf.cadi.CadiException;
import org.onap.aaf.cadi.Locator;
import org.onap.aaf.cadi.LocatorException;
import org.onap.aaf.cadi.Access.Level;
import org.onap.aaf.cadi.aaf.v2_0.AAFCon;
import org.onap.aaf.cadi.client.Future;
import org.onap.aaf.cadi.client.Rcli;
import org.onap.aaf.cadi.client.Result;
import org.onap.aaf.cadi.config.Config;
import org.onap.aaf.cadi.locator.PropertyLocator;
import org.onap.aaf.cadi.util.Split;
import org.onap.aaf.misc.env.APIException;
import org.onap.aaf.misc.env.impl.BasicEnv;
import org.onap.aaf.misc.rosetta.env.RosettaDF;

import locate.v1_0.MgmtEndpoint;
import locate.v1_0.MgmtEndpoints;

public class RemoteRegistrant<ENV extends BasicEnv> implements Registrant<ENV> {
	private final MgmtEndpoint mep;
	private final MgmtEndpoints meps;
	private final AAFCon<HttpURLConnection> aafcon;
	private final RosettaDF<MgmtEndpoints> mgmtEndpointsDF;
	private final Locator<URI> locator;
	private final Access access;
	private final int timeout;

	@SafeVarargs
	public RemoteRegistrant(AAFCon<HttpURLConnection> aafcon, String name, String version, int port, RemoteRegistrant<ENV> ... others) throws CadiException, LocatorException {
		this.aafcon = aafcon;
		access = aafcon.access;
		try {
			mgmtEndpointsDF = aafcon.env.newDataFactory(MgmtEndpoints.class);
		} catch (APIException e1) {
			throw new CadiException(e1);
		}
		timeout = Integer.parseInt(access.getProperty(Config.AAF_CONN_TIMEOUT, Config.AAF_CONN_TIMEOUT_DEF));
		String aaf_locate = access.getProperty(Config.AAF_LOCATE_URL,null);
		if(aaf_locate==null) {
			throw new CadiException(Config.AAF_LOCATE_URL + " is required.");
		} else {
			// Note: want Property Locator, not AAFLocator, because we want the core service, not what it can find
			locator = new PropertyLocator(aaf_locate);
		}
		
		mep = new MgmtEndpoint();
		mep.setName(name);
		mep.setPort(port);
		
		try {
			String latitude = access.getProperty(Config.CADI_LATITUDE, null);
			if(latitude==null) {
				latitude = access.getProperty("AFT_LATITUDE", null);
			}
			String longitude = access.getProperty(Config.CADI_LONGITUDE, null);
			if(longitude==null) {
				longitude = access.getProperty("AFT_LONGITUDE", null);
			}
			if(latitude==null || longitude==null) {
				throw new CadiException(Config.CADI_LATITUDE + " and " + Config.CADI_LONGITUDE + " is required");
			} else {
				mep.setLatitude(Float.parseFloat(latitude));
				mep.setLongitude(Float.parseFloat(longitude));
			}
			String split[] = Split.split('.', version);
			mep.setPkg(split.length>3?Integer.parseInt(split[3]):0);
			mep.setPatch(split.length>2?Integer.parseInt(split[2]):0);
			mep.setMinor(split.length>1?Integer.parseInt(split[1]):0);
			mep.setMajor(split.length>0?Integer.parseInt(split[0]):0);
			
			mep.setHostname(access.getProperty(Config.HOSTNAME, Inet4Address.getLocalHost().getHostName()));
			String subprotocols = access.getProperty(Config.CADI_PROTOCOLS, null);
			if(subprotocols==null) {
				mep.setProtocol("http");
			} else {
				mep.setProtocol("https");
				for(String s : Split.split(',', subprotocols)) {
					mep.getSubprotocol().add(s);
				}
			}
		} catch (NumberFormatException | UnknownHostException e) {
			throw new CadiException("Error extracting Data from Properties for Registrar",e);
		}
		meps = new MgmtEndpoints();
		meps.getMgmtEndpoint().add(mep);
		for(RemoteRegistrant<ENV> rr : others) {
			meps.getMgmtEndpoint().add(rr.mep);
		}
	}
	
	@Override
	public Result<Void> update(ENV env) {
		try {
			Rcli<?> client = aafcon.client(locator);
			try {
				Future<MgmtEndpoints> fup = client.update("/registration",mgmtEndpointsDF,meps);
				if(fup.get(timeout)) {
					access.log(Level.INFO, "Registration complete to",client.getURI());
					return Result.ok(fup.code(),null);
				} else {
					access.log(Level.ERROR,"Error registering to AAF Locator on ", client.getURI());
					return Result.err(fup.code(),fup.body());
				}
			} catch (APIException e) {
				access.log(e, "Error registering service to AAF Locator");
				return Result.err(503,e.getMessage());
			}
			
		} catch (CadiException e) {
			return Result.err(503,e.getMessage());
		}
	}

	@Override
	public Result<Void> cancel(ENV env) {
		try {
			Rcli<?> client = aafcon.client(locator);
			try {
				Future<MgmtEndpoints> fup = client.delete("/registration",mgmtEndpointsDF,meps);
				if(fup.get(timeout)) {
					access.log(Level.INFO, "Deregistration complete on",client.getURI());
					return Result.ok(fup.code(),null);
				} else {
					return Result.err(fup.code(),fup.body());
				}
			} catch (APIException e) {
				access.log(e, "Error deregistering service on AAF Locator");
				return Result.err(503,e.getMessage());
			}
			
		} catch (CadiException e) {
			return Result.err(503,e.getMessage());
		}
	}

}
