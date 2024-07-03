package org.smartjobs.core.entities;

public record RedeemableCoupon(int value, boolean applied, boolean expired) {

    public boolean isValid() {
        return !applied && !expired;
    }

}
