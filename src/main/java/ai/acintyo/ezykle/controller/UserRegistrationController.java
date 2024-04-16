package ai.acintyo.ezykle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.acintyo.ezykle.bindings.AuthenticationResponse;
import ai.acintyo.ezykle.bindings.UserRegistrationForm;
import ai.acintyo.ezykle.model.ApiResponse;
import ai.acintyo.ezykle.services.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserRegistrationController {

	@Autowired
	private UserRegistrationService registrationService;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> getUserData(
			@RequestBody @Valid UserRegistrationForm userInfo) {

		log.info("ai.acintyo.ezykle.controller.UserRegistrationController::Attempting to register new user");

			AuthenticationResponse result = registrationService.saveRegistration(userInfo);
			log.info("ai.acintyo.ezykle.controller.UserRegistrationController::User Registration Successfull");
			return ResponseEntity.ok(result);
		

	}

	@PostMapping("/login")
	public AuthenticationResponse login(@RequestBody UserRegistrationForm request) {
		return registrationService.authenticate(request);
	}
}
