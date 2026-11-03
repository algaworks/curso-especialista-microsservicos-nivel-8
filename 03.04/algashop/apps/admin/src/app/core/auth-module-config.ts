import { OAuthModuleConfig } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';

export const authModuleConfig: OAuthModuleConfig = {
  resourceServer: {
    allowedUrls: [`${environment.apiUrl}/api/v1`, `${environment.authUrl}/api/v1/users`],
    sendAccessToken: true,
  }
};
