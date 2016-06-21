package com.shyla.security;

import android.content.Context;
import android.util.Log;

import junit.framework.Assert;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SecurityUtils {

    private static final String TAG = SecurityUtils.class.getSimpleName();

    public static void parseBKSCert(Context context) {
        String password = "ca1234";
        String fileName = "ca.bks";

        InputStream inputStream = getAssetFile(context, fileName);
        KeyStore keystore = getKeystore(inputStream, password);
        Log.v("keyStore, ", "provider : " + keystore.getProvider().toString() + ", type : " + keystore.getType());

        echoKeystore(keystore);
    }

    public static void parseLeseeCert(Context context) {
        String password = "changme";
        String fileName = "mycar";

        InputStream inputStream = getAssetFile(context, fileName);
        KeyStore keystore = getKeystore(inputStream, password);
        Log.v("keyStore, ", "provider : " + keystore.getProvider().toString() + ", type : " + keystore.getType());

        echoKeystore(keystore);
    }

    // error, java.io.IOException: Wrong version of key store.
    public static void parseKeystore(Context context) {
        String password = "ca1234";
        String fileName = "ca.keystore";

        InputStream inputStream = getAssetFile(context, fileName);
        KeyStore keystore = getKeystore(inputStream, password);

        echoKeystore(keystore);
    }

    public static void parseTeslaKeystore(Context context) {
        String password = "qXD5wUA3qVySNr39Nc8sFEtKXUr3Mg";
        String fileName = "tesla_trust.keystore";

        InputStream inputStream = getAssetFile(context, fileName);

        KeyStore keystore = getKeystore(inputStream, password);
        echoKeystore(keystore);
    }

    private static void echoKeystore(KeyStore keystore) {
        try {
            Enumeration<String> enumeration = keystore.aliases();
            while (enumeration.hasMoreElements()) {
                String aliase = enumeration.nextElement();
                Certificate certificate = keystore.getCertificate(aliase);
                PublicKey key = certificate.getPublicKey();
                byte[] encodebyte = key.getEncoded();

                Log.v("keyStore, aliase : ", aliase + ", type : " + certificate.getType() + ", algorithm : " + key.getAlgorithm() + ", format : " + key.getFormat());
                Log.v(TAG, "encode : " + byte2hex(encodebyte));
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    private static KeyStore getKeystore(InputStream inputStream, String password) {
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(inputStream, password.toCharArray());
            inputStream.close();
            return keystore;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return null;
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

    private static KeyPair getKeyPair(InputStream inputStream, final String password) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            PEMReader reader = new PEMReader(inputStreamReader,
                    new PasswordFinder() {
                        @Override
                        public char[] getPassword() {
                            return password.toCharArray();
                        }
                    }
            );

            KeyPair key = (KeyPair) reader.readObject();
            reader.close();
            return key;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static X509Certificate readX509Certificate(Context context, String crtFileName) {
        try {
            PEMReader reader = new PEMReader(new InputStreamReader(getAssetFile(context, crtFileName)));
            X509Certificate cert = (X509Certificate) reader.readObject();
            reader.close();
            return cert;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            String password = "ca1234";
            String fileName = "ca.bks";

            InputStream inputStream = getAssetFile(context, fileName);
            KeyStore keystore = getKeystore(inputStream, password);
            Log.v("keyStore, ", "provider : " + keystore.getProvider().toString() + ", type : " + keystore.getType());

            // just for debug, log output
            echoKeystore(keystore);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
            trustManagerFactory.init(keystore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            for (TrustManager trustManager : trustManagers) {
                Log.v(TAG, "trustManager : " + trustManager.toString());
            }

            /////////////////////////////////
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keystore, password.toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            for (KeyManager keyManager : keyManagers) {
                Log.v(TAG, "keyManager : " + keyManager.toString());
            }

            ////////////////////////////////
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagers, trustManagers, null);

            return sslContext.getSocketFactory();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void testSslRequest() {
        try {
            URL url = new URL("https://www.360.com/");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Log.v(TAG, in.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testCommonRequest() {
        try {
            URL url = new URL("https://www.alipay.com/");
            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Log.v(TAG, in.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String byte2hex(byte[] buffer) {
        String h = "";

        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }

        return h;
    }
}
