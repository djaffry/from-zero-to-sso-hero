<template>
  <div class="user-info">
    <h2>User Information</h2>
    <div v-if="sessionInfo">
      <div class="user-details">
        <h3>User Details</h3>
        <p><strong>Username:</strong> {{ sessionInfo.user.username }}</p>
        <p><strong>Full Name:</strong> {{
            sessionInfo.user.fullName || `${sessionInfo.user.firstName} ${sessionInfo.user.familyName}`
          }}</p>
        <p><strong>Email:</strong> {{ sessionInfo.user.email }}</p>
        <p><strong>Roles:</strong> {{ sessionInfo.user.roles?.join(', ') }}</p>
      </div>

      <div class="token-info">
        <h3>Tokens</h3>
        <div class="token-section">
          <h4>Access Token</h4>
          <div class="token-display">
            <textarea readonly :value="sessionInfo.tokens.access"></textarea>
          </div>
        </div>

        <div class="token-section">
          <h4>ID Token</h4>
          <div class="token-display">
            <textarea readonly :value="sessionInfo.tokens.id"></textarea>
          </div>
        </div>

        <div class="token-section">
          <h4>Refresh Token</h4>
          <div class="token-display">
            <textarea readonly :value="sessionInfo.tokens.refresh"></textarea>
          </div>
        </div>

        <p><strong>Expires At:</strong> {{
            new Date(sessionInfo.expiresAt * 1000).toLocaleString()
          }}</p>
      </div>
    </div>
    <div v-else>
      <p>No user information available. Please log in.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue';
import {oidcService} from '../auth/oidc.service';
import type {SessionInfo} from '../auth/session.store';

const sessionInfo = computed<SessionInfo | null>(() => {
  return oidcService.getSessionInfo();
});
</script>

<style scoped>
.user-info {
  margin: 20px;
  padding: 20px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background-color: var(--color-background-soft);
}

.user-details, .token-info {
  margin-bottom: 20px;
  color: var(--color-text);
}

.token-section {
  margin-bottom: 15px;
}

.token-display {
  margin-top: 5px;
}

textarea {
  width: 100%;
  height: 80px;
  padding: 8px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background-color: var(--color-background-mute);
  color: var(--color-text);
  font-family: monospace;
  font-size: 12px;
  resize: vertical;
}

h2, h3, h4 {
  color: var(--color-heading);
}

strong {
  font-weight: bold;
  color: var(--color-heading);
}
</style>
