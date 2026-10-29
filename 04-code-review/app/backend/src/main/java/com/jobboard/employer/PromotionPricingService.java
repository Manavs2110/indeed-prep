package com.jobboard.employer;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PromotionPricingService {

    private static final Map<PriceKey, Double> priceCache = new HashMap<>();

    /** Promoted listing price: $4.99/day, $3.99/day for long promotions, 5% off per full week. */
    public double price(String employerId, int days) {
        PriceKey key = new PriceKey(employerId, days);
        if (priceCache.containsKey(key)) {
            return priceCache.get(key);
        }

        double dailyRate = 4.99;
        if (days > 30) {
            dailyRate = 3.99;
        }
        double price = dailyRate * days;
        int discountPercent = days / 7 * 5;
        price = price - price * (discountPercent / 100);

        priceCache.put(key, price);
        return price;
    }

    public String generateCouponCode() {
        return "PROMO-" + new Random().nextInt(999999);
    }

    static class PriceKey {
        String employerId;
        int days;

        PriceKey(String employerId, int days) {
            this.employerId = employerId;
            this.days = days;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PriceKey)) return false;
            PriceKey other = (PriceKey) o;
            return employerId.equals(other.employerId) && days == other.days;
        }
    }
}
