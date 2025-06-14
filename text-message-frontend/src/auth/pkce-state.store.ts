import * as oauth from 'oauth4webapi';

interface PkceVerifyState {
  state: string;
  codeVerifier: string;
}

export class PkceStateStore {
  private readonly _PKCE_VERIFY_STATE_KEY = 'pkce-state';

  public createNewState(): PkceVerifyState {
    const state: PkceVerifyState = {
      state: oauth.generateRandomState(),
      codeVerifier: oauth.generateRandomCodeVerifier(),
    };
    localStorage.setItem(this._PKCE_VERIFY_STATE_KEY, JSON.stringify(state));
    return state;
  }

  public getCurrentState(): PkceVerifyState {
    const state = localStorage.getItem(this._PKCE_VERIFY_STATE_KEY);
    if (!state) {
      throw new Error("State not set, invalid flow!");
    }
    return JSON.parse(state);
  }

  public clearState() {
    localStorage.removeItem(this._PKCE_VERIFY_STATE_KEY);
  }
}
