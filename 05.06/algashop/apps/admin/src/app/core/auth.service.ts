/* eslint-disable brace-style */

import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthErrorEvent, OAuthService } from 'angular-oauth2-oidc';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private isAuthenticatedSubject$ = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject$.asObservable();

  private isDoneLoadingSubject$ = new BehaviorSubject<boolean>(false);
  public isDoneLoading$ = this.isDoneLoadingSubject$.asObservable();

  /**
   * Publishes `true` if and only if (a) all the asynchronous initial
   * login calls have completed or errorred, and (b) the user ended up
   * being authenticated.
   *
   * In essence, it combines:
   *
   * - the latest known state of whether the user is authorized
   * - whether the ajax calls for initial log in have all been done
   */
  public canActivateProtectedRoutes$: Observable<boolean> = combineLatest([
    this.isAuthenticated$,
    this.isDoneLoading$
  ]).pipe(map(values => values.every(b => b)));

  private isPublicHomeRoute(): boolean {
    const currentUrl = (this.router.url || window.location.pathname || '/').split('?')[0].split('#')[0];
    return currentUrl === '/';
  }

  private navigateToPublicHome() {
    if (this.isPublicHomeRoute()) {
      return;
    }

    this.router.navigateByUrl('/');
  }

  constructor(
    private oauthService: OAuthService,
    private router: Router,
  ) {
    // Useful for debugging:
    this.oauthService.events.subscribe(event => {
      if (event instanceof OAuthErrorEvent) {
        console.error('OAuthErrorEvent Object:', event);
        this.logout(); //todo if erro type
      } else {
        console.warn('OAuthEvent Object:', event);
      }
    });

    // THe following cross-tab communication of fresh access tokens works usually in practice,
    // but if you need more robust handling the community has come up with ways to extend logic
    // in the library which may give you better mileage.
    //
    // See: https://github.com/jeroenheijmans/sample-angular-oauth2-oidc-with-auth-guards/issues/2
    //
    // Until then we'll stick to this:
    window.addEventListener('storage', (event) => {
      // The `key` is `null` if the event was caused by `.clear()`
      if (event.key !== 'access_token' && event.key !== null) {
        return;
      }

      console.warn('Noticed changes to access_token (most likely from another tab), updating isAuthenticated');
      this.isAuthenticatedSubject$.next(this.oauthService.hasValidAccessToken());

      if (!this.oauthService.hasValidAccessToken()) {
        this.navigateToPublicHome();
      }
    });

    this.oauthService.events
      .subscribe(_ => {
        this.isAuthenticatedSubject$.next(this.oauthService.hasValidAccessToken());
      });
    this.isAuthenticatedSubject$.next(this.oauthService.hasValidAccessToken());

    this.oauthService.events
      .pipe(filter(e => ['token_received'].includes(e.type)))
      .subscribe(e => this.oauthService.loadUserProfile());

    this.oauthService.events
      .pipe(filter(e => ['session_terminated', 'session_error'].includes(e.type)))
      .subscribe(e => this.navigateToPublicHome());

    this.oauthService.setupAutomaticSilentRefresh();
  }

  public runInitialLoginSequence(): Promise<void> {
    if (location.hash) {
      console.log('Encountered hash fragment, plotting as table...');
      console.table(location.hash.substr(1).split('&').map(kvp => kvp.split('=')));
    }

    return this.oauthService.loadDiscoveryDocument() //loadDiscoveryDocumentAndLogin?
      .then(() => this.oauthService.tryLogin())
      .then(() => {
        this.isDoneLoadingSubject$.next(true);

        if (this.oauthService.hasValidAccessToken()
            && this.oauthService.state
            && this.oauthService.state !== 'undefined'
            && this.oauthService.state !== 'null') {
          let stateUrl = this.oauthService.state;
          if (stateUrl.startsWith('/') === false) {
            stateUrl = decodeURIComponent(stateUrl);
          }
          console.log(`There was state of ${this.oauthService.state}, so we are sending you to: ${stateUrl}`);
          this.router.navigateByUrl(stateUrl);
        }
      })
      .catch((e) => {
        console.error(e);
        this.isDoneLoadingSubject$.next(true);
        return Promise.resolve();
      });
  }

  public login(targetUrl?: string): Promise<void> {
    // Note: before version 9.1.0 of the library you needed to
    // call encodeURIComponent on the argument to the method.
    const redirectUrl = targetUrl || this.router.url;

    if (this.hasValidToken()) {
      this.router.navigateByUrl(redirectUrl);
      return Promise.resolve();
    }

    return this.oauthService.initLoginFlowInPopup()
      .then(() => {
        if (this.hasValidToken()) {
          this.router.navigateByUrl(redirectUrl);
        }
      })
      .catch((e) => {
        console.error(e);
      });
  }

  public logout() { this.oauthService.logOut(); }
  public refresh() { this.oauthService.refreshToken(); }
  public silentRefresh() { this.oauthService.silentRefresh(); }
  public hasValidToken() { return this.oauthService.hasValidAccessToken(); }

  // These normally won't be exposed from a service like this, but
  // for debugging it makes sense.
  public get accessToken() { return this.oauthService.getAccessToken(); }
  public get refreshToken() { return this.oauthService.getRefreshToken(); }
  public get identityClaims() { return this.oauthService.getIdentityClaims(); }
  public get idToken() { return this.oauthService.getIdToken(); }
  public get logoutUrl() { return this.oauthService.logoutUrl; }
  public get jwks() { return this.oauthService.jwks; }
  public get userEmail() {
    return this.oauthService.getIdentityClaims().email;
  }
  public get userName() {
    return this.oauthService.getIdentityClaims().fullName;
  }
}
