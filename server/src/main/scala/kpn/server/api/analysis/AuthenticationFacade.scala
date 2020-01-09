package kpn.server.api.analysis

import org.springframework.social.oauth1.OAuthToken

trait AuthenticationFacade {

  def login(user: Option[String], callbackUrl: String): OAuthToken

  def authenticated(token: OAuthToken, verifier: String): String

}
