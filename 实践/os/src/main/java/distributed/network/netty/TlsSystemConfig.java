package distributed.network.netty;

import distributed.network.common.TlsMode;

/**
 * @author errorfatal89@gmail.com
 */
public class TlsSystemConfig {
    public static final String TLS_SERVER_MODE = "tls.server.mode";
    public static final String TLS_ENABLE = "tls.enable";
    public static final String TLS_CONFIG_FILE = "tls.config.file";
    public static final String TLS_TEST_MODE_ENABLE = "tls.test.mode.enable";

    public static final String TLS_SERVER_NEED_CLIENT_AUTH = "tls.server.need.client.auth";
    public static final String TLS_SERVER_KEYPATH = "tls.server.keyPath";
    public static final String TLS_SERVER_KEYPASSWORD = "tls.server.keyPassword";
    public static final String TLS_SERVER_CERTPATH = "tls.server.certPath";
    public static final String TLS_SERVER_AUTHCLIENT = "tls.server.authClient";
    public static final String TLS_SERVER_TRUSTCERTPATH = "tls.server.trustCertPath";

    public static final String TLS_CLIENT_KEYPATH = "tls.client.keyPath";
    public static final String TLS_CLIENT_KEYPASSWORD = "tls.client.keyPassword";
    public static final String TLS_CLIENT_CERTPATH = "tls.client.certPath";
    public static final String TLS_CLIENT_AUTHSERVER = "tls.client.authServer";
    public static final String TLS_CLIENT_TRUSTCERTPATH = "tls.client.trustCertPath";

    public static boolean tlsTestModeEnable = Boolean.parseBoolean(System.getProperty(TLS_TEST_MODE_ENABLE, "false"));

    public static String tlsServerNeedClientAuth = System.getProperty(TLS_SERVER_NEED_CLIENT_AUTH, "none");

    public static String tlsServerKeyPath = System.getProperty(TLS_SERVER_KEYPATH, "");

    public static String tlsServerKeyPassword = System.getProperty(TLS_SERVER_KEYPASSWORD, "123456");

    public static String tlsServerCertPath = System.getProperty(TLS_SERVER_CERTPATH, "");

    public static boolean tlsServerAuthClient = Boolean.parseBoolean(System.getProperty(TLS_SERVER_AUTHCLIENT, "true"));

    public static String tlsServerTrustCertPath = System.getProperty(TLS_SERVER_TRUSTCERTPATH, "");

    public static String tlsClientKeyPath = System.getProperty(TLS_CLIENT_KEYPATH, "");

    public static String tlsClientKeyPassword = System.getProperty(TLS_CLIENT_KEYPASSWORD, "123456");

    public static String tlsClientCertPath = System.getProperty(TLS_CLIENT_CERTPATH, "");

    public static boolean tlsClientAuthServer = Boolean.parseBoolean(System.getProperty(TLS_CLIENT_AUTHSERVER, null));

    public static String tlsClientTrustCertPath = System.getProperty(TLS_CLIENT_TRUSTCERTPATH, "");

    public static TlsMode tlsMode = TlsMode.parse(System.getProperty(TLS_SERVER_MODE, "permissive"));

    public static String tlsConfigFile = System.getProperty(TLS_CONFIG_FILE, "tls.properties");
}

