<template>
  <div class="about">
    <h1>🤑 Account Info</h1>

    <div v-if="isLoggedIn && hasViewPermission">
      <div class="new-message-form" v-if="hasEditPermission">
        <input
          v-model="newMessage"
          placeholder="Enter a new message"
          @keyup.enter="createMessage"
        />
        <button @click="createMessage" :disabled="!newMessage.trim() || isCreating">
          {{ isCreating ? 'Sending...' : 'Send' }}
        </button>
      </div>

      <div v-else class="permission-warning">
        <p>You don't have permission to add new Account Info.</p>
      </div>

      <div class="messages-container">
        <div v-if="isLoading" class="loading">
          Loading messages...
        </div>
        <div v-else-if="error" class="error">
          {{ error }}
        </div>
        <div v-else-if="messages.length === 0" class="no-messages">
          No messages found. {{ hasEditPermission ? 'Create one above!' : '' }}
        </div>
        <div v-else class="messages-list">
          <div v-for="message in messages" :key="message.id" class="message-card">
            <p>{{ message.message }}</p>
          </div>

          <div class="pagination" v-if="totalPages > 1">
            <button
              @click="changePage(currentPage - 1)"
              :disabled="currentPage === 0"
            >
              Previous
            </button>
            <span>Page {{ currentPage + 1 }} of {{ totalPages }}</span>
            <button
              @click="changePage(currentPage + 1)"
              :disabled="currentPage >= totalPages - 1"
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="isLoggedIn && !hasViewPermission" class="permission-error">
      <p>You don't have permission to view Account Info.</p>
    </div>

    <div v-else class="login-prompt">
      <p>Please log in to view and create Account Info.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import {
  type PaginatedResponse,
  type AccountInfo,
  accountInfoService
} from '../services/account-info.service.ts';
import {oidcService} from '../auth/oidc.service';
import {permissionCheckService} from '../auth/permission-check.service';

const messages = ref<AccountInfo[]>([]);
const isLoading = ref(false);
const error = ref<string | null>(null);
const newMessage = ref('');
const isCreating = ref(false);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = 10;

const isLoggedIn = computed(() => oidcService.userIsLoggedIn());
const hasViewPermission = computed(() => permissionCheckService.hasViewAccountInfoPermission());
const hasEditPermission = computed(() => permissionCheckService.hasEditAccountInfoPermission());

const fetchMessages = async () => {
  if (!isLoggedIn.value) return;

  isLoading.value = true;
  error.value = null;

  try {
    const response: PaginatedResponse<AccountInfo> = await accountInfoService.getAllAccountInfos(
      currentPage.value,
      pageSize
    );
    messages.value = response.content;
    totalPages.value = response.totalPages;
  } catch (err) {
    console.error('Failed to fetch messages:', err);
    error.value = 'Failed to load messages. Please try again later.';
  } finally {
    isLoading.value = false;
  }
};

const createMessage = async () => {
  if (!newMessage.value.trim() || isCreating.value) return;

  isCreating.value = true;

  try {
    await accountInfoService.createAccountInfo(newMessage.value);
    newMessage.value = '';
    await fetchMessages();
  } catch (err) {
    console.error('Failed to create message:', err);
    error.value = 'Failed to create message. Please try again later.';
  } finally {
    isCreating.value = false;
  }
};

const changePage = (page: number) => {
  currentPage.value = page;
  fetchMessages();
};

onMounted(() => {
  if (isLoggedIn.value) {
    fetchMessages();
  }
});
</script>

<style>
.about {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: var(--color-heading);
}

.new-message-form {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
}

.new-message-form input {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background-color: var(--color-background);
  color: var(--color-text);
}

button {
  padding: 10px 15px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.messages-container {
  margin-top: 20px;
}

.loading, .error, .no-messages {
  padding: 20px;
  text-align: center;
  border-radius: 4px;
}

.error {
  background-color: #ffebee;
  color: #c62828;
  border: 1px solid #ef9a9a;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message-card {
  padding: 15px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  background-color: var(--color-background-soft);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 10px;
}

.pagination button {
  background-color: #2196F3;
}

.pagination button:disabled {
  background-color: #cccccc;
}

.permission-warning {
  padding: 10px 15px;
  margin-bottom: 20px;
  background-color: #fff3cd;
  color: #856404;
  border: 1px solid #ffeeba;
  border-radius: 4px;
}

.permission-error {
  padding: 15px;
  margin-bottom: 20px;
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

.about {
  width: 100%;
}
</style>
