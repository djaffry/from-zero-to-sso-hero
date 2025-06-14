import type {AuthorizationServer} from 'oauth4webapi';
import * as oauth from 'oauth4webapi';
import {PkceStateStore} from './pkce-state.store';
import {mapTokensToSessionInfo} from './session-info.mapper';
import type {SessionInfo} from './session.store';
import {useSessionStore} from './session.store';
import {oidc as oidcConfig} from '../../config.json';

class OidcService {
  private readonly _RESPONSE_TYPE = 'code';
  private readonly _CLIENT = <oauth.Client>{
    client_id: oidcConfig.clientId,
    token_endpoint_auth_method: 'none', // ist public client, hat kein Secret
  };
  private _sessionStore: ReturnType<typeof useSessionStore> | null = null;
  private readonly _pkceStateStore = new PkceStateStore();
  private _authorizationServer: AuthorizationServer | null = null;

  private getSessionStore(): ReturnType<typeof useSessionStore> {
    if (!this._sessionStore) {
      this._sessionStore = useSessionStore();
    }
    return this._sessionStore;
  }

  public isOidcCallback(url: string): boolean {
    return [oidcConfig.loginRedirectUri, oidcConfig.logoutRedirectUri].some(
      (configUrl) => url?.startsWith(configUrl) === true
    );
  }

  public async completeOidcCallback(url: string): Promise<void> {
    if (this.isLoginCallback(url)) {
      return await this.completeLogin(url);
    } else if (this.isLogoutCallback(url)) {
      return this.completeLogout();
    }

    throw new Error('Unknown Callback Flow!');
  }

  public async handleOidcCallback(url: string): Promise<void> {
    if (this.isOidcCallback(url)) {
      await this.completeOidcCallback(url);
    }
  }

  public clearSession() {
    this.getSessionStore().clearSession();
    this._pkceStateStore.clearState();
  }

  public async login(): Promise<void> {
    this.clearSession();

    try {
      const authorizationServer = await this.getAuthorizationServer();
      const pkceVerifyState = this._pkceStateStore.createNewState();

      if (
        authorizationServer.code_challenge_methods_supported?.includes(
          oidcConfig.codeChallengeMethod
        ) !== true
      ) {
        throw new Error(
          `challenge method ${oidcConfig.codeChallengeMethod} must be supported!`
        );
      }

      const authorizationUrl = new URL(
        authorizationServer.authorization_endpoint!
      );
      authorizationUrl.searchParams.set('client_id', oidcConfig.clientId);
      authorizationUrl.searchParams.set(
        'code_challenge',
        await oauth.calculatePKCECodeChallenge(pkceVerifyState.codeVerifier)
      );
      authorizationUrl.searchParams.set(
        'code_challenge_method',
        oidcConfig.codeChallengeMethod
      );
      authorizationUrl.searchParams.set(
        'redirect_uri',
        oidcConfig.loginRedirectUri
      );
      authorizationUrl.searchParams.set('response_type', this._RESPONSE_TYPE);
      authorizationUrl.searchParams.set('scope', oidcConfig.scope);
      authorizationUrl.searchParams.set('state', pkceVerifyState.state);

      window.location.href = authorizationUrl.toString();
    } catch (e: unknown) {
      console.error(e);
      this.clearSession();
    }
  }

  public async logout(): Promise<void> {
    const authorizationServer = await this.getAuthorizationServer();
    const sessionInfo = this.getSessionStore().loadSession();

    const endSessionUrl = new URL(authorizationServer.end_session_endpoint!);
    endSessionUrl.searchParams.set(
      'post_logout_redirect_uri',
      oidcConfig.logoutRedirectUri
    );
    const idToken = sessionInfo?.tokens.id;
    if (idToken) {
      this.clearSession();
      endSessionUrl.searchParams.set('id_token_hint', idToken);
      window.location.href = endSessionUrl.toString();
    } else {
      this.clearSession()
      await this.returnToHome();
    }
  }

