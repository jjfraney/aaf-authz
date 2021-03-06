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
 * * distributed under the License is distributed on an "AS IS" BASIS,Z
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 * * ============LICENSE_END====================================================
 * *
 * *
 ******************************************************************************/

package org.onap.aaf.cadi.lur.test;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.onap.aaf.cadi.lur.LocalPermission;
import org.onap.aaf.cadi.Permission;

public class JU_LocalPermission {

    @Mock
    Permission perm;

    private LocalPermission localPerm;
    private String role = "Fake Role";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(perm.getKey()).thenReturn(role);

        localPerm = new LocalPermission(role);
    }

    @Test
    public void getKeyTest() {
        assertThat(localPerm.getKey(), is(role));
    }

    @Test
    public void toStringTest() {
        assertThat(localPerm.toString(), is(role));
    }

    @Test
    public void matchTest() {
        assertTrue(localPerm.match(perm));
    }

    @Test
    public void permTypeTest() {
        assertThat(localPerm.permType(), is("LOCAL"));
    }

}
