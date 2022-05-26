package com.panoramagl.downloaders.ssl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * This socket factory will create ssl socket that accepts self signed certificate
 *
 * @author olamy
 * @version $Id: EasySSLSocketFactory.java 765355 2009-04-15 20:59:07Z evenisse
 * $
 * @since 1.2.3
 */

public class EasySSLSocketFactory implements ProtocolSocketFactory {
    private SSLContext sslcontext = null;

    private static SSLContext createEasySSLContext() throws IOException {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new EasyX509TrustManager(null)}, null);
            return context;
        } catch (Throwable e) {
            throw new IOException(e.getMessage());
        }
    }

    private SSLContext getSSLContext() throws IOException {
        if (this.sslcontext == null) {
            this.sslcontext = createEasySSLContext();
        }
        return this.sslcontext;
    }

    // -------------------------------------------------------------------
    // javadoc in org.apache.http.conn.scheme.SocketFactory says :
    // Both Object.equals() and Object.hashCode() must be overridden
    // for the correct operation of some connection managers
    // -------------------------------------------------------------------

    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(EasySSLSocketFactory.class));
    }

    public int hashCode() {
        return EasySSLSocketFactory.class.hashCode();
    }

    @Override
    public Socket createSocket(String arg0, int arg1) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(arg0, arg1);
    }

    @Override
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
        return getSSLContext().getSocketFactory().createSocket(arg0, arg1, arg2, arg3);
    }

    @Override
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3, org.apache.commons.httpclient.params.HttpConnectionParams arg4)
            throws IOException {
        return getSSLContext().getSocketFactory().createSocket(arg0, arg1, arg2, arg3);
    }
}
