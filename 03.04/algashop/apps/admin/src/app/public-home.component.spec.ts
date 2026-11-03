import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthService } from './core/auth.service';
import { PublicHomeComponent } from './public-home.component';

describe('PublicHomeComponent', () => {
  let component: PublicHomeComponent;
  let fixture: ComponentFixture<PublicHomeComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['hasValidToken', 'login']);

    await TestBed.configureTestingModule({
      declarations: [PublicHomeComponent],
      providers: [
        { provide: AuthService, useValue: authService },
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PublicHomeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    authService.hasValidToken.and.returnValue(false);
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('should redirect to the dashboard when a valid token exists', () => {
    authService.hasValidToken.and.returnValue(true);
    fixture.detectChanges();

    expect(authService.login).toHaveBeenCalledWith('/dashboard');
  });

  it('should keep the public home visible when there is no valid token', () => {
    authService.hasValidToken.and.returnValue(false);
    fixture.detectChanges();

    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should start the oauth login flow for the dashboard', () => {
    authService.hasValidToken.and.returnValue(false);
    component.login();

    expect(authService.login).toHaveBeenCalledWith('/dashboard');
  });
});
