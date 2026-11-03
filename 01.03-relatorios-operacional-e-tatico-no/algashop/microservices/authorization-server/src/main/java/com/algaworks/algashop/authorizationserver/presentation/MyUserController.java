package com.algaworks.algashop.authorizationserver.presentation;

import com.algaworks.algashop.authorizationserver.application.security.SecurityChecks;
import com.algaworks.algashop.authorizationserver.application.user.management.AuthUserManagementApplicationService;
import com.algaworks.algashop.authorizationserver.application.user.management.MyUserUpdateInput;
import com.algaworks.algashop.authorizationserver.application.user.management.PasswordManagementApplicationService;
import com.algaworks.algashop.authorizationserver.application.user.query.AuthUserOutput;
import com.algaworks.algashop.authorizationserver.application.user.query.AuthUserQueryService;
import com.algaworks.algashop.authorizationserver.infrastructure.security.check.SecurityAnnotations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class MyUserController {

	private final SecurityChecks securityCheck;
	private final AuthUserQueryService queryService;
	private final AuthUserManagementApplicationService managementService;
	private final PasswordManagementApplicationService passwordManagementApplicationService;

	@GetMapping
	@SecurityAnnotations.CanAccessOwnProfile
	public AuthUserOutput getMe() {
		return queryService.findById(securityCheck.getAuthenticatedUserId());
	}

	@PutMapping
	@SecurityAnnotations.CanAccessOwnProfile
	public AuthUserOutput updateMe(@RequestBody @Valid MyUserUpdateInput input) {
		return managementService.update(securityCheck.getAuthenticatedUserId(), input);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityAnnotations.CanDeleteOwnProfile
	public void deleteMe() {
		managementService.delete(securityCheck.getAuthenticatedUserId());
	}

	@PostMapping("/password-change")
	@SecurityAnnotations.CanAccessOwnProfile
	public void requestPasswordChange() {
		passwordManagementApplicationService.requestPasswordChange(securityCheck.getAuthenticatedUserId());
	}

}
