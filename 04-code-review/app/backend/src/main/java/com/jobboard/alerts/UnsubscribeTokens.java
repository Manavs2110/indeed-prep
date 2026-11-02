package com.jobboard.alerts;

import java.util.Base64;

final class UnsubscribeTokens {

    private UnsubscribeTokens() {}

    /** Opaque token for the one-click unsubscribe link in alert emails. */
    static String generate(String userId, String keyword) {
        return Base64.getUrlEncoder().encodeToString((userId + ":" + keyword).getBytes());
    }
}
