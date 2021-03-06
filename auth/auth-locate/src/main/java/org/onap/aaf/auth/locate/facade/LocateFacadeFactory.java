/**
\\ * ============LICENSE_START====================================================
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

package org.onap.aaf.auth.locate.facade;

import org.onap.aaf.auth.env.AuthzEnv;
import org.onap.aaf.auth.env.AuthzTrans;
import org.onap.aaf.auth.locate.AAF_Locate;
import org.onap.aaf.auth.locate.mapper.Mapper_1_1;
import org.onap.aaf.auth.locate.service.LocateServiceImpl;
import org.onap.aaf.misc.env.APIException;
import org.onap.aaf.misc.env.Data;

import locate_local.v1_0.InRequest;
import locate_local.v1_0.Out;
import locate_local.v1_0.Error;;



public class LocateFacadeFactory {
    public static LocateFacade_1_1 v1_1(AuthzEnv env, AAF_Locate locate, AuthzTrans trans, Data.TYPE type) throws APIException {
        return new LocateFacade_1_1(
                env,
                new LocateServiceImpl<
                    InRequest,
                    Out,
                    Error>(trans,locate,new Mapper_1_1()),
                type);
    }

}
