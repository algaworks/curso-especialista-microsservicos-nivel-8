import { AuthConfig } from 'angular-oauth2-oidc';
import { OAuthModuleConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  issuer: 'http://auth.algashop.local:8081',
  redirectUri: 'http://admin.algashop.local:4200',
  silentRefreshRedirectUri: 'http://admin.algashop.local:4200/silent-refresh.html',

  clientId: 'algashop-admin-web',
  responseType: 'code',
  scope: 'openid products:read products:write products:stock:write categories:read categories:write invoices:read orders:read customers:read shopping-carts:read users:read users:write',
  useSilentRefresh: true, // Needed for Code Flow to suggest using iframe-based refreshes
  silentRefreshTimeout: 5000, // For faster testing
  timeoutFactor: 0.25, // For faster testing
  sessionChecksEnabled: false, // Can turn this to true but it may cause CSP issues
  showDebugInformation: true, // Also requires enabling "Verbose" level in devtools
  clearHashAfterLogin: true,
  nonceStateSeparator: 'semicolon',
  oidc: true,
  disablePKCE: false,
  requireHttps: false, // Only for testing. In production, this should be `true`.
};

// Resource Server Configuration for development
export const authModuleConfig: OAuthModuleConfig = {
  resourceServer: {
    allowedUrls: [
      'http://admin-api.algashop.local:9998/api/v1',
      'http://auth.algashop.local:8081/api/v1'
    ],
    sendAccessToken: true,
  }
};

export const environment = {
  production: false,
  apiUrl: 'http://admin-api.algashop.local:9998',
  authUrl: 'http://auth.algashop.local:8081',
  showDashboardExtraCards: false,
  authConfig,
  authModuleConfig
};
