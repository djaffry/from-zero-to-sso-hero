# Discussion Questions

## Token Design and Claims

1. **Token Size vs. Role Server:**
   * What are the implications of mapping many claims and access rights directly into JWT tokens versus using a separate role server?
   * How does token size affect performance, especially with header size limitations in HTTP?
   * What's the trade-off between self-contained tokens and making additional authorization server calls?
   * When would you choose one approach over the other in real-world applications?

2. **Handling Sensitive Information in Tokens:**
   * What types of information should never be included in a JWT token?
   * How do you balance convenience (having all needed information) with security (minimizing exposure)?
   * What strategies can be used to reference sensitive data without including it directly?

3. **Token Reuse Prevention and JTI (JWT ID):**
   * What is the purpose of the JTI claim in JWT tokens and how does it help prevent token reuse attacks?
   * What are the implementation considerations for using JTI claims effectively (storage, validation, cleanup)?
   * What are the trade-offs between stateless JWT validation and maintaining a token blacklist?

## Error Handling and Security

4. **Error Verbosity and Security:**
   * When is it appropriate to return verbose errors (401, 403, 404) and when should errors be generic?
   * What are the security implications of detailed error messages for outward-facing servers?
   * How can you balance helpful error messages for legitimate users while not aiding potential attackers?
   * What information might different status codes (401, 403, 404) leak to attackers?

5. **Rate Limiting and Brute Force Protection:**
   * What strategies can be implemented to prevent brute force attacks against authentication endpoints?
   * How can rate limiting be implemented without affecting legitimate users?
   * What are the trade-offs between security and user experience when implementing these protections?

## Client Types and Token Handling

6. **Confidential vs. Public Clients:**
   * What are the key differences between confidential and public clients in OAuth2?
   * What special security considerations exist for public clients?
   * What are the implications of sharing tokens with untrusted clients?
   * When would you use a session-based approach versus a token-based approach?
   * How do you secure tokens in browser-based applications?

7. **Token Refresh Strategies:**
   * What are best practices for token refresh in different types of applications?
   * How do you handle token expiration gracefully from a UX perspective?
   * What security considerations should be taken into account when implementing refresh token rotation?

## Session Management

8. **Logout Strategies:**
   * What are the implications of simply discarding a token on the client side versus performing a proper SSO logout?
   * How do you handle logout in a distributed system with multiple services?
   * What are the security risks if logout is not properly implemented?
   * How do you ensure all sessions are terminated across devices?

9. **Session Duration and Inactivity:**
   * How do you determine appropriate token lifetimes for access and refresh tokens?
   * What factors should influence your session timeout policies?
   * How do you balance security with user experience when it comes to session duration?

## Additional Considerations

10. **Cross-Origin Resource Sharing (CORS):**
    * What CORS configurations are necessary when implementing OAuth2 in a distributed system?
    * What are common pitfalls when setting up CORS for authentication flows?
    * How do redirect URIs and web origins in Keycloak relate to CORS issues?

11. **Mobile and Native Application Considerations:**
    * How does OAuth2 implementation differ between web applications and mobile/native applications?
    * What are secure storage options for tokens on different platforms?
    * What additional security measures should be considered for mobile applications?

12. **Microservices and Token Propagation:**
    * What are best practices for propagating tokens between microservices?
    * How do you handle service-to-service authentication versus user context propagation?
    * What are the security implications of token forwarding between services?
