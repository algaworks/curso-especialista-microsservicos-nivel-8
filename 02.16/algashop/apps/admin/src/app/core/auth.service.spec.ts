import { fakeAsync, TestBed, waitForAsync } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { EventType, OAuthEvent, OAuthService, OAuthSuccessEvent } from 'angular-oauth2-oidc';
import { Subject } from 'rxjs';
import { AuthService } from './auth.service';

class FakeComponent {}

describe('AuthService', () => {
  let mockOAuthEvents: Subject<OAuthEvent>;
  let mockOAuthService: jasmine.SpyObj<OAuthService>;
  let router: Router;
  let routerUrlSpy: jasmine.Spy;

  beforeEach(() => {
    mockOAuthEvents = new Subject<OAuthEvent>();
    mockOAuthService = jasmine.createSpyObj<OAuthService>({
      configure: void 0,
      initLoginFlow: void 0,
      initLoginFlowInPopup: Promise.resolve(),
      hasValidAccessToken: false,
      loadDiscoveryDocument: Promise.resolve(new OAuthSuccessEvent('discovery_document_loaded')),
      loadDiscoveryDocumentAndLogin: Promise.resolve(false),
      loadDiscoveryDocumentAndTryLogin: Promise.resolve(false),
      loadUserProfile: Promise.resolve({ }),
      restartSessionChecksIfStillLoggedIn: void 0,
      setupAutomaticSilentRefresh: void 0,
      silentRefresh: Promise.resolve(new OAuthSuccessEvent('silently_refreshed')),
      stopAutomaticRefresh: void 0,
      tryLogin: Promise.resolve(false),
      tryLoginCodeFlow: Promise.resolve(void 0),
      tryLoginImplicitFlow: Promise.resolve(false),
    }, {
      events: mockOAuthEvents.asObservable(),
    });

    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([
        { path: 'should-login', component: FakeComponent },
      ])],
      providers: [
        AuthService,
        { provide: OAuthService, useValue: mockOAuthService },
      ]
    });
    router = TestBed.inject(Router);
    routerUrlSpy = spyOnProperty(router, 'url', 'get').and.returnValue('/');
    spyOn(router, 'navigateByUrl');
  });

  describe('constructor', () => {
    // This set of tests needs to .inject(AuthService) after some
    // additional 'Arrange' phase stuff. So each test does it on
    // its own.

    it('should initialize isAuthenticated$ based on hasValidAccessToken===false', fakeAsync(() => {
      let latestIsAuthenticatedValue: any = null;
      mockOAuthService.hasValidAccessToken.and.returnValue(false);
      const service = TestBed.inject(AuthService);
      service.isAuthenticated$.subscribe(x => latestIsAuthenticatedValue = x);
      expect(latestIsAuthenticatedValue).toEqual(false);
    }));

    it('should initialize isAuthenticated$ based on hasValidAccessToken===true', fakeAsync(() => {
      let latestIsAuthenticatedValue: any = null;
      mockOAuthService.hasValidAccessToken.and.returnValue(true);
      const service = TestBed.inject(AuthService);
      service.isAuthenticated$.subscribe(x => latestIsAuthenticatedValue = x);
      expect(latestIsAuthenticatedValue).toEqual(true);
    }));
  });

  describe('in general', () => {
    let service: AuthService;
    beforeEach(() => service = TestBed.inject(AuthService));

    it('should react on OAuthService events', () => {
      mockOAuthEvents.next({type: 'silently_refreshed'});
      mockOAuthEvents.next({type: 'token_received'});

      expect(mockOAuthService.loadUserProfile).toHaveBeenCalled();
      expect(mockOAuthService.hasValidAccessToken).toHaveBeenCalledTimes(3); // one extra time in constructor
    });

    ['session_terminated', 'session_error'].forEach(eventType => {
      it(`should react on OAuthService event ${eventType} and navigate to public home`, () => {
        routerUrlSpy.and.returnValue('/dashboard');
        mockOAuthEvents.next({type: eventType as EventType});

        expect(router.navigateByUrl).toHaveBeenCalledWith('/');
        expect(mockOAuthService.initLoginFlowInPopup).not.toHaveBeenCalled();
      });
    });

    it('should handle storage event and update isAuthenticated status', () => {
      routerUrlSpy.and.returnValue('/dashboard');
      mockOAuthService.hasValidAccessToken.and.returnValue(false);

      window.dispatchEvent(new StorageEvent('storage', {key: 'access_token'}));

      expect(router.navigateByUrl).toHaveBeenCalledWith('/');
      expect(mockOAuthService.initLoginFlowInPopup).not.toHaveBeenCalled();
      service.isAuthenticated$.subscribe(isAuthenticated => expect(isAuthenticated).toBe(false));
    });

    it('should not start login flow on the public home route', () => {
      routerUrlSpy.and.returnValue('/');
      mockOAuthService.hasValidAccessToken.and.returnValue(false);

      window.dispatchEvent(new StorageEvent('storage', {key: 'access_token'}));
      mockOAuthEvents.next({type: 'session_error' as EventType});

      expect(mockOAuthService.initLoginFlowInPopup).not.toHaveBeenCalled();
      expect(router.navigateByUrl).not.toHaveBeenCalled();
    });
  });

  describe('login', () => {
    let service: AuthService;
    beforeEach(() => service = TestBed.inject(AuthService));

    it('should navigate after popup login when token becomes valid', waitForAsync(() => {
      mockOAuthService.hasValidAccessToken.and.returnValues(false, true);

      service.login('/dashboard').then(() => {
        expect(mockOAuthService.initLoginFlowInPopup).toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledWith('/dashboard');
      });
    }));

    it('should keep current route after popup login when token is still invalid', waitForAsync(() => {
      mockOAuthService.hasValidAccessToken.and.returnValue(false);

      service.login('/dashboard').then(() => {
        expect(mockOAuthService.initLoginFlowInPopup).toHaveBeenCalled();
        expect(router.navigateByUrl).not.toHaveBeenCalled();
      });
    }));

    it('should keep current route when popup login fails', waitForAsync(() => {
      mockOAuthService.hasValidAccessToken.and.returnValue(false);
      mockOAuthService.initLoginFlowInPopup.and.returnValue(Promise.reject('popup closed'));

      service.login('/dashboard').then(() => {
        expect(mockOAuthService.initLoginFlowInPopup).toHaveBeenCalled();
        expect(router.navigateByUrl).not.toHaveBeenCalled();
      });
    }));

    it('should navigate directly when token is valid', () => {
      mockOAuthService.hasValidAccessToken.and.returnValue(true);

      service.login('/dashboard');

      expect(router.navigateByUrl).toHaveBeenCalledWith('/dashboard');
      expect(mockOAuthService.initLoginFlowInPopup).not.toHaveBeenCalled();
    });
  });

  describe('runInitialLoginSequence', () => {
    let service: AuthService;
    beforeEach(() => service = TestBed.inject(AuthService));

    it('should login via hash if token is valid', waitForAsync (() => {
      mockOAuthService.hasValidAccessToken.and.returnValue(true);

      service.runInitialLoginSequence().then(() => {
        expect(mockOAuthService.tryLogin).toHaveBeenCalled();
        expect(mockOAuthService.silentRefresh).not.toHaveBeenCalled();
      });
    }));

    it('should navigate to state url after login callback with a valid token', waitForAsync (() => {
      mockOAuthService.state = '/some/url';
      mockOAuthService.hasValidAccessToken.and.returnValue(true);

      service.runInitialLoginSequence().then(() => {
        expect(mockOAuthService.tryLogin).toHaveBeenCalled();
        expect(mockOAuthService.silentRefresh).not.toHaveBeenCalled();
        expect(router.navigateByUrl).toHaveBeenCalledWith('/some/url');
      });
    }));

    it('should finish loading without silent refresh when there is no active token', waitForAsync (() => {
      mockOAuthService.hasValidAccessToken.and.returnValue(false);

      service.runInitialLoginSequence().then(() => {
        expect(mockOAuthService.tryLogin).toHaveBeenCalled();
        expect(mockOAuthService.silentRefresh).not.toHaveBeenCalled();
        expect(router.navigateByUrl).not.toHaveBeenCalled();
      });
    }));
  });
});
