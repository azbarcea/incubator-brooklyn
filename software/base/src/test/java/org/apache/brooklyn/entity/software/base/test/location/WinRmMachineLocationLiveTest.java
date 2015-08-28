/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.brooklyn.entity.software.base.test.location;

import static org.testng.Assert.assertEquals;

import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.internal.BrooklynProperties;
import org.apache.brooklyn.core.mgmt.internal.ManagementContextInternal;
import org.apache.brooklyn.core.test.BrooklynAppLiveTestSupport;
import org.apache.brooklyn.core.test.entity.LocalManagementContextForTests;
import org.apache.brooklyn.core.test.entity.TestApplication;
import org.apache.brooklyn.location.jclouds.JcloudsLocation;

import io.cloudsoft.winrm4j.winrm.WinRmToolResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.apache.brooklyn.location.jclouds.JcloudsWinRmMachineLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class WinRmMachineLocationLiveTest {
    
    // FIXME failing locally with:
    //   Caused by: Traceback (most recent call last):
    //     File "__pyclasspath__/winrm/__init__.py", line 40, in run_cmd
    //     File "__pyclasspath__/winrm/protocol.py", line 118, in open_shell
    //     File "__pyclasspath__/winrm/protocol.py", line 190, in send_message
    //     File "__pyclasspath__/winrm/transport.py", line 112, in send_message
    //     winrm.exceptions.WinRMTransportError: 500 WinRMTransport. [Errno 20001] getaddrinfo failed
    //     at org.python.core.PyException.doRaise(PyException.java:226)

    private static final Logger LOG = LoggerFactory.getLogger(BrooklynAppLiveTestSupport.class);

    protected JcloudsLocation loc;
    protected TestApplication app;
    protected ManagementContextInternal mgmt;

    private JcloudsWinRmMachineLocation machine;
    
    @BeforeClass(alwaysRun=true)
    public void setUpClass() throws Exception {
        mgmt = new LocalManagementContextForTests(BrooklynProperties.Factory.newDefault());
        JcloudsLocation loc = (JcloudsLocation) mgmt.getLocationRegistry().resolve("jclouds:aws-ec2:us-west-2", ImmutableMap.<String, Object>builder()
                .put("inboundPorts", ImmutableList.of(5985, 3389))
                .put("displayName", "AWS Oregon (Windows)")
                .put("imageOwner", "801119661308")
                .put("imageNameRegex", "Windows_Server-2012-R2_RTM-English-64Bit-Base-.*")
                .put("hardwareId", "m3.medium")
                .put("useJcloudsSshInit", false)
                .build());
        machine = (JcloudsWinRmMachineLocation) loc.obtain();
    }

    @AfterClass(alwaysRun=true)
    public void tearDownClass() throws Exception {
        try {
            if (machine != null) loc.release(machine);
            if (mgmt != null) Entities.destroyAll(mgmt);
        } catch (Throwable t) {
            LOG.error("Caught exception in tearDown method", t);
        } finally {
            mgmt = null;
        }
    }

    @Test(groups="Live")
    public void testExecScript() throws Exception {
        WinRmToolResponse response = machine.executeScript("echo true");
        assertEquals(response.getStatusCode(), 0);
        assertEquals(response.getStdErr(), "");
    }
}