  public async loadSession(): Promise<SessionInfo | undefined> {
    const existingSession = this.getSessionStore().loadSession();
    if (!existingSession) {
      // Keine Session vorhanden
      return undefined;
    } else if (
      Date.now() <
      (existingSession.expiresAt - oidcConfig.refreshIntervalInSeconds) * 1000
    ) {
      // Session ist vorhanden und enthält einen gültigen Access-Token
      return Promise.resolve(existingSession);
    } else {
      // Session ist vorhanden und Access-Token muss erneuert werden
      return this.refreshAccessToken(existingSession);
    }
  }

  public userIsLoggedIn(): boolean {
    return this.getSessionStore().isUserLoggedIn;
  }

  public getSessionInfo(): SessionInfo | null {
    return this.getSessionStore().loadSession();
  }

  private async getAuthorizationServer(): Promise<AuthorizationServer> {
    if (this._authorizationServer == null) {
      const authority = new URL(oidcConfig.authority);
      const discoveryResponse = await oauth.discoveryRequest(authority);
      this._authorizationServer = await oauth.processDiscoveryResponse(
        authority,
        discoveryResponse
      );
    }
    return this._authorizationServer;
  }

  private isLoginCallback(url: string): boolean {
    return url?.startsWith(oidcConfig.loginRedirectUri) === true;
  }

  private async completeLogin(url: string): Promise<void> {
    try {
      const authorizationServer = await this.getAuthorizationServer();
      const state = this._pkceStateStore.getCurrentState();

      const params = oauth.validateAuthResponse(
        authorizationServer,
        this._CLIENT,
        new URL(url),
        state.state
      );
      if (oauth.isOAuth2Error(params)) {
        throw params;
      }

      const response = await oauth.authorizationCodeGrantRequest(
        authorizationServer,
        this._CLIENT,
        params,
        oidcConfig.loginRedirectUri,
        state.codeVerifier
      );
      if (oauth.parseWwwAuthenticateChallenges(response)) {
        throw response;
      }

      const result = await oauth.processAuthorizationCodeOpenIDResponse(
        authorizationServer,
        this._CLIENT,
        response
      );
      if (oauth.isOAuth2Error(result)) {
        throw result;
      }

      const sessionInfo = mapTokensToSessionInfo(
        result.access_token,
        result.refresh_token!,
        result.id_token!
      );
      console.log('successfully logged in', sessionInfo);
      this.getSessionStore().setSession(sessionInfo);
      return;
    } catch (error: unknown) {
      console.error(error);
      this.clearSession();
    } finally {
      this._pkceStateStore.clearState();
    }
  }

  private async refreshAccessToken(
    sessionInfo: SessionInfo
  ): Promise<SessionInfo> {
    try {
      const authorizationServer = await this.getAuthorizationServer();

      const response = await oauth.refreshTokenGrantRequest(
        authorizationServer,
        this._CLIENT,
        sessionInfo.tokens.refresh
      );
      if (oauth.parseWwwAuthenticateChallenges(response)) {
        throw response;
      }

      const result = await oauth.processRefreshTokenResponse(
        authorizationServer,
        this._CLIENT,
        response
      );
      if (oauth.isOAuth2Error(result)) {
        throw result;
      }

      sessionInfo = mapTokensToSessionInfo(
        result.access_token,
        result.refresh_token!,
        result.id_token!
      );
      console.log('successfully refreshed token', sessionInfo);
      this.getSessionStore().setSession(sessionInfo);
      return sessionInfo;

    } catch (e: unknown) {
      console.error(e);
      this.clearSession();
    }
    throw Error("Failed to refresh access token");
  }

  private isLogoutCallback(url: string): boolean {
    return url?.startsWith(oidcConfig.logoutRedirectUri) === true;
  }

  private async completeLogout() {
    this.clearSession();
  }

  private async returnToHome() {
    window.location.href = '/';
  }
}

export const oidcService = new OidcService();
