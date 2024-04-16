package ai.acintyo.ezykle.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ai.acintyo.ezykle.entities.EzUserRegistration;

public interface UserRegistrationRepo extends JpaRepository<EzUserRegistration, Integer> {

	Optional<EzUserRegistration> findByEmail(String email);
}
