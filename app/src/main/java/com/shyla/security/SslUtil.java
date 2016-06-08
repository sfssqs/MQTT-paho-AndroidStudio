package com.shyla.security;

/**
 * Created by xiaxing on 16-6-8.
 */

import android.content.Context;

import junit.framework.Assert;

import java.io.*;
import java.security.*;
import java.security.cert.*;

import javax.net.ssl.*;

import org.bouncycastle.jce.provider.*;
import org.bouncycastle.openssl.*;

public class SslUtil {
    public static final String caCrtFile = "ca.crt";
    public static final String crtFile = "client.crt";
    public static final String keyFile = "client.key";
    public static final String password = "client321";

    public static SSLSocketFactory getSocketFactory(Context context) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        PEMReader reader = new PEMReader(new InputStreamReader(getAssetFile(context, caCrtFile)));
        X509Certificate caCert = (X509Certificate) reader.readObject();
        reader.close();

        // load client certificate
        reader = new PEMReader(new InputStreamReader(getAssetFile(context, crtFile)));
        X509Certificate cert = (X509Certificate) reader.readObject();
        reader.close();

        // load client private key
        reader = new PEMReader(
                new InputStreamReader(getAssetFile(context, keyFile)),
                new PasswordFinder() {
                    @Override
                    public char[] getPassword() {
                        return password.toCharArray();
                    }
                }
        );
        KeyPair key = (KeyPair) reader.readObject();
        reader.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", cert);
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext sslContext = SSLContext.getInstance("TLSv1");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    private static InputStream getAssetFile(Context context, String fileName) {
        Assert.assertNotNull(fileName);
        InputStream inputStream = null;

        try {
            inputStream = context.getAssets().open(fileName);
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;
    }
}
