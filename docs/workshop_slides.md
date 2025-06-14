---
marp: true
theme: default
paginate: true
backgroundColor: "#FFF"
style: |
  section::before {
    content: '';
    background-image: url('https://www.cloudflight.io/app/uploads/2023/05/cloudflight_logo_rgb__rgb-logotype-blue.png');
    background-size: contain;
    background-repeat: no-repeat;
    position: absolute;
    top: 32px;
    right: 32px;
    width: 180px;
    height: 90px;
    z-index: 99;
    pointer-events: none;
    display: block;
  }
---

# From Zero to SSO Hero 🦸
## OIDC, Authorization, and Keycloak Workshop

---

# Agenda

- **Introduction** (5min)
- **OIDC & OAuth2 Fundamentals** (45min)
- **System Setup Overview** (10min)
- **Hands-on Session** (60min)
- **Bonus Challenge**: Hack the Super-Secret Backend (???)
- **Discussion & Networking**

---

# Why

> Let’s face it - most authentication setups in our company are inherited, rarely revisited, and often become siloed knowledge over time. This makes them harder to understand, improve, and modernize, potentially creating gaps in security or user experience. 🔓

---

# About This Workshop

This workshop will:
- Demystify authentication and authorization concepts
- Provide hands-on experience with Keycloak and OIDC
- Guide you through implementing secure applications

**By the end**: You'll understand how to implement and secure applications using industry-standard protocols.

---

# Part 1: Fundamentals

## Authentication 🙋
- Verifies **WHO** you are
- "Are you really who you claim to be?"
- Examples: Username/password, biometrics, MFA


## Authorization 🔑
- Determines **WHAT** you can do
- "Are you allowed to access this resource?"
- Examples: Role-based access, permissions

---

## OAuth 2.0:
・ Authorization framework for third-party apps  
・ Uses tokens (not credentials)  
・ Separates authentication from authorization  
・ Multiple "grant types" (flows)  

## OIDC (OpenID Connect)
・ Identity layer built on OAuth 2.0  
・ Adds authentication & user profile info  
・ Introduces the **ID Token**  
・ Enables Single Sign-On (SSO)

