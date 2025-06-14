import * as jose from 'jose';
import type {SessionInfo} from './session.store';
import {oidc as oidcConfig} from '../../config.json';

type DecodedKeycloakAccessToken = jose.JWTPayload & {
  resource_access: {
    [clientId: string]: {
      roles: string[];
    };
  };
};

export function mapTokensToSessionInfo(
  accessToken: string,
  refreshToken: string,
  idToken: string
): SessionInfo {
  const decodedAccessToken: DecodedKeycloakAccessToken =
    jose.decodeJwt(accessToken);

  let roles = undefined;
  if (
    !!decodedAccessToken?.resource_access &&
    !!decodedAccessToken.resource_access[oidcConfig.clientId]
  ) {
    roles = decodedAccessToken.resource_access[oidcConfig.clientId].roles;
  }

  return <SessionInfo>{
    user: {
      fullName: decodedAccessToken.name,
      familyName: decodedAccessToken.family_name,
      firstName: decodedAccessToken.given_name,
      username: decodedAccessToken.preferred_username,
      email: decodedAccessToken.email,
      roles: roles ?? [],
    },
    expiresAt: decodedAccessToken.exp,
    tokens: {
      access: accessToken,
      refresh: refreshToken,
      id: idToken,
    },
  };
}
