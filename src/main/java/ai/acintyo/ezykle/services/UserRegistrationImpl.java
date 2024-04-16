package ai.acintyo.ezykle.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ai.acintyo.ezykle.bindings.AuthenticationResponse;
import ai.acintyo.ezykle.bindings.UserRegistrationForm;
import ai.acintyo.ezykle.entities.EzUserAccount;
import ai.acintyo.ezykle.entities.EzUserRegistration;
import ai.acintyo.ezykle.entities.Token;
import ai.acintyo.ezykle.jwtservice.JwtService;
import ai.acintyo.ezykle.repositories.TokenRepository;
import ai.acintyo.ezykle.repositories.UserRegistrationRepo;
import ai.acintyo.ezykle.util.EncodingData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@NoArgsConstructor@AllArgsConstructor
public class UserRegistrationImpl implements UserRegistrationService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private  UserRegistrationRepo registrationRepo;

	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private  JwtService jwtService;
	@Autowired
	private  AuthenticationManager authenticationManager;

	@Override
	public AuthenticationResponse saveRegistration(UserRegistrationForm registrationForm) {

		if (registrationRepo.findByEmail(registrationForm.getEmail()).isPresent()) {
			return new AuthenticationResponse(null, "User already exist");
		}

		EzUserRegistration ezUserRegistration = new EzUserRegistration();

		ezUserRegistration.setName(registrationForm.getName());
		ezUserRegistration.setMobile(registrationForm.getMobileNumber());
		ezUserRegistration.setEmail(registrationForm.getEmail());
		ezUserRegistration.setPassword(passwordEncoder.encode(registrationForm.getPassword()));
		ezUserRegistration.setConfirmPassword(passwordEncoder.encode(registrationForm.getConfirmPassword()));
		ezUserRegistration.setRegistrationDate(LocalDate.now());

		EzUserAccount ezUserAccount = new EzUserAccount();

		ezUserAccount.setBankName(passwordEncoder.encode(registrationForm.getBankName()));
		ezUserAccount.setAccountNumber(passwordEncoder.encode(registrationForm.getAccountNumber()));
		ezUserAccount.setIfscCode(passwordEncoder.encode(registrationForm.getIfscCode()));
		ezUserAccount.setBranch(passwordEncoder.encode(registrationForm.getAccountNumber()));
		ezUserAccount.setRegistrationDate(LocalDate.now());

		ezUserRegistration.setUserAccount(ezUserAccount);

		String jwt = jwtService.generateToken(ezUserRegistration);
		saveUserToken(jwt, ezUserRegistration);
		return new AuthenticationResponse(jwt, "User registration was successful");

	}

	public AuthenticationResponse authenticate(UserRegistrationForm registrationForm) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(registrationForm.getEmail(), registrationForm.getPassword()));

		EzUserRegistration user = registrationRepo.findByEmail(registrationForm.getEmail()).orElseThrow();

		String jwt = jwtService.generateToken(user);
		
		System.out.println(user+jwt);

		revokeAllTokenByUser(user);
		saveUserToken(jwt, user);

		return new AuthenticationResponse(jwt, "User login was successful");
	}

	private void revokeAllTokenByUser(EzUserRegistration user) {
		List<Token> validTokens = tokenRepository.findAllTokensByUserId(user.getId());
		if (validTokens.isEmpty()) {
			return;
		}

		validTokens.forEach(t -> {
			t.setLoggedOut(true);
		});

		tokenRepository.saveAll(validTokens);
	}

	private void saveUserToken(String jwt, EzUserRegistration user) {
		Token token = new Token();
		token.setToken(jwt);
		token.setLoggedOut(false);
		token.setUser(user);
		tokenRepository.save(token);
	}
}