![bg right:40% 60%](https://upload.wikimedia.org/wikipedia/commons/d/d2/Oauth_logo.svg)
![bg right:40% 50%](https://avatars.githubusercontent.com/u/71971?s=200&v=4)

---

# [INTERACTIVE MOMENT]
## JWT Live Demo

Let's paste a token into [jwt.io](https://jwt.io/)
- What info can you see?
- What can't you change?

---

# JWT: Not Just JSON

![bg right:40% 90%](https://images.ctfassets.net/cdy7uua7fh8z/5U3Azt2AReuNzNuQqkRs5/9629ab9924a0212b74bee0b8fa88c295/legacy-app-auth-5.png)

JSON Web Token (JWT) consists of 3 parts:
- **Header**: Algorithm & token type
- **Payload**: Claims (data)
- **Signature**: Ensures integrity

**Why secure?**
- Signed using a secret key from IDP
- Can be verified by the client without contacting the IDP using the IDPs JSON Web Key Set (JWKS) public keys 
- Tamper evident due to signature

---

# What is Keycloak?

![bg right:40% 50%](https://upload.wikimedia.org/wikipedia/commons/2/29/Keycloak_Logo.png)

- Open-source **Identity and Access Management** by Red Hat 
- Provides SSO, User management,
  Social login, Multifactor authentication,
  Fine grained authorization, Admin console,
  REST API, ...
- in OIDC: Keycloak is an identity provider (IDP), which provides user authentication functions to other apps called relying parties (RP)

---

# Keycloak Terminology

- 🌌 **Realm**: A security domain (isolated user base)
- 📲 **Client**: An application that uses Keycloak for auth
- 🔐 **Client Roles**: Permissions specific to an application
- 🧑 **Users**: People and other Entities that can authenticate
- 👭 **Groups**: Collections of users
- 🌍 **Identity Providers**: External auth sources (Google, Facebook)

---

# OIDC Flows: Authorization Code with PKCE

![bg right:40% 90%](https://developer.okta.com/assets-jekyll/blog/oauth-authorization-code-grant-type/auth-code-pkce-7a1d783d06ede4b388db9efb13df9f4de6d0c7be80828cf6a6bcd1030e89b9c1.png)

**PKCE (Proof Key for Code Exchange)**
- Enhanced security for public clients
- Prevents authorization code interception
- Uses a code verifier and code challenge
- Recommended for all clients, especially SPAs and mobile apps

---

[![bg 75%](https://mermaid.ink/img/pako:eNp1VE1P3DAQ_SuWDxUVNN-bbHJAonxUCFVCRb1UuZhkklgkdmo70GW1_71jZ1nYBfawiifz3sx7M_GaVrIGWlANfycQFVxw1io2lILgz3DTAzmbTCcVf2aGS0HOMZ9c9fKJPHHTkdub88s5eWTK8IqPTBjyW4N6Hz0bR8I0uVJSGBD1BwlY6Q7UIyibd31x69_Aquole3if-wu0nFQFr_n7Ef87qx5clRmLxb-dnuJ_QUKP_AABihlw_RPrAUEMbzhSfSFVx_oeRAt70F1zBYm8A1ccg7IeakNKhB3PoR0TOfLZFgFft7Q7QmS3jhUk9siLYjIq0CCMJhaHD7yaS41KDqOZKSzqoLVkbu0NgIkamxGW7IPCzpHFJ3r0aHGfuZB65A4dJuw99PjA0yPfSJzGR8rfMmaW0c0THlk_4YD064Q-a36JzVcVaO2UXl8QV0qTIznahnAAK5TSoJqO7HXhBO1vTUFyDxdpnqPavtLzqrO5iKOYGfaxu_UKLMVs3YzcEZWCntBW8ZoWRk1wQgdQA7NHuraUJcXJDVDSAh9raNjUm5KWYoMw3Po_Ug4vSCWntqNFw3qNp2ms0aztx3sQvay5kWoXxGnJu5WoXohw2WrA12tqVqO9ClquDdbDlWl4a-OT6jHcGTPqwvfta69FWdO9V8nB17zu8LvsHvPUT6N0yaIY0ixmiziuq_swXzZREjZ1FoQRo5sNdo47A-pcTsLQIgsCJ83W-YfH3IvSIMvjeJlkaZ4vTuiKFkm49JZREkdhkC_yJI8SpHl2ZgReHi5SzMyyNE3yJEMEOME_54vN3W-b_2PLpWc?type=png)](https://mermaid.live/edit#pako:eNp1VE1P3DAQ_SuWDxUVNN-bbHJAonxUCFVCRb1UuZhkklgkdmo70GW1_71jZ1nYBfawiifz3sx7M_GaVrIGWlANfycQFVxw1io2lILgz3DTAzmbTCcVf2aGS0HOMZ9c9fKJPHHTkdub88s5eWTK8IqPTBjyW4N6Hz0bR8I0uVJSGBD1BwlY6Q7UIyibd31x69_Aquole3if-wu0nFQFr_n7Ef87qx5clRmLxb-dnuJ_QUKP_AABihlw_RPrAUEMbzhSfSFVx_oeRAt70F1zBYm8A1ccg7IeakNKhB3PoR0TOfLZFgFft7Q7QmS3jhUk9siLYjIq0CCMJhaHD7yaS41KDqOZKSzqoLVkbu0NgIkamxGW7IPCzpHFJ3r0aHGfuZB65A4dJuw99PjA0yPfSJzGR8rfMmaW0c0THlk_4YD064Q-a36JzVcVaO2UXl8QV0qTIznahnAAK5TSoJqO7HXhBO1vTUFyDxdpnqPavtLzqrO5iKOYGfaxu_UKLMVs3YzcEZWCntBW8ZoWRk1wQgdQA7NHuraUJcXJDVDSAh9raNjUm5KWYoMw3Po_Ug4vSCWntqNFw3qNp2ms0aztx3sQvay5kWoXxGnJu5WoXohw2WrA12tqVqO9ClquDdbDlWl4a-OT6jHcGTPqwvfta69FWdO9V8nB17zu8LvsHvPUT6N0yaIY0ixmiziuq_swXzZREjZ1FoQRo5sNdo47A-pcTsLQIgsCJ83W-YfH3IvSIMvjeJlkaZ4vTuiKFkm49JZREkdhkC_yJI8SpHl2ZgReHi5SzMyyNE3yJEMEOME_54vN3W-b_2PLpWc)

---

# [INTERACTIVE MOMENT]
## Authorization Code Flow Roleplay

*Assign participants*:  
- User  
- Frontend  
- Keycloak  
- Backend  
And walk through the sequence!

---

# Redirect URIs and Web Origins

**🔙 Redirect URIs**
- Where Keycloak sends the user after authentication
- Must be preregistered for security
- Must match exactly (including trailing slashes, KC supports wildcards)

**🎯 Web Origins (CORS)**
- Domains allowed to make AJAX requests to Keycloak
- Prevents cross site request forgery
- Manifests as CORS errors when misconfigured

---

# Part 2: System Overview

---

[![bg fit](https://mermaid.ink/img/pako:eNqdV2tzokgU_StdTM1WpgoNICgyVVtFkJm48ZEVUrOzmg8NNEipQDVNEjeV_77digKCMTN8wT59-xzuuf3ylXNjD3Ea56_jZ3cJMQGj2SIC9Pn8GbRaLfBgmTMLXI3MbzYwpqOH8YQHumFPZ8C61e_NLyxoPyDNnADDZAkeUoRTMAePe5w9XoiRS8I4AvZNgUJvE0ZXc529Hr8UeEYJruaMpoyGEUGYhg9373LPys2Z7gxQIUORt4gq6dyZP43RVL8Df4DBDbgyzIltzvLEmnIZ6mMwH3ooIiHZ0kG666I0BWMYwQBtKHwxyRXauusYruYFxJ67HK6iDUrBhlRj7mNMNKAKslDgj3U9zzmjSBM_JUxJgJH196hJSJE7Uk2obqxl68adOQDWT8s2x3TGzIbfb-2qs8cBtvmPDcamZenfzXzEie82pZjb6IWAMXWBeg2sbUrQ5qLdhI7x0UnqFaJvOKYziCVQDvmBHKAnSbPVvWarmZbzrtYNdFc1qZlp2UC_H57REs9q1Upa0frVsgrChbLqhjF9mNjDyffmGukGmNNZGmd0ykbBRwsEXbdWnxLLb1RHaa4OFXLeEfqN0ijiOaFaZUpCv1wX8UJdrId7umdZpjEzbXDDlt1kcFIai64eK0sQBhZyMSL0hZ9CF12sTprWPKvwNLo2ZpXH24NIs3l94QO7yMwc6fZwOrFuh_dWsR3vtnfQarf-zNf3HmbnRB3dnxN1vESym4OnHCWwTJHDVQ6w4EZxEEYLDrCYVWUzzynfjTkqnI3ax-VHG7Vif7KFzOmUNuuh-1z3lNPhwGjU3WXzTkyVKnfQQZXBe1McVI52imjPOUY7x2gG5hkdjqFyDkX3gYx94RPCob8Ff_2wz-XywUg2qy8GFuuLbNcIkGcEVym4Cn26rJKEzmLk5dcKdw3TdIBox27Lc-IX4IfrtfaJ2iP5Pp8SHK-Q9knodLteN2-2nkOPLDWxrSQvX0s87KjjdYOnS_ZI9_VE5_CRhRISfR_1CqW-2u8ql5QONHxhe5n6VJXN40LR9_2-5xwV_Z7bl9UGxQPkwZTeJzHcakAGUuU7dlOaZ_T8fh3wh2meS3I8F-DQ4zSCM8RzG4Q3kDW5V0az4MiS3r0WnEZ_esiH2ZosuEX0RoclMPo3jjeHkTjOgiWn-XCd0laWeJCgQQjpJrk5QU0vJDE-gjAjsbWN3AMRtchDtPuVI9uEXZgDuhKpnhtHfhgwPMNrCi8JSVLt-pp1t4OQLDOn7cab6zT02O16-dTvXnelrgqlDur2OlDpdDzXEfuqL8mi7_UEUYLc2xv9cro5ImywQ4TTREUVd7kxoRfaVtuqpAj9nqrK_Y4sd7o8t-U0SZXagiRLck_q9QVFEinPfzs3xHanJ6iqSksmy4KiSDyHdgmP99f_3b-At_8BkRd2uw?type=png)](https://mermaid.live/edit#pako:eNqdV2tzokgU_StdTM1WpgoNICgyVVtFkJm48ZEVUrOzmg8NNEipQDVNEjeV_77digKCMTN8wT59-xzuuf3ylXNjD3Ea56_jZ3cJMQGj2SIC9Pn8GbRaLfBgmTMLXI3MbzYwpqOH8YQHumFPZ8C61e_NLyxoPyDNnADDZAkeUoRTMAePe5w9XoiRS8I4AvZNgUJvE0ZXc529Hr8UeEYJruaMpoyGEUGYhg9373LPys2Z7gxQIUORt4gq6dyZP43RVL8Df4DBDbgyzIltzvLEmnIZ6mMwH3ooIiHZ0kG666I0BWMYwQBtKHwxyRXauusYruYFxJ67HK6iDUrBhlRj7mNMNKAKslDgj3U9zzmjSBM_JUxJgJH196hJSJE7Uk2obqxl68adOQDWT8s2x3TGzIbfb-2qs8cBtvmPDcamZenfzXzEie82pZjb6IWAMXWBeg2sbUrQ5qLdhI7x0UnqFaJvOKYziCVQDvmBHKAnSbPVvWarmZbzrtYNdFc1qZlp2UC_H57REs9q1Upa0frVsgrChbLqhjF9mNjDyffmGukGmNNZGmd0ykbBRwsEXbdWnxLLb1RHaa4OFXLeEfqN0ijiOaFaZUpCv1wX8UJdrId7umdZpjEzbXDDlt1kcFIai64eK0sQBhZyMSL0hZ9CF12sTprWPKvwNLo2ZpXH24NIs3l94QO7yMwc6fZwOrFuh_dWsR3vtnfQarf-zNf3HmbnRB3dnxN1vESym4OnHCWwTJHDVQ6w4EZxEEYLDrCYVWUzzynfjTkqnI3ax-VHG7Vif7KFzOmUNuuh-1z3lNPhwGjU3WXzTkyVKnfQQZXBe1McVI52imjPOUY7x2gG5hkdjqFyDkX3gYx94RPCob8Ff_2wz-XywUg2qy8GFuuLbNcIkGcEVym4Cn26rJKEzmLk5dcKdw3TdIBox27Lc-IX4IfrtfaJ2iP5Pp8SHK-Q9knodLteN2-2nkOPLDWxrSQvX0s87KjjdYOnS_ZI9_VE5_CRhRISfR_1CqW-2u8ql5QONHxhe5n6VJXN40LR9_2-5xwV_Z7bl9UGxQPkwZTeJzHcakAGUuU7dlOaZ_T8fh3wh2meS3I8F-DQ4zSCM8RzG4Q3kDW5V0az4MiS3r0WnEZ_esiH2ZosuEX0RoclMPo3jjeHkTjOgiWn-XCd0laWeJCgQQjpJrk5QU0vJDE-gjAjsbWN3AMRtchDtPuVI9uEXZgDuhKpnhtHfhgwPMNrCi8JSVLt-pp1t4OQLDOn7cab6zT02O16-dTvXnelrgqlDur2OlDpdDzXEfuqL8mi7_UEUYLc2xv9cro5ImywQ4TTREUVd7kxoRfaVtuqpAj9nqrK_Y4sd7o8t-U0SZXagiRLck_q9QVFEinPfzs3xHanJ6iqSksmy4KiSDyHdgmP99f_3b-At_8BkRd2uw)

---

# [INTERACTIVE MOMENT]
## Repository Overview

*Walkthrough*:  
- Repository Structure
- Show `readme.md` and `docs`
- Docker Compose Setup and started Containers
- Accounting Backend and Frontend are missing Implementations, look for `TODO`s

---

# Implementation Approaches

### ✅ Best Practice
- Use established libraries:
  - `keycloak.js` for JS Frontend
  - `oauth2ResourceServer` for Spring Backend
- many Benefits: Silent refresh, Token management, Standardized security, low Maintenance

### 🤕 Our Workshop Approach
- Low-level implementation with `oauth4webapi`
- Custom `SecurityFilter` in backend
- Helps understand the underlying mechanisms

---

# Endpoint Security vs. Method Security

**Endpoint Security**
- Secures entire API endpoints
- Coarse grained control
- Implemented at the web layer

**Method Security**
- Secures individual service methods
- Additional control
- Implemented at the service layer

---

# Part 3: Hands-on Session

## Your Mission

Complete the accounting service implementation:
- Set up Keycloak realm, client, roles and assign them
- Connect the frontend to Keycloak
- Implement security in the backend

**Success Criteria**: Admin can add account entries, users can view them, and interns have no access.

---

# Keycloak Setup Tasks

1. Add a new client in the e-corp realm
2. Add client roles (need to be exact, or you need to change the impl.):
   - `VIEW_ACCOUNT_INFO`
   - `EDIT_ACCOUNT_INFO`
3. Assign roles to users:
   - Admin: EDIT and VIEW roles
   - User: VIEW only
   - Intern: No roles
4. Configure client settings:
   - Valid redirect URIs
   - Web origins

---

# Backend Implementation Tasks

1. Complete the `JWTSecurityFilter`:
   - Validate tokens
   - Extract user roles
   - Set security context

2. Register the filter in `WebSecurityConfig`

3. Secure endpoints and service methods:
   - Data modification → WRITE rights
   - Data retrieval → READ rights

**TIP**: Look for `TODO` comments in the code

---

# Frontend Implementation Tasks

1. Complete the initial login request
2. Register and implement the logout callback
3. Complete session handling in `loadSession()`
4. Fix the `config.json` file

**TIP**: Look for `TODO` comments in the code

---

# 💡 Implementation Help

- All tests should pass when you're done
- If you are stuck, use the text message service as a reference
- Take a look at the `readme.md`

---

# Bonus Challenge: Hack the Super-Secret Backend

![bg right:40% 90%](https://media.giphy.com/media/115BJle6N2Av0A/giphy.gif)

- The super-secret backend runs on port `8090`
- Your goal: Successfully authenticate against its root endpoint
- Hint: It uses our Keycloak's JWKS for JWT verification

---

# Discussion & Wrap-up

- What challenges did you face?
- How would you implement this in your own projects?
- What security considerations are most important?
- How does this compare to other auth systems you've used?

---

# Extra Slides

---

# Common Pitfalls: Domain Names

**The Problem**:
- Browser uses `localhost` to access Keycloak
- Container services use internal names (e.g., `keycloak`)
- JWT issuer URL must match exactly for verification

**Solutions**:
- Configure proper issuer URL in Keycloak
- Use consistent naming across environments
- Configure token verification to handle different issuer URLs
