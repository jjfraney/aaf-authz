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

package org.onap.aaf.misc.xgen.xml;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.onap.aaf.misc.env.APIException;
import org.onap.aaf.misc.xgen.Code;

public class JU_XMLCacheGenTest {

    @Mock
    Writer writer;

    @Mock
    Code code;

    @Before
    public void setup() {

        code = mock(Code.class);
        writer = mock(Writer.class);
    }

    @Test
    public void test() throws APIException, IOException {
        XMLCacheGen cacheGen = new XMLCacheGen(0, code);
        assertEquals(cacheGen.PRETTY, 1);

        XMLGen xgen = cacheGen.create(1, writer);
        assertEquals(0, xgen.getIndent());

        xgen.setIndent(10);
        assertEquals(10, xgen.getIndent());
        xgen.comment("Comment");
    }

}
