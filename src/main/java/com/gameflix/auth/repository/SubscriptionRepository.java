package com.gameflix.auth.repository;

import com.gameflix.auth.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUsername(String username);

    Optional<Subscription> findByUsernameAndStatus(String username, Subscription.Status status);
}
