import {defineStore} from 'pinia';

export interface SessionInfo {
  tokens: {
    access: string;
    refresh: string;
    id: string;
  };
  expiresAt: number;
  user: {
    fullName: string;
    familyName: string;
    firstName: string;
    username: string;
    email: string;
    roles: string[];
    padIntern: number;
  };
}

export const useSessionStore = defineStore('session', {
  state: () => ({
    sessionInfo: null as SessionInfo | null
  }),
  getters: {
    isUserLoggedIn: (state) => state?.sessionInfo?.user?.username != null
  },
  actions: {
    setSession(state: SessionInfo): SessionInfo {
      localStorage.setItem('session-info', JSON.stringify(state));
      this.sessionInfo = state;
      return state;
    },
    loadSession(): SessionInfo | null {
      const stateJson = localStorage.getItem('session-info');
      if (stateJson) {
        try {
          this.sessionInfo = JSON.parse(stateJson);
        } catch (e) {
          console.error('Failed to parse session info from localStorage', e);
          this.clearSession();
        }
      }
      return this.sessionInfo;
    },
    clearSession() {
      localStorage.removeItem('session-info');
      this.sessionInfo = null;
    }
  }
});

export const getSessionInfoSync = (): SessionInfo | null => {
  try {
    const sessionInfo = localStorage.getItem('session-info');
    return JSON.parse(sessionInfo!);
  } catch (_) {
    return null;
  }
}
