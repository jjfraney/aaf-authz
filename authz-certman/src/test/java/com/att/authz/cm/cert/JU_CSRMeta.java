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
package com.att.authz.cm.cert;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.att.cadi.cm.CertException;
import com.att.inno.env.Trans;

@RunWith(MockitoJUnitRunner.class)
public class JU_CSRMeta {
	
	private static CSRMeta csrmeta;
	private static Trans trans;
	private static PKCS10CertificationRequest req;
	
	@BeforeClass
	public static void setUp() {
		trans = mock(Trans.class);
		csrmeta = new CSRMeta();
		csrmeta.cn("CN");
		csrmeta.email("pupleti@ht.com");
		csrmeta.mechID("HAKJH787");
		csrmeta.o("O");
		csrmeta.l("L");
		csrmeta.st("ST");
		csrmeta.c("C");
		csrmeta.challenge("Challenge");
		csrmeta.san("CA");
	}
	
	@Test
	public void x500Name() throws IOException {
		
		X500Name x500 = csrmeta.x500Name();
		assertEquals(x500.toString(),"CN=CN,E=pupleti@ht.com,OU=HAKJH787,O=O,L=L,ST=ST,C=C");
	}
	
	@Test
	public void initialConversationCert() throws CertificateException, OperatorCreationException, IOException {
		X509Certificate cert = csrmeta.initialConversationCert(trans);
		assertEquals(cert.getBasicConstraints(),-1);
	}
	
	@Test
	public void generateCSR() throws IOException, CertException {
		req = csrmeta.generateCSR(trans);
		assertNotNull(req);
	}
	
	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	@Test
	public void dump() throws IOException, CertException {
		req = csrmeta.generateCSR(trans);
		csrmeta.dump(req);
	}
	
}
