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

package org.onap.aaf.cadi.configure;

import certman.v1_0.Artifacts.Artifact;
import certman.v1_0.CertInfo;

import org.onap.aaf.cadi.CadiException;
import org.onap.aaf.misc.env.Trans;

public interface PlaceArtifact {
	/**
	 * 
	 * @param transientInfo of the caller
	 * @param certificateInfo describing the certificate
	 * @param artifact
	 * @param machineName
	 * @return if successful, true, otherwise false
	 * @throws CadiException
	 */
    public boolean place(Trans transientInfo, CertInfo certificateInfo, Artifact artifact, String machineName) throws CadiException;
}
