package com.gameflix.auth.service;

import com.gameflix.auth.model.Subscription;
import com.gameflix.auth.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Optional<Subscription> getActiveSubscription(String username) {
        return subscriptionRepository.findByUsernameAndStatus(username, Subscription.Status.ACTIVE);
    }

    /**
     * Start or change a plan. A user only ever has one ACTIVE subscription, so
     * if one already exists we move it to the new plan instead of creating a
     * second active row.
     */
    public Subscription subscribe(String username, Subscription.Plan plan) {
        return getActiveSubscription(username)
                .map(existing -> {
                    existing.setPlan(plan);
                    return subscriptionRepository.save(existing);
                })
                .orElseGet(() -> subscriptionRepository.save(new Subscription(username, plan)));
    }

    public void cancel(String username) {
        getActiveSubscription(username).ifPresent(sub -> {
            sub.setStatus(Subscription.Status.CANCELLED);
            subscriptionRepository.save(sub);
        });
    }
}
