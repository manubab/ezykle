package ai.acintyo.ezykle.services;

import org.springframework.http.ResponseEntity;

import ai.acintyo.ezykle.bindings.AuthenticationResponse;
import ai.acintyo.ezykle.bindings.UserRegistrationForm;
import ai.acintyo.ezykle.entities.EzUserRegistration;

public interface UserRegistrationService {

	AuthenticationResponse saveRegistration(UserRegistrationForm registrationForm);

	AuthenticationResponse authenticate(UserRegistrationForm request);

	
}
