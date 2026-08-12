package com.gameflix.auth.model;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Links a user (by username, matching how auth identifies people) to a plan.
 * A user has at most one ACTIVE subscription; cancelling flips the status
 * rather than deleting the row so we keep a basic billing trail.
 */
@Entity
@Table(name = "subscriptions")
public class Subscription {

    /** The three plan tiers GameFlix offers, each with a monthly price. */
    public enum Plan {
        BASIC(4.99),
        STANDARD(9.99),
        PREMIUM(14.99);

        private final double monthlyPrice;

        Plan(double monthlyPrice) {
            this.monthlyPrice = monthlyPrice;
        }

        public double getMonthlyPrice() {
            return monthlyPrice;
        }
    }

    public enum Status {
        ACTIVE, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Column(name = "monthly_price", nullable = false)
    private double monthlyPrice;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    public Subscription() {
    }

    public Subscription(String username, Plan plan) {
        this.username = username;
        this.plan = plan;
        this.monthlyPrice = plan.getMonthlyPrice();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
        this.monthlyPrice = plan.getMonthlyPrice();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getMonthlyPrice() {
        return monthlyPrice;
    }

    public Instant getStartedAt() {
        return startedAt;
    }
}
